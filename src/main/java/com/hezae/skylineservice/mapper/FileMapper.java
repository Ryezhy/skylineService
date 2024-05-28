package com.hezae.skylineservice.mapper;

import com.hezae.skylineservice.DTO.LiteFile;
import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.User;

import java.time.LocalDateTime;
import java.util.List;
public interface FileMapper {

    /*----------------------增加------------------------*/
    public int addFile(String UUid, String filePath, String fileName, String fileType,
                       Long fileSize, String parentFolderId, int downloadCount,String fileStatus,
                       String uploadTime, String modificationTime, String accessPermissions,
                       String description, int ownerId);

    /*----------------------查询------------------------*/
        //1.以文件的角度
            //1.1全局获取
            //按文件路径、文件名、文件类型和用户id查询，精确查找
            public List<File> selectFileByPathAndFileNameAndTypeAndUserId(String filePath,String name,String type,Long userId);
                //仅返回文件UUid和文件名、文件类型,用于简要显示
                public LiteFile simplySelectFileByPathAndFileNameAndTypeAndUserId (String filePath, String name, String type, Long userId);
            //查询某个目录下的所有文件名包括关键字的文件和文件夹，模糊查找
            public List<File> selectFileByUser(String filePath,String keyword,Long userId);

            //根据文件UUid查询文件
            public File selectFileByUUid(String fileUUid);

            //1.2局部获取
            //根据文件UUid获取文件路经
            // 根据文件UUid获取文件路径
            public String selectFilePathByUUid(String fileUUid);

            // 根据文件UUid获取文件名
            public String selectFileNameByUUid(String fileUUid);

            // 根据文件UUid获取文件类型
        public String selectFileTypeByUUid(String fileUUid);

            // 根据文件UUid获取文件大小
            public Long selectFileSizeByUUid(String fileUUid);

            // 根据文件UUid获取文件上传时间
            public LocalDateTime selectUploadTimeByUUid(String fileUUid);

            // 根据文件UUid获取文件修改时间
            public LocalDateTime selectModificationTimeByUUid(String fileUUid);

            //!!!根据文件路径、文件名、及文件类型和用户Id查询文件UUid <---常用--->
            public Long selectFileUUidByPath(String filePath,String fileName,String fileType,Long userId);

         //以用户的角度
            //根据用户的id查询根目录所有文件
            public List<File> selectRootDirByUser(int userId,String username);//默认根目录路径的用户名，用户可以选择更改（先不按自定义开发）
            //根据用户的id查询某目录所有文件
            public List<File> selectFileDirByUser(int userId,String filePath);

            //查找文件，按路径作为前缀
            public List<File> selectFileByUser(int userId,String filePath);

        //查询某个目录下的所有文件夹,注意这个filePath是文件夹的父路径
            public List<File> selectFileFolderByUser(int userId, String filePath);
    /*----------------------修改------------------------*/
            //1.文件的角度
            //根据文件UUid修改信息
            public void updateFileByUUid(String UUid,String fileName, Long fileSize, String fileType, String filePath,LocalDateTime updateTime,LocalDateTime modificationTime, User owner, String description);
            //根据文件UUid修改文件名
            public void  updateFileNameByUUid(Long UUid, String fileName);
            //根据文件UUid修改文件路径
            public void  updateFilePathByUUid(Long UUid, String filePath);
            //根据文件UUid修改文件大小
            public void  updateFileSizeByUUid(Long UUid, Long fileSize);
            //根据文件UUid修改文件类型
            public void  updateFileTypeByUUid(Long UUid, String fileType);
            //根据文件UUid修改文件hash
            public void  updateFileHashByUUid(Long UUid, String fileHash);
            //根据文件UUid修改文件状态
            public void  updateFileStatusByUUid(Long UUid, String fileStatus);
            //根据文件UUid修改文件权限
            public void  updateAccessPermissionsByUUid(Long UUid, String accessPermissions);
            //根据文件UUid修改文件描述
            public void  updateDescriptionByUUid(Long UUid, String description);
            //根据文件UUid修改文件上传时间
            public void  updateUploadTimeByUUid(Long UUid, LocalDateTime uploadTime);




    /*----------------------删除------------------------*/
    //按文件名，文件类型、文件路径和用户Id删除文件
    public void deleteFileAndFileDirByUser(String filePath,String fileName,String fileType,Long userId);
    //按文件UUid删除文件
    public void  deleteFileByUUid(int UUid);


}
