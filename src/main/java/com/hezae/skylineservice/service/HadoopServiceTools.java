package com.hezae.skylineservice.service;

import com.google.common.util.concurrent.RateLimiter;
import com.hezae.skylineservice.tools.HdfsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
public class HadoopServiceTools {

    @Value("${hdfs.Path}")
    private String hdfsUri;
    @Autowired
    private HdfsUtil hdfsService;

    //创建文件夹
    public boolean createFolder(String path, String folderName) {
        return hdfsService.mkdir(path + '/' + folderName);
    }
    //注意这个文件名是文件名+'.'+文件类型
    public void deleteFile(String path, String fileName) {
        hdfsService.delete(path + '/' + fileName);
    }
    //重命名，这个路径是全路径
    public boolean rename(String oldPath, String newPath){
       return hdfsService.rename(oldPath,newPath);
    }
    //上传文件
    public boolean uploadFile(String path,String fileName,String fileType) throws RuntimeException {
        System.out.println(path);
        try {
            hdfsService.uploadFileToHdfs("D:/下载/testDir/"+path+'/'+fileName+'.'+fileType, path+'/'+fileName+'.'+fileType);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadFile(String path, OutputStream outputStream) {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", hdfsUri);
        FileSystem fs;
        FSDataInputStream inputStream;
        Path filePath = new Path(path);
        try {
            fs = FileSystem.get(conf);
            if (!fs.exists(filePath )) {
                throw new FileNotFoundException("文件未找到: "+ filePath );
            }
            FileStatus fileStatus = fs.getFileStatus(filePath);
            long fileSize = fileStatus.getLen();
            inputStream = fs.open(filePath);
            byte[] buffer = new byte[1024];
            // 初始化 RateLimiter 为每秒最多允许10MB的流量
            RateLimiter limiter = RateLimiter.create(2 * 1024 * 1024); // 注意转换为字节每秒
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                limiter.acquire(bytesRead);
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分块下载HDFS上的文件。
     *
     * @param fileName     文件名，不含扩展名。
     * @param fileType     文件类型（扩展名）。
     * @param outputStream 输出流，用于写入下载的文件内容。
     * @param startByte    开始下载的字节位置。
     * @param length       需要下载的字节数，如果为-1，则下载从startByte到文件结尾的所有内容。
     * @throws IOException 在读取文件或写入输出流时可能发生的I/O异常。
     **/
    public void downloadFileInChunks(String path,String fileName, String fileType, OutputStream outputStream, Long startByte, long length) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", hdfsUri);

        long totalBytesRead = 0; // 用于追踪总读取字节数
        Path filePath = new Path(path +'/' +fileName + "." + fileType);

        FileSystem fs;
        FSDataInputStream inputStream;

        try {
            fs = FileSystem.get(conf);

            if (fs == null) {
                log.info("fs is null");
            }
            if (!fs.exists(filePath)) {
                throw new FileNotFoundException("文件未找到: "+ filePath);
            }

            FileStatus fileStatus = fs.getFileStatus(filePath);
            long fileSize = fileStatus.getLen();
            log.info("文件大小: " + fileSize + " 字节");

            if (startByte >= fileSize) {
                throw new IllegalArgumentException("起始字节大于或等于文件大小。");
            }
            if (startByte > 0) {
                log.info("继续下载，起始字节: " + startByte + " 字节，长度: " + length + " 字节");
            }

            inputStream = fs.open(filePath);
            inputStream.seek(startByte);
            byte[] buffer = (length > 0 && length < 1024) ? new byte[(int) length] : new byte[1024];

            // 初始化 RateLimiter 为每秒最多允许10MB的流量
            RateLimiter limiter = RateLimiter.create(10 * 1024 * 1024); // 注意转换为字节每秒
            log.info("长度: " + length + " 字节");
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1 && (length == -1 || length >= bytesRead)) {
                limiter.acquire(bytesRead); // 请求读取的字节数量的令牌

                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead; // 累加已读取的字节数
                if (length != -1) {
                    length -= bytesRead;
                }
            }
            if (length < bytesRead && length > 0) { // 如果剩余长度小于读取长度，则只写入剩余长度
                outputStream.write(buffer, 0, (int) length);
                totalBytesRead += length;
            }
            log.info("还剩余: " + length + " 字节");
            log.info("已成功读取并写入: " + totalBytesRead + " 字节"); // 添加日志记录总读取字节数
        } catch (FileNotFoundException e) {
            log.warn("文件未找到: " + fileName + "." + fileType);
            throw e;
        } catch (IOException e) {
            log.error("文件下载错误: " + fileName + "." + fileType + " 错误消息: " + e.getMessage());
            throw e;
        }
        //注意，需要手动结束fs，TODD：逻辑是找到最后一个还在下载的，如果下完了，就关掉系统
        /*finally {
            // 确保 FSDataInputStream 被关闭
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭输入流时发生错误: " + e.getMessage());
                }
            }
            // 确保 FileSystem 被关闭
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    log.error("关闭文件系统时发生错误: " + e.getMessage());
                }
            }
        }*/
    }

}