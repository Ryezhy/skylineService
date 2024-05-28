package com.hezae.skylineservice.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hezae.skylineservice.model.File;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileSerializer extends JsonSerializer<File> {
    @Override
    public void serialize(File value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("file_UUid", String.valueOf(value.getFile_UUid()));
        gen.writeStringField("file_name", value.getFile_name());
        gen.writeStringField("file_type", value.getFile_type());
        gen.writeStringField("file_path", value.getFile_path());
        gen.writeStringField("file_size", String.valueOf(value.getFile_size()));
        gen.writeStringField("upload_time", String.valueOf(value.getUpload_time()));
        gen.writeEndObject();
    }
}