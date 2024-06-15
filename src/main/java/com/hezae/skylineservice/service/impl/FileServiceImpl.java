package com.hezae.skylineservice.service.impl;

import com.hezae.skylineservice.mapper.FileMapper;
import com.hezae.skylineservice.mapper.UserMapper;
import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.HadoopServiceTools;
import com.hezae.skylineservice.service.api.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    private static final String ALGORITHM = "MD5";
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HadoopServiceTools hadoopServiceTools;

    @Override
    public List<File> selectRootDirByUserId(int userId) { // 通过用户访问根目录
        List<File> files = fileMapper.selectRootDirByUser( userId);
        return files;
    }

    @Override
    public List<File> selectFileDirByUser(int userId, int parentId) { // 通过用户访问文件目录
        log.warn("selectFileDirByUser 用户id:{}", userId, parentId);
        return fileMapper.selectFileDirByUser(userId, parentId);
    }

    @Override
    public List<File> selectFileFolderByUser(String username, int parentId) {
        User user = userMapper.selectUserByUsername(username);
        return fileMapper.selectFileFolderByUser(user.getId(), parentId);
    }

    @Override
    public File createFile(int userId, File file) {
        log.warn("createFile:{}  路径：{}", file.getFile_name(), file.getFile_path());
        fileMapper.addFile(file.getFile_path(), file.getFile_name(),
                file.getFile_type(), file.getFile_size(), file.getParent_folder_id(), 0,
                0, file.getUpload_time(), file.getUpload_time(),
                file.getAccess_permissions(), file.getDescription(), userId);
        //这里缺少对Hadoop的处理
        return file;
    }

    @Override
    public File copyFile(String username, File file, String targetPath, int targetParentId) {
        try {
            //构建一个新的文件
            File newFile;
            newFile = file;
            newFile.setFile_path(targetPath);
            newFile.setParent_folder_id(targetParentId);
            //重名处理
            User user = userMapper.selectUserByUsername(username);
            String fileName = getNewFileName(user.getId(), file.getFile_name(), file.getFile_type(), file.getParent_folder_id());
            if (fileName.isEmpty()) {
                return null;
            }
            newFile.setFile_name(fileName);
            createFile(user.getId(), newFile);
            return newFile;
        } catch (Exception e) {
            log.error("复制文件错误:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteFolder(int userId, File file) {
        try {
            List<File> files = fileMapper.selectFileDirByUser(userId, file.getFile_id());
            for (File item : files) {
                if (item.getFile_type().equals("Folder")) {
                    deleteFolder(userId, item);
                } else {
                    // 删除文件
                    fileMapper.deleteFileByFileId(item.getFile_id());
                }
            }
            // 现在删除文件夹本身
            fileMapper.deleteFileByFileId(file.getFile_id());
            // 从文件系统中删除文件夹
            hadoopServiceTools.deleteFile(file.getFile_path(), file.getFile_name());
            return true;
        } catch (Exception e) {
            log.error("删除文件错误: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //删除文件
    @Override
    public boolean deleteFile(int userId, File file) {
        try {
            fileMapper.deleteFileByFileId(file.getFile_id());
            hadoopServiceTools.deleteFile(file.getFile_path(), file.getFile_name() + '.' + file.getFile_type());
            log.info("删除文件" + file.getFile_name() + "成功");
            return true;
        } catch (Exception e) {
            log.error("删除文件错误:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int renameFile(int userId, File file, String newName) {
        try {
            if (fileMapper.selectFileByUserAndFileNameAndParentFolderId(userId, newName, file.getParent_folder_id()).isEmpty()) {
                if (Objects.equals(file.getFile_type(), "Folder")) {//如果是文件夹要求修改其目录所有文件的路径
                    renameFolder(userId, file, file.getFile_path() + '/' + newName);//递归修改
                    if (hadoopServiceTools.rename(file.getFile_path() + '/' + file.getFile_name(),
                            file.getFile_path() + '/' + newName)) {
                        fileMapper.updateFileNameById(newName, file.getFile_id());
                        return 0;//修改成功
                    } else {
                        return 2;//修改失败
                    }
                } else {
                    if (hadoopServiceTools.rename(file.getFile_path() + '/' + file.getFile_name() + '.' + file.getFile_type(),
                            file.getFile_path() + '/' + newName + '.' + file.getFile_type())) {
                        fileMapper.updateFileNameById(newName, file.getFile_id());
                        log.info("修改文件" + file.getFile_name() + "成功");
                        return 0;
                    } else {
                        log.error("修改文件" + file.getFile_name() + "失败");
                        return 2;//修改失败
                    }
                }
            } else {
                log.error("重命名文件错误:{}", "新文件名与其他文件重复");
                return 1;
            }

        } catch (Exception e) {
            log.error("重命名文件错误:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //递归修改文件路径
    private void renameFolder(int userId, File file, String newPath) {
        try {
            List<File> files = fileMapper.selectFileDirByUser(userId, file.getFile_id());
            for (File item : files) {
                if (item.getFile_type().equals("Folder")) {
                    fileMapper.updateFilePathById(newPath, item.getFile_id());
                    renameFolder(userId, item, newPath + '/' + item.getFile_name());
                } else {
                    fileMapper.updateFilePathById(newPath, item.getFile_id());
                }
            }
        } catch (Exception e) {
            log.error("重命名文件错误:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean uploadFile(MultipartFile file, int userId, com.hezae.skylineservice.model.File file1,
                              Long chunkIndex, Long totalChunks) throws Exception {
        String uploadDir = "D:\\下载\\testDir" + file1.getFile_path();
        java.io.File uploadDirectory = new java.io.File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        String fileName = getNewFileName(userId, file1.getFile_name(), file1.getFile_type(), file1.getParent_folder_id());
        if (fileName.isEmpty()) {
            return false;
        }
        file1.setFile_name(fileName);
        String filePath = Paths.get(uploadDir, file1.getFile_name() + '.' + file1.getFile_type()).toString();

        // 构建保存的文件全路径
        String chunkFilePath = filePath + "_part_" + (chunkIndex + 1);

        // 将分片文件保存到本地
        try (FileOutputStream fos = new FileOutputStream(chunkFilePath)) {
            fos.write(file.getBytes());
            fos.flush();
        }
        log.info("文件分片上传成功，保存至：" + chunkFilePath);

        // 检查是否所有分片都已上传
        if (chunkIndex + 1 == totalChunks) {
            // 所有分片已上传，进行合并操作
            log.info("文件合并成功，保存至临时目录：" + filePath);
            if (mergeChunks(filePath, totalChunks, file1.getFile_hash())) {
                if (fileName.isEmpty()) {
                    return false;
                }
                file1.setFile_name(fileName);
                if (hadoopServiceTools.uploadFile( file1.getFile_path(), file1.getFile_name(), file1.getFile_type())) {
                    createFile(userId, file1);
                    //缺少更新用户容量
                    return true;
                }
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    private boolean mergeChunks(String filePath, Long totalChunks, String fileHash) throws IOException {
        if (new java.io.File(filePath).exists()) {
            // 文件存在，删除文件
            log.info("重复文件存在，删除文件");
            new java.io.File(filePath).delete();
        } else {
            log.info("文件不存在，创建文件");
        }
        try (FileOutputStream fos = new FileOutputStream(filePath, true)) {
            // 先判断文件是否存在
            for (int i = 1; i <= totalChunks; i++) {
                String chunkFilePath = filePath + "_part_" + i;
                try (FileInputStream fis = new FileInputStream(chunkFilePath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                // 删除已经合并的分片文件
                java.io.File chunkFile = new java.io.File(chunkFilePath);
                chunkFile.delete();
            }
        }

        // 计算整个文件的 MD5 值
        String finalMD5 = calculateMD5(filePath);
        log.info("文件合并完成，MD5值为：" + finalMD5);
        // 检查计算出的 MD5 值与传入的 fileHash 是否一致
        if (finalMD5.equals(fileHash)) {
            log.info("文件MD5校验成功：" + fileHash);
            return true;
        } else {
            log.info("文件MD5校验失败：" + fileHash);
            return false;
        }
    }

    private String calculateMD5(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File selectFileById(int fileId) {
        return fileMapper.selectFileById(fileId);
    }

    @Override
    //获取新的文件名
    public String getNewFileName(int userId, String fileName, String fileType, int parentFolderId) {
        String newFileName = fileName;
        //判断文件是否存在
        int i = 1;
        for (; i <= 10; i++) {
            List<File> fileList = fileMapper.selectFileByUserAndFileNameAndFileTypeAndParentFolderId(userId, newFileName, fileType, parentFolderId);
            log.info("扫描出的列表:" + fileList);
            if (fileList.isEmpty()) {
                break;
            }
            newFileName = fileName + '(' + i + ')';
        }
        if (i > 10) {
            log.error("重名处理失败");
            return "";//文件名重复太多了
        }
        log.info("扫描出的结果:" + newFileName);

        return newFileName;
    }

    @Override
    public User getUser(String username) {
        return userMapper.selectUserByUsername(username);
    }
}
