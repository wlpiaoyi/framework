package org.wlpiaoyi.framework.utils.gson.type.adapter;

import com.google.gson.*;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

public class JsonDateTypeAdapter implements GsonBuilder.JsonSerializer<Date>, JsonDeserializer<Date> {

    public JsonDateTypeAdapter(){

    }

    public static Class getType(){
        return Date.class;
    }

    public JsonElement serialize(Date timestamp, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(timestamp.getTime());
    }

    public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }
        Long time = json.getAsLong();
        return new Date(time);
    }

}
