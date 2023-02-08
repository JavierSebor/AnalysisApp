package com.mundiaurum.analysisapp.domain;

public class AnalysisFile {
    private String dateTime;
    private String uuid;
    private String itemId;
    private String fileName;
    private String state;

    public AnalysisFile(String dateTime, String uuid, String itemId, String fileName, String state) {
        this.dateTime = dateTime;
        this.uuid = uuid;
        this.itemId = itemId;
        this.fileName = fileName;
        this.state = state;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
