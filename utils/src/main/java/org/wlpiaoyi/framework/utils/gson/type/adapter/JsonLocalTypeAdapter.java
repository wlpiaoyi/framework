package org.wlpiaoyi.framework.utils.gson.type.adapter;

import com.google.gson.*;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class JsonLocalTypeAdapter  implements GsonBuilder.JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Getter
    @NonNull
    private ZoneId zoneId;

    public static Class getType(){
        return LocalDateTime.class;
    }

    public JsonLocalTypeAdapter(@Nullable ZoneId zoneId){
        if(ValueUtils.isBlank(zoneId)){
            zoneId = ZoneId.systemDefault();
        }
        this.zoneId = zoneId;
    }

    @Override
    public JsonElement serialize(LocalDateTime dateTime, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DateUtils.toTimestamp(dateTime));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }

        Long time = json.getAsLong();
        LocalDateTime dateTime = DateUtils.getLocalDateTime(time, this.zoneId);
        return dateTime;
    }
}
