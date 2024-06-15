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
        gen.writeNumberField("file_id", value.getFile_id());
        gen.writeStringField("file_name", value.getFile_name());
        gen.writeStringField("file_type", value.getFile_type());
        gen.writeStringField("file_path", value.getFile_path());
        gen.writeStringField("file_size", String.valueOf(value.getFile_size()));
        gen.writeStringField("upload_time", String.valueOf(value.getUpload_time()));
        gen.writeStringField("file_hash", value.getFile_hash());
        gen.writeNumberField("parent_folder_id", value.getParent_folder_id());
        gen.writeEndObject();
    }
}