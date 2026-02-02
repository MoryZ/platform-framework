package com.old.silence.autoconfigure.minio;

import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetBucketPolicyArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.BucketPolicyTooLargeException;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author MurrayZhang
 */
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    private final MinioClient minioClient;

    public MinioService(MinioProperties minioProperties) {
        this.minioClient = MinioClient.builder().endpoint(minioProperties.getHost())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey()).build();
    }

    /**
     * 启动SpringBoot容器的时候初始化Bucket
     * 如果没有Bucket则创建
     */
    public void createBucket(String bucket) {
        try {
            if (!bucketExists(bucket)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("创建bucket={}完成!", bucket);
                return;
            }
            log.info("bucket={}已存在！策略为：{}", bucket, getBucketPolicy(bucket));
        } catch (Exception e) {
            log.error("创建bucket:{}异常!,e:{}", bucket, e.getMessage());
        }
    }

    /**
     * 判断Bucket是否存在，true：存在，false：不存在
     */
    public boolean bucketExists(String bucket) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    }


    public String getBucketPolicy(String bucket) throws ServerException, InsufficientDataException, ErrorResponseException, BucketPolicyTooLargeException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucket).build());
    }

    /**
     * 获得所有Bucket列表
     */
    public List<Bucket> getAllBuckets() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.listBuckets();
    }

    /**
     * 根据bucket获取其相关信息
     */
    public Optional<Bucket> getBucket(String bucket) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return getAllBuckets().stream().filter(b -> b.name().equals(bucket)).findFirst();
    }

    /**
     * 根据bucket删除Bucket，true：删除成功；false：删除失败，文件或已不存在
     */
    public void removeBucket(String bucket) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucket).build());
    }


    /**
     * 判断文件是否存在
     */
    public boolean isObjectExist(String bucket, String objectName) {
        boolean exist = true;
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(objectName).build());
        } catch (Exception e) {
            log.error("[Minio工具类]>>>>判断文件是否存在,异常：", e);
            exist = false;
        }
        return exist;
    }

    /**
     * 判断文件夹是否存在
     */
    public boolean isFolderExist(String bucket, String objectName) {
        boolean exist = false;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(objectName).recursive(false).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && objectName.equals(item.objectName())) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            log.error("[Minio工具类]>>>>判断文件夹是否存在，异常：", e);
            exist = false;
        }
        return exist;
    }

    /**
     * 根据文件前置查询文件
     */
    public List<Item> getAllObjectsByPrefix(String bucket, String prefix, boolean recursive) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Item> list = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(prefix).recursive(recursive).build());
        if (objectsIterator != null) {
            for (Result<Item> o : objectsIterator) {
                Item item = o.get();
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 获取文件流
     */
    public InputStream getObject(String bucket, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(objectName).build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | NoSuchAlgorithmException |
                 IOException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 断点下载
     */
    public InputStream getObject(String bucket, String objectName, long offset, long length) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(objectName).offset(offset).length(length).build());
    }

    /**
     * 获取路径下文件列表
     */
    public Iterable<Result<Item>> listObjects(String bucket, String prefix, boolean recursive) {
        return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(prefix).recursive(recursive).build());
    }

    /**
     * 使用MultipartFile进行文件上传
     */
    public ObjectWriteResponse upload(String bucket, MultipartFile file, String objectName, String contentType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InputStream inputStream = file.getInputStream();
        return minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName)
                .contentType(contentType).stream(inputStream, inputStream.available(), -1).build());
    }

    /**
     * 图片上传
     */
    public ObjectWriteResponse uploadImage(String bucket, String imageBase64, String imageName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (!StringUtils.isEmpty(imageBase64)) {
            InputStream in = base64ToInputStream(imageBase64);
            String newName = System.currentTimeMillis() + "_" + imageName + ".jpg";
            int year = LocalDateTime.now().getYear();
            int month = LocalDateTime.now().getMonthValue();
            return upload(bucket, year + "/" + month + "/" + newName, in);

        }
        return null;
    }

    public static InputStream base64ToInputStream(String base64) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = Base64.getEncoder().encode(base64.trim().getBytes());
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            log.error("base64ToInputStream error:{}", base64);
        }
        return stream;
    }


    /**
     * 上传本地文件
     */
    public ObjectWriteResponse upload(String bucket, String objectName, String fileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucket).object(objectName).filename(fileName).build());
    }

    /**
     * 通过流上传文件
     */
    public ObjectWriteResponse upload(String bucket, String objectName, InputStream inputStream) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName).stream(inputStream, inputStream.available(), -1).build());
    }

    public ObjectWriteResponse upload(String bucket, String objectName, InputStream inputStream, long objectSize) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName).stream(inputStream, inputStream.available(), objectSize).build());
    }

    /**
     * 创建文件夹或目录
     */
    public ObjectWriteResponse createDir(String bucket, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName).stream(new ByteArrayInputStream(new byte[]{
        }), 0, -1).build());
    }

    /**
     * 获取文件信息,如果抛出异常则说明文件不存在
     */
    public String getFileStatusInfo(String bucket, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(objectName).build()).toString();
    }

    /**
     * 拷贝文件
     */
    public ObjectWriteResponse copyFile(String bucket, String objectName, String srcBucket, String srcObjectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.copyObject(CopyObjectArgs.builder().source(CopySource.builder().bucket(bucket).object(objectName).build()).bucket(srcBucket).object(srcObjectName).build());
    }

    /**
     * 删除文件
     */
    public void removeFile(String bucket, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectName).build());
    }

    /**
     * 批量删除文件
     */
    public void removeFiles(String bucket, List<String> keys) {
        List<DeleteObject> objects = new LinkedList<>();
        keys.forEach(s -> {
            objects.add(new DeleteObject(s));
            try {
                removeFile(bucket, s);
            } catch (Exception e) {
                log.error("[Minio工具类]>>>>批量删除文件，异常：", e);
            }
        });
    }

    /**
     * 获取文件外链
     */
    public String getPresignedObjectUrl(String bucket, String objectName, Integer expires) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder().expiry(expires).bucket(bucket).object(objectName).build();
        return minioClient.getPresignedObjectUrl(args);
    }

    /**
     * 获得文件外链
     */
    public String getPresignedObjectUrl(String bucket, String fileKey, String filename) {
        var objectName = fileKey + "-" + filename;
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder().bucket(bucket).object(objectName).method(Method.GET).build();
        try {
            return minioClient.getPresignedObjectUrl(args);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将URLDecoder编码转成UTF8
     */
    public String getUtf8ByURLDecoder(String str) throws UnsupportedEncodingException {
        String url = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }


}
