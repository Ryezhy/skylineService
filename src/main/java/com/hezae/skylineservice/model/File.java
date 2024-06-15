package com.hezae.skylineservice.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hezae.skylineservice.databind.FileSerializer;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import java.util.UUID;
@JsonSerialize(using = FileSerializer.class)
@Entity
@Data
@Table(name = "files")
public class File {

  //  @Serial
    //private static final long serialVersionUID = 1L;

    // uuid
    @Id
    @Column(name = "file_id", nullable = false, unique = true)
    private int file_id;
    // 文件名
    @Column(name = "file_name", nullable = false)
    private String file_name;
    // 文件大小
    @Column(name = "file_size", nullable = false)
    private Long file_size;
    // 文件路径
    @Column(name = "file_path", nullable = false)
    private String file_path;
    // 文件上传时间
    @Column(name = "upload_time")
    private String upload_time;
    // 修改时间
    @Column(name = "modification_time")
    private String modification_time;
    // 文件类型
    @Column(name = "file_type", nullable = false)
    private String file_type;
    // 文件hash
    @Column(name = "file_hash", nullable = false)
    private String file_hash;
    // 文件访问权限
    @Column(name = "access_permissions", nullable = false)
    private String access_permissions;
    // 下载次数
    @Column(name = "download_count", nullable = false)
    private Integer download_count;
    // 文件描述
    @Column(name = "description", nullable = false)
    private String description;
    // 文件拥有者
    @JoinColumn(name = "owner_id", nullable = false)
    private int owner_id;
    // 文件状态
    @Column(name = "file_status", nullable = false)
    private int file_status;
    // 父文件夹ID
    @Column(name = "parent_folder_Id", nullable = false)
    private int parent_folder_id; // 父文件夹ID是外键


    public File(String fileName,Long fileSize,String fileType,String filePath,int owner,String description){
        this.file_name = fileName;
        this.file_size = fileSize;
        this.file_type = fileType;
        this.file_path = filePath;
        this.upload_time = String.valueOf(LocalDateTime.now());
        this.modification_time = String.valueOf(LocalDateTime.now());
        this.file_status = 0;
        this.access_permissions = "public";
        this.download_count = 0;
        this.file_hash = "";
        this.owner_id = owner;
        this.description = description;
    }
    public File(int id,String fileName,Long fileSize,String fileType,String filePath,int owner,String access_permissions,
                int file_status,Integer download_count,String upload_time,String modification_time,String file_hash,
                        String description,int parent_folder_id){
        this.file_id = id;
        this.file_name = fileName;
        this.file_size = fileSize;
        this.file_type = fileType;
        this.file_path = filePath;
        this.upload_time =  upload_time;
        this.modification_time = modification_time;
        this.file_status = file_status;
        this.access_permissions = access_permissions;
        this.download_count =  download_count;
        this.file_hash = file_hash;
        this.owner_id = owner;
        this.description = description;
        this.parent_folder_id = parent_folder_id;
    }

    public File() {

    }


}
