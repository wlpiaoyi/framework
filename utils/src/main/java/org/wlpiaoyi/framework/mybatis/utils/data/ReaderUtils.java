package org.wlpiaoyi.framework.mybatis.utils.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.NonNull;
import lombok.Setter;
import org.wlpiaoyi.framework.mybatis.utils.ValueUtils;
import org.wlpiaoyi.framework.mybatis.utils.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

/**
 * file reading tools
 */
public class ReaderUtils extends DataUtils{

    @Setter
    private static Class resourceClass;

    public static URL getResource(Class resourceClass, String name){
        return resourceClass.getClassLoader().getResource(name);
    }

    public static URL getResource(String name){
        return ReaderUtils.resourceClass.getClassLoader().getResource(name);
    }

    /**
     * read file convert to properties
     * @param path
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(@NonNull String path) throws IOException {
        File file = loadFile(path);
        if(file == null) throw new IOException("没有找到文件");
        return ReaderUtils.loadProperties(file);
    }

    /**
     * read file convert to properties
     * @param file
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(@NonNull File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(fileInputStream);
        return properties;
    }

    /**
     * read bytes file
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] loadBytes(@NonNull String path) throws IOException {
        File file = loadFile(path);
        if(file == null) throw new IOException("没有找到文件");
        return Files.readAllBytes(file.toPath());
    }

    /**
     * read file convert to string
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String loadString(@NonNull String path, Charset encoding) throws IOException {
        if(encoding == null){
            encoding = StandardCharsets.UTF_8;
        }
        return new String(loadBytes(path), encoding);
    }

    /**
     * read file convert to Gson Element
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static JsonElement loadJson(@NonNull String path, Charset encoding) throws IOException {
        Map map = ReaderUtils.loadMap(path, encoding);
        if(ValueUtils.isBlank(map)) return null;
        Gson gson = GsonBuilder.gsonDefault();
        return gson.toJsonTree(map);
    }

    /**
     * read file convert to Map
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static Map loadMap(@NonNull String path, Charset encoding) throws IOException {
        String str = ReaderUtils.loadString(path, encoding);
        if(ValueUtils.isBlank(str)) return null;
        Gson gson = GsonBuilder.gsonDefault();
        return gson.fromJson(str, Map.class);
    }

    /**
     * read file convert to target object
     * @param path
     * @param encoding
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T loadObject(@NonNull String path, Charset encoding, Class<T> clazz) throws IOException {
        JsonElement json = ReaderUtils.loadJson(path, encoding);
        if(json == null) return null;
        Gson gson = GsonBuilder.gsonDefault();
        return gson.fromJson(json, clazz);
    }

    public static void main(String[] args) {

        System.out.println();
    }

}
