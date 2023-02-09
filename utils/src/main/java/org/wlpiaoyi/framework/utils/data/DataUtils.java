package org.wlpiaoyi.framework.utils.data;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * collection of data tools
 */
public class DataUtils {

    public static final String USER_DIR = System.getProperty("user.dir");

//load file=====================================================================>
    public static File loadPath(@NonNull String path){
        File file = new File(path);
        if(!file.exists()){ return null; }
        return file;
    }
    public static File loadFile(@NonNull String path){
        File file = loadPath(path);
        if(!file.isFile()){ return null; }
        return file;
    }
    public static File isDirectory(@NonNull String path){
        File file = loadPath(path);
        if(!file.isDirectory()){ return null; }
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


    /**
     * 从文件读取String
     * @param PATH
     * @return
     */
    public static String readFile(String PATH){
        BufferedReader br = null;
        try {
            StringBuffer sb = new StringBuffer();
            String str;
            br = new BufferedReader(new FileReader(PATH));
            while((str = br.readLine()) != null) {
                sb.append(str);
                sb.append("\r\n");
            }
            return sb.toString();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null) {
                try {br.close();} catch (IOException e) { e.printStackTrace();}
            }
        }
        return null;
    }

    /**
     * 从String写入文件
     * @param source
     * @param PATH
     * @return
     */
    public static boolean writeFile(String source, String PATH){
        File f = new File(PATH);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(source);
            bw.flush();
            bw.close();
            return true;
        }catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

//文件指纹================================================================>
    public static String MD5(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        byte[] b = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            hex = (hex.length() == 1 ? "0" : "") + hex;
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 对一个文件获取md5值
     * @return md5串
     */
    @SneakyThrows
    public static String MD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md.digest()));
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String sha256Hex(byte[] fileData) {
        return DigestUtils.sha256Hex(fileData);
    }
    /**
     * 生成文件 SHA 指纹
     * @param	file        文件路径
     * @return  java.lang.String    文件内容 SHA 指纹
     */
    public static String sha256Hex(File file) throws IOException {
        return DigestUtils.sha256Hex(new FileInputStream(file));
    }

    public static String sha384Hex(byte[] fileData) {
        return DigestUtils.sha384Hex(fileData);
    }
    /**
     * 生成文件 SHA 指纹
     * @param	file        文件路径
     * @return  java.lang.String    文件内容 SHA 指纹
     */
    public static String sha384Hex(File file) throws IOException {
        return DigestUtils.sha384Hex(new FileInputStream(file));
    }

    public static String sha512Hex(byte[] fileData) {
        return DigestUtils.sha512Hex(fileData);
    }
    /**
     * 生成文件 SHA 指纹
     * @param	file        文件路径
     * @return  java.lang.String    文件内容 SHA 指纹
     */
    public static String sha512Hex(File file) throws IOException {
        return DigestUtils.sha512Hex(new FileInputStream(file));
    }
//文件指纹<================================================================

}
