package com.old.silence.autoconfigure.minio;

/**
 * @author MurrayZhang
 */
public class OssUploadResponse {

    private final String fileKey;
    private final String filename;
    private final int code;
    private final String error;
    private final boolean successful;

    public OssUploadResponse(String filename, boolean successful) {
        this(null, filename, successful, 200, "");
    }

    public OssUploadResponse(String fileKey, String filename, boolean successful) {
        this(fileKey, filename, successful, 200, "");
    }

    public OssUploadResponse(String filename, boolean successful, int code, String error) {
        this(null, filename, successful, code, error);
    }

    public OssUploadResponse(String fileKey, String filename, boolean successful, int code, String error) {
        this.fileKey = fileKey;
        this.filename = filename;
        this.code = code;
        this.error = error;
        this.successful = successful;
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getFilename() {
        return filename;
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
