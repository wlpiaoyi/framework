package org.wlpiaoyi.framework.utils.data;

import lombok.NonNull;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
;
import java.io.File;

/**
 * collection of data tools
 */
public class DataUtils {

    public static final String USER_DIR = System.getProperty("user.dir");

//load file=====================================================================>
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
//load file<=====================================================================

    /**
     * make sure the dictionary is exists, create it if it doesn't exist
     * @param dirPath
     * @return true:dictionary does not exist, created. false:dictionary already exist, do not create
     */
    public static boolean makeDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
            return true;
        }
        return false;
    }

    /**
     * get file size
     * @param filePath
     * @return -1:file not found -2:is a folder
     */
    public static long getSize(String filePath){
        File file = new File(filePath);
        if (!file.exists()) {
            return -1;
        }
        if (!file.isFile()) {
            return -2;
        }
        return file.length();
    }

    /**
     * move file
     * @param move
     * @param to the destination folder automatically created if it does not exist
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
