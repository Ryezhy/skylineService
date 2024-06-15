package com.hezae.skylineservice.DTO;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class FileInfo {
    int file_id;
    Long start;
    Long length;
    int state;
    public FileInfo(int file_id, int start, int length, int state) {
        this.file_id = file_id;
        this.start = (long) start;
        this.length = (long) length;
        this.state = state;
    }
    public FileInfo() {
    }
}
