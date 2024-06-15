package com.hezae.skylineservice.service.api;


import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    //根据用户的id查询根目录所有文件
    List<File> selectRootDirByUserId(int userId);
    //根据用户的id和文件路径查询文件
    List<File> selectFileDirByUser(int userId,int parentId);
    //根据用户的id和文件路径和文件夹列表
    public List<File> selectFileFolderByUser(String username,int parentId);
    //根据文件的id查找文件，例如判断这个文件夹是否存在
    File selectFileById(int fileId);
    //创建文件
    File createFile(int userId, File file);
    //删除文件夹
    boolean  deleteFolder(int userId, File file);
    //删除文件
    boolean  deleteFile(int userId, File file);
    //复制文件
    File copyFile(String username, File file,String targetPath,int targetParentId);

    //文件重命名
    int renameFile(int userId, File file, String newName);

    public boolean uploadFile(MultipartFile file, int userId, com.hezae.skylineservice.model.File file1,
                              Long chunkIndex, Long totalChunks) throws Exception;
    public String getNewFileName(int userId,String fileName, String fileType,int parentFolderId);

    public User getUser(String username);
    }
