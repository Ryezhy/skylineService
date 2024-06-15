package com.hezae.skylineservice.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.stp.StpUtil;
import java.net.URLEncoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.hezae.skylineservice.DTO.FileInfo;
import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.HadoopServiceTools;
import com.hezae.skylineservice.service.api.FileService;
import com.hezae.skylineservice.service.api.UserService;
import com.hezae.skylineservice.tools.StpKit;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.SaslException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;


@Controller
@CrossOrigin
@Slf4j
public class FileApi {
    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @Autowired
    private HadoopServiceTools hadoopServiceTools;

    @PostMapping("/files")
    @SaCheckOr(
            login = {  @SaCheckLogin(type = "user"),@SaCheckLogin(type = "superUser") }
    )
    public ResponseEntity<List<com.hezae.skylineservice.model.File>> getFiles(@RequestBody File file
    , @RequestHeader("satoken") String satoken){
        List<String> keys = StpKit.getUserIdAndRole();
        List<com.hezae.skylineservice.model.File> files = fileService.selectFileDirByUser(Integer.parseInt(keys.get(0)), file.getParent_folder_id());
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
    // 创建一个数据持有者类来接收请求体中的数据

    @PostMapping("/folders")//获取文件夹列表
    @SaCheckOr(
           login = {  @SaCheckLogin(type = "user"),@SaCheckLogin(type = "superUser") }
    )
    public ResponseEntity<List<File>> getFolders(@RequestBody File file,@RequestHeader("satoken") String satoken) {
        List<String> keys = StpKit.getUserIdAndRole();
        // 在这里处理user和file对象，比如保存到数据库等
        User user = userService.selectUserById(Integer.parseInt(keys.get(0)));
        List<File> files = fileService.selectFileFolderByUser(user.getUsername(), file.getFile_id());
        // 返回响应
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/files/root")//获取根目录
    @SaCheckOr(
            login = { @SaCheckLogin(type = "user") , @SaCheckLogin(type = "superUser") }
    )
    public ResponseEntity<List<File>> getFile(@RequestHeader("satoken") String satoken){
        List<String> keys = StpKit.getUserIdAndRole();
        List<File> files = fileService.selectRootDirByUserId(Integer.parseInt(keys.get(0)));
            return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/createFolder")//创建文件夹
    @SaCheckOr(
            login = { @SaCheckLogin(type = "user") , @SaCheckLogin(type = "superUser") }
    )
    public ResponseEntity<Void> createFolder(@RequestBody File file,@RequestHeader("satoken") String satoken) {
        List<String> keys = StpKit.getUserIdAndRole();
        String fileName = fileService.getNewFileName(Integer.parseInt(keys.get(0)),file.getFile_name(),file.getFile_type(),file.getParent_folder_id());
        file.setFile_name(fileName);
        if (fileService.createFile(Integer.parseInt(keys.get(0)), file)!=null){
              if(hadoopServiceTools.createFolder(file.getFile_path(),fileName)){
                  return new ResponseEntity<>(HttpStatus.OK);
              }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @PostMapping("/deleteFile")//删除文件或文件夹
    @SaCheckOr(
            login = { @SaCheckLogin(type = "user") , @SaCheckLogin(type = "superUser") }
    )
    public ResponseEntity<Void> deleteFile(@RequestBody File file,@RequestHeader("satoken") String satoken) {
        List<String> keys = StpKit.getUserIdAndRole();
        log.info("file:{}",file);
        if (file.getFile_type().equals("Folder")){
           if (fileService.deleteFolder(Integer.parseInt(keys.get(0)), file)){
               return new ResponseEntity<>(HttpStatus.OK);
           }
        }
        else {
            if (  fileService.deleteFile(Integer.parseInt(keys.get(0)),file)){
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @PostMapping("/renameFile")//重命名
    @SaCheckOr(
            login = { @SaCheckLogin(type = "user") , @SaCheckLogin(type = "superUser") }
    )
    public ResponseEntity<String> renameFile( @RequestParam("file") String file
            ,@RequestParam("newName") String newName,@RequestHeader("satoken") String satoken
    ) throws JsonProcessingException {
        File file1 =  new ObjectMapper().readValue(file, File.class);
        List<String> keys = StpKit.getUserIdAndRole();
        int massage = fileService.renameFile(Integer.parseInt(keys.get(0)), file1, newName);
            if (massage==0){
                return new ResponseEntity<>("重命名成功", HttpStatus.OK);
            }else if ( massage==1){
                return new ResponseEntity<>("重命名失败，文件名重复", HttpStatus.BAD_REQUEST);

            }else if (massage==2){
                return new ResponseEntity<>("重命名失败，文件系统错误", HttpStatus.BAD_REQUEST);
            }
        return new ResponseEntity<>("重命名失败，未知原因", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/download")//下载文件
    @SaCheckOr(
            login = { @SaCheckLogin(type = "user") , @SaCheckLogin(type = "superUser") }
    )
    public ResponseEntity<Void> downloadHadoopFile(@RequestBody FileAndFileInfo dataHolder, @RequestHeader("satoken") String satoken,HttpServletResponse response
    ) throws IOException {
        File file = dataHolder.getFile();//文件信息
        FileInfo fileInfo = dataHolder.getFileInfo();//文件信息
        List<String> keys = StpKit.getUserIdAndRole();
        // 设置响应头，支持断点下载
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFile_name() + "." + file.getFile_type() + "\"");
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        // 使用OutputStream来接收文件内容
        OutputStream outputStream = response.getOutputStream();
        // 调用服务层方法进行文件下载
        try {
            hadoopServiceTools.downloadFileInChunks(file.getFile_path(),file.getFile_name(), file.getFile_type(), outputStream, fileInfo.getStart(), fileInfo.getLength());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/download/test")//测试下载
    public ResponseEntity<Void> downloadFileDirTest(HttpServletResponse response) throws IOException {
        // 设置响应头，支持断点下载
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+"twrp.img");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(128*1024*1024));
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        // 使用OutputStream来接收文件内容
        OutputStream outputStream = response.getOutputStream();
        // 调用服务层方法进行文件下载
        hadoopServiceTools.downloadFile("/admin/twrp.img", outputStream);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/downloadFileDir/{fileId}/{satoken}")//直链下载
    public ResponseEntity<Void> downloadHadoopFileDir(@PathVariable("fileId")String fileId,
                                                      @PathVariable("satoken") String satoken,
    HttpServletResponse response
    )  throws IOException {
        int userId = Integer.parseInt(StpKit.SuperUser.getLoginIdByToken(satoken).toString());
        log.info("userId:{}",userId);
        File file = fileService.selectFileById(Integer.parseInt(fileId));
        // 设置响应头，支持断点下载
        String encodedFileName = URLEncoder.encode(file.getFile_name()+"."+file.getFile_type(), "UTF-8");
        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
        response.setHeader("Content-Disposition", contentDisposition);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getFile_size()));
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        // 使用OutputStream来接收文件内容
        OutputStream outputStream = response.getOutputStream();
        // 调用服务层方法进行文件下载
        hadoopServiceTools.downloadFile(file.getFile_path()+"/"+file.getFile_name()+"."+file.getFile_type(), outputStream);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Data
    public static class UserAndFile {
        // 提供getter和setter方法
        private User user;// 用户信息
        private File File;

        // 必须有默认的无参构造器
        public UserAndFile() {
        }
    }

    @Data
    public static class FileAndFileInfo {
        // 提供getter和setter方法
        private File File;
        private FileInfo fileInfo;

        // 必须有默认的无参构造器
        public FileAndFileInfo() {
        }
    }

    private static final double BYTES_PER_MB = 1024 * 1024;
    private static final double RATE_LIMIT_MB_PER_SECOND = 100; // 限制为每秒 1 MB
    private static final RateLimiter rateLimiter = RateLimiter.create(RATE_LIMIT_MB_PER_SECOND * BYTES_PER_MB);

    @PostMapping("/upload")//上传文件
    @SaCheckOr(
            login = { @SaCheckLogin(type = "user") , @SaCheckLogin(type = "superUser") }
    )
    public ResponseEntity<String> uploadChunk(
            @RequestParam("uploadFile") MultipartFile file,
            @RequestParam("jsonStr") String  obj,@RequestHeader("satoken") String satoken) {
        try {
            List<String> keys = StpKit.getUserIdAndRole();
            FileAndFileInfo dataHolder = new ObjectMapper().readValue(obj,FileAndFileInfo.class);
            File file1 = dataHolder.getFile();//获取文件信息
            FileInfo fileInfo= dataHolder.getFileInfo();//获取断点续传信息
            log.info("file:{}",file1);
            if (fileService.selectFileById(file1.getParent_folder_id())==null&&file1.getParent_folder_id()!=0){//判断父文件夹是否存在
                return new ResponseEntity<>("上传的父文件夹不存在，请检查是否已经删除了这个删除目录", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(fileService.uploadFile(file,Integer.parseInt(keys.get(0)),//调用上传文件方法
                   file1,fileInfo.getStart(),fileInfo.getLength())){
                return new ResponseEntity<>("文件上传成功", HttpStatus.OK);
            }else{
                log.error("文件上传失败");
                return new ResponseEntity<>("文件上传失败", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            log.error("文件上传失败：" + e.getMessage(), e);
            return new ResponseEntity<>("文件上传失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}