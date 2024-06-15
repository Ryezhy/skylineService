package com.hezae.skylineservice.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.SystemInfo;

import java.io.IOException;

public class SystemInfoSerializer extends JsonSerializer<SystemInfo> {
    @Override
    public void serialize(SystemInfo value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("os",value.getOs());
        gen.writeStringField("totalMemory",value.getTotalMemory());
        gen.writeStringField("usedMemory",value.getUsedMemory());
        gen.writeStringField("freeMemory",value.getFreeMemory());
        gen.writeStringField("cpuCurrentFreq",value.getCpuCurrentFreq());
        gen.writeStringField("cpuMaxFreq",value.getCpuMaxFreq());
        gen.writeStringField("cpuUsage",value.getCpuUsage());
        gen.writeStringField("cpuPhysicalCore",value.getCpuPhysicalCore());
        gen.writeStringField("cpuLogicalCore",value.getCpuLogicalCore());
        gen.writeStringField("totalDisk", value.getTotalDisk());
        gen.writeStringField("freeDisk",value.getFreeDisk());
        gen.writeStringField("usedDisk",value.getUsedDisk());
        gen.writeEndObject();
    }
}
