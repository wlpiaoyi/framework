package org.wlpiaoyi.framework.utils.gson.type.adapter;

import com.google.gson.*;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class JsonLocalDateTypeAdapter implements GsonBuilder.JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {


    public static Class getType(){
        return LocalDate.class;
    }

    public JsonLocalDateTypeAdapter(){
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }
        return DateUtils.parseToLocalDate(json.getAsLong());
    }

    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DateUtils.parseToEpochDay(date));
    }
}
