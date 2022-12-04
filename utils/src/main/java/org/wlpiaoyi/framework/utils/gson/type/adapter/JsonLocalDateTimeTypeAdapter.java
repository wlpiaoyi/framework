package org.wlpiaoyi.framework.utils.gson.type.adapter;

import com.google.gson.*;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.*;

public class JsonLocalDateTimeTypeAdapter implements GsonBuilder.JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Getter
    @NonNull
    private ZoneId zoneId;

    public static Class getType(){
        return LocalDateTime.class;
    }

    public JsonLocalDateTimeTypeAdapter(@Nullable ZoneId zoneId){
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        this.zoneId = zoneId;
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        Long time = json.getAsLong();
        LocalDateTime dateTime = DateUtils.toLocalDateTime(time, this.zoneId);
        return dateTime;
    }

    @Override
    public JsonElement serialize(LocalDateTime dateTime, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DateUtils.toTimestamp(dateTime));
    }

}
