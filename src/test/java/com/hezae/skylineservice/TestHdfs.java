package com.hezae.skylineservice;

import com.hezae.skylineservice.tools.HdfsUtil;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestHdfs {

    @Autowired
    private HdfsUtil hdfsService;


    @Test
    public void testExist(){
        boolean isExist = hdfsService.checkExists("/use");
        System.out.println(isExist);
    }

    /**
     * 测试创建HDFS目录
     */
    @Test
    public void testMkdir(){
        boolean result1 = hdfsService.mkdir("/admin");
        boolean result2 = hdfsService.mkdir("/admin/我的音乐");
        System.out.println("创建结果：" + result1);
    }

    /**
     * 测试上传文件
     */
    @Test
    public void testUploadFile(){
        //测试上传三个文件"        C:/Users/26506/Downloads/test.zip

        //C:/Users/26506/IdeaProjects/skylineService/src/main/resources/test/text2.txt
        hdfsService.uploadFileToHdfs("C:/Users/26506/IdeaProjects/skylineService/src/main/resources/test/text2.txt","/admin");
    }

    /**
     * 测试列出某个目录下面的文件
     */
    @Test
    public void testListFiles(){
        List<Map<String,Object>> result = hdfsService.listFiles("/testDir",null);

        result.forEach(fileMap -> {
            fileMap.forEach((key,value) -> {
                System.out.println(key + "--" + value);
            });
            System.out.println();
        });
    }

    /**
     * 测试下载文件
     */
    @Test
    public void testDownloadFile(){
        hdfsService.downloadFileFromHdfs("/testDir/wallhaven-k7orzm.jpg","C:/Users/26506/IdeaProjects/skylineService/src/main/resources/test/test");
    }

    /**
     * 测试打开HDFS上面的文件
     */
    @Test
    public void testOpen() throws IOException {
        FSDataInputStream inputStream = hdfsService.open("/testDir/test.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while((line = reader.readLine())!=null){
            System.out.println(line);
        }

        reader.close();
    }

    /**
     * 测试打开HDFS上面的文件，并转化为Java对象
     */
    @Test
    public void testOpenWithObject() throws IOException {
        //SysUserEntity user = hdfsService.openWithObject("/testDir/b.txt", SysUserEntity.class);
        //System.out.println(user);
    }

    /**
     * 测试重命名
     */
    @Test
    public void testRename(){
        hdfsService.rename("/admin/text2.txt","/admin/test.txt");

        //再次遍历
        testListFiles();
    }

    /**
     * 测试删除文件
     */
    @Test
    public void testDelete(){
        hdfsService.delete("/testDir/text2.txt");
        //再次遍历
        testListFiles();
    }

    /**
     * 测试获取某个文件在HDFS集群的位置
     */
    @Test
    public void testGetFileBlockLocations() throws IOException {
        BlockLocation[] locations = hdfsService.getFileBlockLocations("/testDir/test.txt"); // 确保文件名正确
        if (locations != null) {
            for (BlockLocation location : locations) {
                String[] hosts = location.getHosts();
                if (hosts != null && hosts.length > 0) {
                    System.out.println("块位置：");
                    for (String host : hosts) {
                        System.out.println("  主机名: " + host);
                    }
                    System.out.println("  块偏移量: " + location.getOffset());
                    System.out.println("  块长度: " + location.getLength());
                } else {
                    System.out.println("块位置信息为空");
                }
            }
        } else {
            System.out.println("没有找到文件块位置信息");
        }
    }

}