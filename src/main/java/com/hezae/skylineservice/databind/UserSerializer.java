package com.hezae.skylineservice.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hezae.skylineservice.model.File;
import com.hezae.skylineservice.model.User;

import java.io.IOException;

public class UserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeStringField("username", value.getUsername());
        gen.writeStringField("password", value.getPassword());
        gen.writeStringField("email", value.getEmail());
        gen.writeStringField("phone", value.getPhone());
        gen.writeStringField("role", value.getRole());
        gen.writeNumberField("capacity", value.getCapacity());
        gen.writeNumberField("curren_capability", value.getCurren_capability());
        gen.writeStringField("status", value.getStatus());
        gen.writeStringField("signature", value.getSignature());
        gen.writeStringField("nickname", value.getNickname());
        gen.writeEndObject();
    }
}
