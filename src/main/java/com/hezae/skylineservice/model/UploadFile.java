package com.hezae.skylineservice.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hezae.skylineservice.databind.UploadFileSerializer;
import jakarta.persistence.*;
import lombok.Data;

@JsonSerialize(using = UploadFileSerializer.class)
@Entity
@Data
@Table(name = "uploadfiles")
public class UploadFile {
    // uuid
    @Id
    @Column(name = "file_UUid", nullable = false, unique = true)
    private String file_UUid;
    // 文件名
    @Column(name = "file_name", nullable = false)
    private String file_name;

    @Column(name = "file_start", nullable = false)
    private Long file_start;
    // 文件大小
    @Column(name = "file_length", nullable = false)
    private Long file_length;
    // 文件路径
    @Column(name = "file_path", nullable = false)
    private String file_path;
    // 文件上传时间
    @Column(name = "upload_time")
    private String upload_time;
    // 最近上传时间
    @Column(name = "last_time")
    private String last_time;
    // 文件类型
    @Column(name = "file_type", nullable = false)
    private String file_type;
    // 文件hash
    @Column(name = "file_hash", nullable = false)
    private String file_hash;
    // 文件拥有者
    @JoinColumn(name = "owner_id", nullable = false)
    private int owner_id;
    // 文件状态
    @Column(name = "file_status", nullable = false)
    private String file_status;

    //构造函数
    public UploadFile(String file_UUid, String file_name, Long file_start, Long file_length, String file_path, String upload_time, String last_time, String file_type, String file_hash, int owner_id, String file_status) {
        this.file_UUid = file_UUid;
        this.file_name = file_name;
        this.file_start = file_start;
        this.file_length = file_length;
        this.file_path = file_path;
        this.upload_time = upload_time;
        this.last_time = last_time;
    }
    public UploadFile() {
    }

}
