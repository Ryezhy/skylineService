import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//此方法为限速版的hadoop下载，为Controller
//接收数据为DataHodler 对应model里面的uers和file
@PostMapping("/download")
public ResponseEntity<Void> downloadHadoopFile(@RequestBody DataHolder dataHolder, HttpServletResponse response) {
        User user = dataHolder.getUser();
        File file = dataHolder.getFile();

        try {
        // 设置响应头信息
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFile_name() + "\"");

        // 创建 Hadoop 配置和文件系统对象
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.2.128:9000"); // 设置你的 Hadoop 集群地址
        FileSystem fs = FileSystem.get(conf);

        // 构建 HDFS 文件路径
        Path hdfsFilePath = new Path("/" + user.getUsername() + "/" + file.getFile_name() + "." + file.getFile_type());
        System.out.println(hdfsFilePath.toString());
        // 打开 HDFS 文件流
        try (FSDataInputStream inputStream = fs.open(hdfsFilePath)) {
        RateLimiter limiter = RateLimiter.create(10.00*1024*1024);

        byte[] buffer = new byte[128]; //  缓冲区
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
        limiter.acquire(bytesRead); // 限速
        response.getOutputStream().write(buffer, 0, bytesRead);
        }
        }
        } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
}