package org.wlpiaoyi.framework.utils.data;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ReaderUtils {

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

}
