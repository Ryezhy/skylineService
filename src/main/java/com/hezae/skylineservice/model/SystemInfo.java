package com.hezae.skylineservice.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hezae.skylineservice.databind.SystemInfoSerializer;
import lombok.Data;
@Data
@JsonSerialize(using = SystemInfoSerializer.class)
public class SystemInfo {
    private String os;
    private String totalMemory;
    private String freeMemory;
    private String usedMemory;
    private String cpuCurrentFreq;
    private String cpuMaxFreq;
    private String cpuUsage;
    private String cpuPhysicalCore;
    private String cpuLogicalCore;
    private String TotalDisk;
    private String FreeDisk;
    private String UsedDisk;
    //构造函数
    public SystemInfo(String os,String totalMemory,String availableMemory,String usedMemory,
                      String cpuCurrentFreq,String cpuMaxFreq,
                      String cpuUsage,String cpuPhysicalCore,String cpuLogicalCore,
                      String TotalDisk,String FreeDisk,String UsedDisk
    ){
        this.os = os;
        this.totalMemory = totalMemory;
        this.freeMemory = availableMemory;
        this.usedMemory = usedMemory;
        this.cpuCurrentFreq = cpuCurrentFreq;
        this.cpuMaxFreq = cpuMaxFreq;
        this.cpuUsage = cpuUsage;
        this.cpuPhysicalCore = cpuPhysicalCore;
        this.cpuLogicalCore = cpuLogicalCore;
        this.TotalDisk = TotalDisk;
        this.UsedDisk = UsedDisk;
        this.FreeDisk = FreeDisk;
    }

    public SystemInfo() {

    }
}
