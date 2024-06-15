package com.hezae.skylineservice.mapper;

import com.hezae.skylineservice.model.File;

import java.util.List;
public interface FileMapper {

    /*----------------------增加------------------------*/
    public int addFile(String filePath, String fileName, String fileType,
                       Long fileSize, int parentFolderId, int downloadCount,int fileStatus,
                       String uploadTime, String modificationTime, String accessPermissions,
                       String description, int ownerId);
    //根据用户的id查询根目录所有文件
    public List<File> selectRootDirByUser(int userId);//默认根目录路径的用户名，用户可以选择更改（先不按自定义开发）
    //根据用户的id查询某目录所有文件
    public List<File> selectFileDirByUser(int userId, int parentId);
    //查询是否有名字和父文件夹id的文件，用于重命名查询是否重名
    public List<File> selectFileByUserAndFileNameAndParentFolderId(int userId, String fileName,int parentId);

    public File selectFileById(int fileId);

    //查询某个目录下的所有文件夹,注意这个filePath是文件夹的父路径
    public List<File> selectFileFolderByUser(int userId, int parentId);

    public List<File> selectFileByUserAndFileNameAndFileTypeAndParentFolderId(int userId,String fileName,String fileType, int parentFolderId);
    /*----------------------删除------------------------*/
    public void deleteFileByFileId(int fileId);

    public  void updateFileNameById(String fileName, int fileId);
    public void updateFilePathById(String filePath, int fileId);

}
