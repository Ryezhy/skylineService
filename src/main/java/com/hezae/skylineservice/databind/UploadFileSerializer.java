package com.hezae.skylineservice.databind;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hezae.skylineservice.model.UploadFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class UploadFileSerializer extends JsonSerializer<UploadFile> {


    @Override
    public void serialize(UploadFile uploadFile, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("file_UUid", uploadFile.getFile_UUid());
        gen.writeStringField("file_name", uploadFile.getFile_name());
        gen.writeStringField("file_start",  String.valueOf(uploadFile.getFile_start()));
        gen.writeStringField("file_length", String.valueOf(uploadFile.getFile_length()));
        gen.writeStringField("file_path", uploadFile.getFile_path());
        gen.writeStringField("upload_time", uploadFile.getUpload_time());
        gen.writeStringField("last_time", uploadFile.getLast_time());
        gen.writeStringField("file_type", uploadFile.getFile_type());
        gen.writeStringField("file_hash", uploadFile.getFile_hash());
        gen.writeEndObject();
    }
}
