package com.hezae.skylineservice.service;

import com.google.common.util.concurrent.RateLimiter;
import com.hezae.skylineservice.model.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
public class HadoopServiceTools {

    @Value("${hdfs.Path}")
    private String hdfsUri;

    public void uploadFileInChunks(MultipartFile file, String userId, String fileName) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", hdfsUri);

        try (FileSystem fs = FileSystem.get(conf);
             FSDataOutputStream outputStream = fs.append(new Path("/user/" + userId + "/" + fileName));
             InputStream inputStream = file.getInputStream()) {

            // 因为使用了append模式，所以无需显式seek，上次的写入点即为本次的起始位置
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 分块下载HDFS上的文件。
     *
     * @param username     用户名，用于构建HDFS文件路径。
     * @param fileName     文件名，不含扩展名。
     * @param fileType     文件类型（扩展名）。
     * @param outputStream 输出流，用于写入下载的文件内容。
     * @param startByte    开始下载的字节位置。
     * @param length       需要下载的字节数，如果为-1，则下载从startByte到文件结尾的所有内容。
     * @throws IOException 在读取文件或写入输出流时可能发生的I/O异常。
     **/
    public void downloadFileInChunks(String username, String fileName, String fileType, OutputStream outputStream, Long startByte, long length) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", hdfsUri);

        long totalBytesRead = 0; // 用于追踪总读取字节数
        Path filePath = new Path("/" + username + "/" + fileName + "." + fileType);

        FileSystem fs;
        FSDataInputStream inputStream;

        try {
            fs = FileSystem.get(conf);

            if (fs == null) {
                log.info("fs is null");
            }
            if (!fs.exists(filePath)) {
                throw new FileNotFoundException("文件未找到: " + fileName + "." + fileType);
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


    private final String tempDir = System.getProperty("java.io.tmpdir");
    public void uploadChunk(MultipartFile file, UploadFile uploadFile) throws IOException {
        String tempDir = this.tempDir + File.separator + uploadFile.getFile_path();
        File tempFile = new File(tempDir, uploadFile.getFile_UUid() + ".part" + uploadFile.getFile_start());
        Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        boolean allUploaded = true;
        //当上传完所有分块，检查是否存在所有块在临时文件里面
        if(Objects.equals(uploadFile.getFile_start(), uploadFile.getFile_length())){
            for (int i = 0; i < uploadFile.getFile_length(); i++) {
                File partFile = new File(tempDir, uploadFile.getFile_name() + ".part" + i);
                if (!partFile.exists()) {
                    allUploaded = false;
                    break;
                }
            }
        }
        if (allUploaded) {
            mergeChunksAndUploadToHdfs(uploadFile.getFile_name()+'.'+uploadFile.getFile_type(), uploadFile,uploadFile.getFile_length(), tempDir);
        }
    }

    private void mergeChunksAndUploadToHdfs(String fileName, UploadFile uploadFile,Long totalChunks, String tempDir) throws IOException {
        File completeFile = new File(tempDir, fileName);

        try (BufferedOutputStream mergedStream = new BufferedOutputStream(new FileOutputStream(completeFile))) {
            for (int i = 0; i < totalChunks; i++) {
                File partFile = new File(tempDir, uploadFile.getFile_UUid() + ".part"+ i);
                Files.copy(partFile.toPath(), mergedStream);//合并
                partFile.delete();//删除临时文件块
            }
        } catch (IOException e) {
            // 处理文件操作异常
            e.printStackTrace();
            throw new IOException("Failed to merge chunks and upload to HDFS: " + e.getMessage());
        }

        // Upload the complete file to HDFS
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", hdfsUri);
        FileSystem fs = FileSystem.get(conf);

        try (InputStream inputStream = new FileInputStream(completeFile)) {
            Path hdfsPath = new Path(hdfsUri+'/'+uploadFile.getFile_path()+'/'+uploadFile.getFile_name()+'.'+uploadFile.getFile_type());
            if (fs.exists(hdfsPath)) {
                throw new IOException("File already exists in HDFS: " + fileName);
            }
            try (OutputStream outputStream = fs.create(hdfsPath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            // 处理 HDFS 操作异常
            e.printStackTrace();
            throw new IOException("Failed to upload file to HDFS: " + e.getMessage());
        } finally {
            fs.close();
        }

        completeFile.delete();
    }

}