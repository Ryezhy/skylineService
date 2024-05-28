package com.hezae.skylineservice.service.api;


import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.User;

import java.util.List;

public interface FileService {

    //根据用户的id查询根目录所有文件
    List<File> selectRootDirByUser(int userId, String username);

    List<File> selectFileDirByUser(int userId, String filePath);

    List<File> selectFileFolderByUser(int userId, String filePath, String fileName);

    //测试方法
    int test();
}
