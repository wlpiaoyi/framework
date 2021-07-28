package org.wlpiaoyi.framework.utils.gson.type.adapter;

import com.google.gson.*;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.sql.Timestamp;

public class JsonDateTypeAdapter implements GsonBuilder.JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

    public JsonDateTypeAdapter(){

    }

    public static Class getType(){
        return Timestamp.class;
    }

    public JsonElement serialize(Timestamp timestamp, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(timestamp.getTime());
    }

    public Timestamp deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }
        Long time = json.getAsLong();
        return new Timestamp(time);
    }

}
