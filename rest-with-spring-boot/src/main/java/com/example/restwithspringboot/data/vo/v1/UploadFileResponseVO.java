package com.example.restwithspringboot.data.vo.v1;

import java.io.Serializable;

public class UploadFileResponseVO implements Serializable {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private String size;

    public UploadFileResponseVO(){}

    public UploadFileResponseVO(String fileName, String fileDownloadUri, String fileType, String size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


}
