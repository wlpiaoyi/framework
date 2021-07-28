package org.wlpiaoyi.framework.utils.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.gson.type.adapter.*;

import java.lang.reflect.Method;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class GsonBuilder  {
    public interface JsonSerializer<T> extends com.google.gson.JsonSerializer<T> {
        static Class getType(){
            return null;
        }
    }

    @NonNull
    protected Gson gson;

    @NonNull
    private List<JsonSerializer> jsonSerializers;

    @NonNull
    private List<TypeAdapterFactory> factories;

    protected static GsonBuilder xGsonBuilder;

    public static final GsonBuilder instance(){
        return new GsonBuilder();
    }

    public static final Gson gsonDefault(){
        return GsonBuilder.singleInstance().gson;
    }

    protected static final GsonBuilder singleInstance(){
        if(xGsonBuilder != null) return xGsonBuilder;
        synchronized (GsonBuilder.class){
            if(xGsonBuilder == null){
                xGsonBuilder = GsonBuilder.instance();
            }
        }
        return xGsonBuilder;
    }

    private GsonBuilder(){
        this.factories = new ArrayList<>();
        this.jsonSerializers = new ArrayList<>();
        this.resetDefault();
    }


    protected GsonBuilder resetDefault(){
        this.clearJsonSerializers()
                .clearFactories()
                .addJsonSerializer(new JsonDateTypeAdapter())
                .addJsonSerializer(new JsonLocalTimeTypeAdapter())
                .addJsonSerializer(new JsonLocalDateTypeAdapter())
                .addJsonSerializer(new JsonLocalDateTimeTypeAdapter(ZoneId.systemDefault()));
        this.gson = this.createGson();
        return this;
    }

    public final GsonBuilder clearJsonSerializers(){
        this.jsonSerializers.clear();
        return this;
    }

    public final GsonBuilder clearFactories(){
        this.factories.clear();
        return this;
    }

    public final GsonBuilder addJsonSerializer(JsonSerializer jsonSerializer){
        if(jsonSerializer == null) return this;
        if(this.jsonSerializers.contains(jsonSerializer)) return this;
        this.jsonSerializers.add(jsonSerializer);
        return this;
    }

    public final GsonBuilder addFactories(TypeAdapterFactory factory){
        if(this.factories.contains(factory)) return this;
        this.factories.add(factory);
        return this;
    }

    @SneakyThrows
    protected final Gson createGson(){
        ExclusionStrategy myExclusionStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                Expose clazz = fieldAttributes.getAnnotation(Expose.class);
                if(clazz == null) return false;
                return true;
            }
            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };

        com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder()
                .setExclusionStrategies(myExclusionStrategy);

        for (TypeAdapterFactory factory : this.factories) {
            gsonBuilder.registerTypeAdapterFactory(factory);
        }
        for (JsonSerializer jsonSerializer : this.jsonSerializers) {
            Method method = jsonSerializer.getClass().getDeclaredMethod("getType", null);
            Class clazz = (Class) method.invoke(null);
            gsonBuilder.registerTypeAdapter(clazz, jsonSerializer);
        }
        Gson gson = gsonBuilder.create();
        return gson;
    }

}
