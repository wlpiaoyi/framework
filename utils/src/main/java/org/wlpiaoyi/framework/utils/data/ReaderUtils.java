package org.wlpiaoyi.framework.utils.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.NonNull;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

public class ReaderUtils {


    public static Properties loadProperties(@NonNull String path) throws IOException {
        File file = DataUtils.loadFile(path);
        if(file == null) throw new IOException("没有找到文件");
        FileInputStream fileInputStream = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(fileInputStream);
        return properties;
    }

    public static byte[] loadBytes(@NonNull String path) throws IOException {
        File file = DataUtils.loadFile(path);
        if(file == null) throw new IOException("没有找到文件");
        return Files.readAllBytes(file.toPath());
    }

    public static String loadString(@NonNull String path, Charset encoding) throws IOException {
        if(encoding == null){
            encoding = StandardCharsets.UTF_8;
        }
        return new String(loadBytes(path), encoding);
    }

    public static JsonElement loadJson(@NonNull String path, Charset encoding) throws IOException {
        Map map = ReaderUtils.loadMap(path, encoding);
        if(ValueUtils.isBlank(map)) return null;
        Gson gson = GsonBuilder.gsonDefault();
        return gson.toJsonTree(map);
    }

    public static Map loadMap(@NonNull String path, Charset encoding) throws IOException {
        String str = ReaderUtils.loadString(path, encoding);
        if(ValueUtils.isBlank(str)) return null;
        Gson gson = GsonBuilder.gsonDefault();
        return gson.fromJson(str, Map.class);
    }

    public static <T> T loadObject(@NonNull String path, Charset encoding, Class<T> clazz) throws IOException {
        JsonElement json = ReaderUtils.loadJson(path, encoding);
        if(json == null) return null;
        Gson gson = GsonBuilder.gsonDefault();
        return gson.fromJson(json, clazz);
    }

}
