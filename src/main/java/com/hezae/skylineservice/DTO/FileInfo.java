package com.hezae.skylineservice.DTO;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class FileInfo {
    String file_UUid;
    Long start;
    Long length;
    int state;
    public FileInfo(String file_UUid, int start, int length, int state) {
        this.file_UUid = file_UUid;
        this.start = (long) start;
        this.length = (long) length;
        this.state = state;
    }
    public FileInfo() {
    }
}
