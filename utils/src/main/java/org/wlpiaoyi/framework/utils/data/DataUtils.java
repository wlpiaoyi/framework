package org.wlpiaoyi.framework.utils.data;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
        if(!move.exists()) {
            throw new BusinessException("被移动文件不存在");
        }
        if(to.exists()) {
            throw new BusinessException("目标文件已存在");
        }
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
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md.digest()));
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对一个文件获取md5值
     * @return md5串
     */
    @SneakyThrows
    public static String MD5PLUS(File file) {
        FileInputStream fileInput = null;
        StringBuffer bufStr1 = new StringBuffer();
        StringBuffer bufStr2 = new StringBuffer();
        try {
            final long fileSize = DataUtils.getSize(file.getAbsolutePath());
            long bufSize = (fileSize / 256);
            boolean supportPlus = true;
            if(bufSize < 64){
                bufSize = 64;
            }else if(bufSize > 8192){
                bufSize = 8192;
            }
            if(fileSize <= 256){
                supportPlus = false;
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            MessageDigest md1 = MessageDigest.getInstance("MD5");
            MessageDigest md2 = MessageDigest.getInstance("MD5");
            fileInput = new FileInputStream(file);
            byte[] buffer = new byte[(int) bufSize];
            int unitLen;
            int totalLen = 0;
            long fileSize_2 = fileSize / 2;
            while ((unitLen = fileInput.read(buffer)) != -1) {
                md.update(buffer, 0, unitLen);
                totalLen += unitLen;
                if(supportPlus){
                    if(totalLen <= fileSize_2){
                        bufStr1.append(new String(buffer, 0, unitLen, "UTF-8"));
                        md1.update(buffer, 0, unitLen);
                    }else{
                        bufStr2.append(new String(buffer, 0, unitLen, "UTF-8"));
                        md2.update(buffer, 0, unitLen);
                    }
                }
            }
            fileInput.close();
            fileInput = null;
            if(!supportPlus){
                if(fileSize >= 8){
                    buffer = new byte[(int) fileSize_2];
                    fileInput = new FileInputStream(file);
                    unitLen = fileInput.read(buffer);
                    md1.update(buffer, 0, unitLen);
                    bufStr1.append(new String(buffer, 0, unitLen, "UTF-8"));
                    unitLen = fileInput.read(buffer);
                    bufStr2.append(new String(buffer, 0, unitLen, "UTF-8"));
                    md2.update(buffer, 0, unitLen);
                }else {
                    String md5 = new String(Hex.encodeHex(md.digest()));
                    return md5 + md5 + md5;
                }
            }
            return new String(Hex.encodeHex(md.digest()))
                    + new String(Hex.encodeHex(md1.digest()))
                    + new String(Hex.encodeHex(md2.digest()));
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
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

//base64转码解码================================================================>
    /**
     * 将base64字符解码
     * @param bytes
     * @return
     */
    public static byte[] base64Decode(byte[] bytes){
        return Base64.getMimeDecoder().decode(bytes);
    }
    /**
     * 将base64字符解码
     * @param buffer
     * @return
     */
    public static ByteBuffer base64Decode(ByteBuffer buffer){
        return Base64.getMimeDecoder().decode(buffer);
    }
    public static String base64Encode(String encodeStr){
        return base64Encode(encodeStr, StandardCharsets.UTF_8);
    }
    public static String base64Encode(String encodeStr, Charset charset){
        byte[] res = DataUtils.base64Encode(encodeStr.getBytes(charset));
        return new String(res, charset);
    }
    /**
     * 将bytes编码成base64
     * @param bytes
     * @return
     */
    public static byte[] base64Encode(byte[] bytes) {
        return Base64.getMimeEncoder().encode(bytes);
    }

    /**
     * 将bytes编码成base64
     * @param buffer
     * @return
     */
    public static ByteBuffer base64Encode(ByteBuffer buffer) {
        return Base64.getMimeEncoder().encode(buffer);
    }
    public static String base64Decode(String decodeStr){
        return base64Decode(decodeStr, StandardCharsets.UTF_8);
    }
    public static String base64Decode(String decodeStr, Charset charset){
        byte[] res = DataUtils.base64Decode(decodeStr.getBytes(charset));
        return new String(res, charset);
    }

//base64转码解码<================================================================

}
