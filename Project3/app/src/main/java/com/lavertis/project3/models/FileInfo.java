package com.lavertis.project3.models;

public class FileInfo {
    private Integer size;
    private String type;

    public FileInfo(Integer size, String type) {
        this.size = size;
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
