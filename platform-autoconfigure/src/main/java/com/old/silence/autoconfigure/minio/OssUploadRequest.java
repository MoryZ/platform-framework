package com.old.silence.autoconfigure.minio;

/**
 * @author MurrayZhang
 */
public class OssUploadRequest {

    private final String fileKey;
    private final String filename;
    private final Object content;

    public OssUploadRequest(String fileKey, Object content) {
        this(null, fileKey, content);
    }

    public OssUploadRequest(String fileKey, String filename, Object content) {
        this.fileKey = fileKey;
        this.filename = filename;
        this.content = content;
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getFilename() {
        return filename;
    }

    public Object getContent() {
        return content;
    }
}
