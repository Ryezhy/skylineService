package com.hezae.skylineservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/hadoop-proxy")
public class HadoopProxyController {
    @Value("${hdfs.path}")
    private  String hadoopApiUrl ;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]>  downloadFile(@PathVariable("fileId") String fileId) {
        // 这里可以进行权限验证等操作
        HttpHeaders headers = new HttpHeaders();
        return restTemplate.exchange(hadoopApiUrl + "/download/" + fileId, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
    }

    // 添加其他需要的方法，比如上传文件和删除文件等
}
