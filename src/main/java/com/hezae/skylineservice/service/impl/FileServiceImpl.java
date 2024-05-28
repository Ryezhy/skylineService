package com.hezae.skylineservice.service.impl;

import com.hezae.skylineservice.mapper.FileMapper;
import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.api.FileService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileMapper fileMapper;
    @Override
    public List<File> selectRootDirByUser(int userId, String username) { // 通过用户访问根目录
        log.warn("selectRootDirByUser 访问根文件，访问用户名:{} 访问用户id:{}",userId,username);
        String rootDir = '/'+username;
        List<File> files = fileMapper.selectRootDirByUser(userId,rootDir);
        return files;
    }

    @Override
    public  List<File> selectFileDirByUser(int userId,String filePath) { // 通过用户访问文件目录
        log.warn("selectFileDirByUser 访问文件路径:{} 访问用户:{}",filePath,userId);
        return fileMapper.selectFileDirByUser(userId,filePath);
    }

    @Override
    public List<File> selectFileFolderByUser(int userId, String filePath, String fileName) {
        log.warn("selectFileFolderByUser 访问文件夹目录下的所有文件:{} 访问用户:{}",filePath+fileName,userId);
        if (Objects.equals(fileName, "")){
            return fileMapper.selectFileFolderByUser(userId,filePath+fileName);
        }
        return fileMapper.selectFileFolderByUser(userId,filePath+"/"+fileName);
    }

    @Override
    public int test(){
        String CHINESE_CHARACTERS = "啊都黑科技啊傻瓜好卡是能够卡到v卡的呢flag确认41"; // 示例中文字符集
        final String OTHER_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // 示例其他字符集
        final String ALL_CHARACTERS = CHINESE_CHARACTERS + OTHER_CHARACTERS;
        final int MIN_LENGTH = 3;
         final int MAX_LENGTH = 6;
        final Random RANDOM = new Random();

        int length = MIN_LENGTH + RANDOM.nextInt(MAX_LENGTH - MIN_LENGTH + 1); // 生成3到6之间的随机长度
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
                int index = RANDOM.nextInt(ALL_CHARACTERS.length());
                char randomChar = ALL_CHARACTERS.charAt(index);
                sb.append(randomChar);
        }

        int size = 100 + RANDOM .nextInt(2901);

        String fileType = switch (RANDOM.nextInt(14)) {
            case 0 -> "Folder";
            case 1 -> "mp4";
            case 2 -> "mp3";
            case 3 -> "txt";
            case 4 -> "pdf";
            case 5 -> "docx";
            case 6 -> "xml";
            case 7 -> "png";
            case 8 -> "zip";
            case 9 -> "mp4";
            case 10 -> "mp4";
            case 11 -> "flac";
            case 12 -> "mp3";
            case 13 -> "mp4";
            default -> "";
        };
        File file = new File(sb.toString(), (long) size, fileType, "/", 1, sb.toString());
        return fileMapper.addFile(file.getFile_UUid(), file.getFile_path(), file.getFile_name(), file.getFile_type(),
                file.getFile_size(), String.valueOf(file.getParent_folderId()), file.getDownload_count(),
                file.getFile_status(), file.getUpload_time(), file.getModification_time(),
                file.getAccess_permissions(), file.getDescription(), file.getOwner_id());
    }

    //获取用户的根目录

}
