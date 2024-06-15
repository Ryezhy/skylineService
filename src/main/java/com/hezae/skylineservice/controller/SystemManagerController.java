package com.hezae.skylineservice.controller;

import cn.hutool.system.oshi.OshiUtil;
import com.hezae.skylineservice.model.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;

@RestController
@CrossOrigin
@Slf4j
public class SystemManagerController {

    @GetMapping("/systemInfo")
    public ResponseEntity<com.hezae.skylineservice.model.SystemInfo> getSystemMemoryInfo() {
        //获取系统信息
        String os = String.valueOf(OshiUtil.getOs());
        String totalMemory = format(OshiUtil.getMemory().getTotal());
        String freeMemory = format(OshiUtil.getMemory().getAvailable());
        String usedMemory = format(OshiUtil.getMemory().getTotal() - OshiUtil.getMemory().getAvailable());
        String cpuCurrentFreq = Arrays.toString(OshiUtil.getProcessor().getCurrentFreq());
        String cpuMaxFreq = String.valueOf(OshiUtil.getProcessor().getMaxFreq());
        String cpuPhysicalCore = String.valueOf(OshiUtil.getProcessor().getPhysicalProcessorCount());
        String cpuLogicalCore = String.valueOf(OshiUtil.getProcessor().getLogicalProcessorCount());
        double free = OshiUtil.getCpuInfo().getFree();
        String cpuUsage = String.format("%.2f",(100 - free));
        File win = new File("/");
        String totalDisk ="error";
        String freeDisk = "error";
        String usedDisk = "error";
        if (win.exists()){
            totalDisk = String.valueOf(win.getTotalSpace());
            freeDisk = String.valueOf(win.getFreeSpace());
            usedDisk = String.valueOf(win.getTotalSpace() - win.getFreeSpace());
        }
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setOs(os);
        systemInfo.setTotalMemory(totalMemory);
        systemInfo.setFreeMemory(freeMemory);
        systemInfo.setUsedMemory(usedMemory);
        systemInfo.setCpuCurrentFreq(cpuCurrentFreq);
        systemInfo.setCpuMaxFreq(cpuMaxFreq);
        systemInfo.setCpuUsage(cpuUsage);
        systemInfo.setCpuPhysicalCore(cpuPhysicalCore);
        systemInfo.setCpuLogicalCore(cpuLogicalCore);
        systemInfo.setTotalDisk(totalDisk);
        systemInfo.setFreeDisk(freeDisk);
        systemInfo.setUsedDisk(usedDisk);
        return ResponseEntity.ok(systemInfo);
    }

    private String format(Long memory) {
        if (memory > 1024 * 1024 * 1024) {
            return memory / 1024 / 1024 / 1024 + "";
        } else if (memory > 1024 * 1024) {
            return memory / 1024 / 1024 + "";
        } else {
            return memory / 1024 + "";
        }
    }

}