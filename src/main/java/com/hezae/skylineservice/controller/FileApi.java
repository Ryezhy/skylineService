package com.hezae.skylineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.hezae.skylineservice.DTO.FileInfo;
import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.UploadFile;
import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.HadoopServiceTools;
import com.hezae.skylineservice.service.api.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Controller
@CrossOrigin
@Slf4j
public class FileApi {
    @Autowired
    private FileService fileService;

    @Autowired
    private HadoopServiceTools hadoopServiceTools;

    @PostMapping("/files")
    public ResponseEntity<List< com.hezae.skylineservice.model.File>> getFiles(@RequestBody UserAndFileAndFileInfo dataHolder) {
        User user = dataHolder.getUser();
        com.hezae.skylineservice.model.File file = dataHolder.getFile();

        // 在这里处理user和file对象，比如保存到数据库等
        List< com.hezae.skylineservice.model.File> files = fileService.selectFileDirByUser(1, file.getFile_path() + file.getFile_name());
        log.warn("user:{},file:{}", user, file);
        // 返回响应
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
    // 创建一个数据持有者类来接收请求体中的数据

    @PostMapping("/folders")
    public ResponseEntity<List<File>> getFolders(@RequestBody UserAndFileAndFileInfo dataHolder) {
        User user = dataHolder.getUser();
        File file = dataHolder.getFile();
        // 在这里处理user和file对象，比如保存到数据库等
        List<File> files = fileService.selectFileFolderByUser(1, file.getFile_path(), file.getFile_name());
        log.warn("user:{},file:{}", user, files.size());
        // 返回响应
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/files/root")
    public List<File> getFile() {
        User owner = new User();
        return fileService.selectRootDirByUser(1, "admin");
    }

    @PostMapping("/download")
    public ResponseEntity<Void> downloadHadoopFile(@RequestBody UserAndFileAndFileInfo dataHolder, HttpServletResponse response
    ) throws IOException {
        User user = dataHolder.getUser();
        File file = dataHolder.getFile();
        FileInfo fileInfo = dataHolder.getFileInfo();
        // 设置响应头，支持断点下载
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFile_name() + "." + file.getFile_type() + "\"");
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

        // 使用OutputStream来接收文件内容
        OutputStream outputStream = response.getOutputStream();

        // 调用服务层方法进行文件下载
        hadoopServiceTools.downloadFileInChunks(user.getUsername(), file.getFile_name(), file.getFile_type(), outputStream, fileInfo.getStart(), fileInfo.getLength());

        // 根据实际情况设置响应状态码，这里默认成功
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Data
    public static class UserAndUploadFile {
        // 提供getter和setter方法
        private User user;// 用户信息
        private UploadFile uploadFile;

        // 必须有默认的无参构造器
        public UserAndUploadFile() {
        }
    }

    @Data
    public static class UserAndFileAndFileInfo {
        // 提供getter和setter方法
        private User user;// 用户信息
        private File file;// 文件信息
        private FileInfo fileInfo;//断点临时续传信息

        // 必须有默认的无参构造器
        public UserAndFileAndFileInfo() {
        }
    }

    private static final double BYTES_PER_MB = 1024 * 1024;
    private static final double RATE_LIMIT_MB_PER_SECOND = 10; // 限制为每秒 1 MB
    private static final RateLimiter rateLimiter = RateLimiter.create(RATE_LIMIT_MB_PER_SECOND * BYTES_PER_MB);

    @PostMapping("/upload")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("uploadFile") MultipartFile file,
            @RequestParam("obj") String obj) {
        try {
            // 指定绝对保存路径，根据实际情况替换
            String uploadDir = "C:\\Users\\26506\\IdeaProjects\\skylineService\\src\\main\\resources\\static"; // Windows示例
            // 或者在Linux、Mac中
            // String uploadDir = "/your/absolute/path/to/upload/directory/";

            // 确保目录存在，如果不存在则创建
            java.io.File uploadDirectory = new java.io.File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // 构建保存的文件全路径
            String filePath = Paths.get(uploadDir, file.getOriginalFilename()).toString();

            // 将文件保存到本地，限速实现
            int bufferSize = 1024; // 每次读取1KB
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                try (var inputStream = file.getInputStream()) {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        rateLimiter.acquire(bytesRead); // 限速
                        fos.write(buffer, 0, bytesRead);
                        fos.flush();
                    }
                }
            }

            log.info("文件上传成功，保存至：" + filePath);
            return new ResponseEntity<>("文件上传成功", HttpStatus.OK);
        } catch (IOException e) {
            log.error("文件上传失败：" + e.getMessage(), e);
            return new ResponseEntity<>("文件上传失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}