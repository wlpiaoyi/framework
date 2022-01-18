package org.wlpiaoyi.framework.utils.data;

import lombok.NonNull;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
;
import java.io.File;

public class DataUtils {

    public static final String basePath = System.getProperty("user.dir");

//加载文件=====================================================================>
    /**
     * 加载文件
     * @param path
     * @return
     */
    public static File loadPath(@NonNull String path){
        File file = new File(path);
        if(!file.exists()) return null;
        return file;
    }
    public static File loadFile(@NonNull String path){
        File file = loadPath(path);
        if(!file.isFile()) return null;
        return file;
    }
    public static File isDirectory(@NonNull String path){
        File file = loadPath(path);
        if(!file.isDirectory()) return null;
        return file;
    }
//加载文件<=====================================================================

    /**
     * 确保目录存在，不存在则创建
     * @param dirPath
     */
    public static void makeDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 移动文件夹
     * @param move
     * @param to 目标文件夹如果不存在则自动创建
     * @return
     */
    public static boolean moveFile(File move, File to) {
        if(!move.exists()) throw new BusinessException("被移动文件不存在");
        if(to.exists()) throw new BusinessException("目标文件已存在");
        String toPath = to.getPath();
        toPath = toPath.substring(0, toPath.lastIndexOf("/"));
        makeDir(toPath);
        move.renameTo(to);
        return true;
    }

}
