package com.hezae.skylineservice;


import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.service.api.FileService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestFile {
    @Autowired
    private FileService fileService;

    @Test
    public void test()
    {
        for (int i=0;i<1;i++)
        if( fileService.test()!=-1){
            System.out.println("test success");
        }
    }

    @Test
    public void testGetFileByUserAndDir()
    {
        List<File> files = fileService.selectRootDirByUser(1,"admin");
        System.out.println(files);
        for (File file:files){
            System.out.println(file.getFile_UUid());
        }
    }
}
