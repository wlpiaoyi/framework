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
        if(file == null){
            throw new BusinessException("没有找到文件:" + path);
        }
        if(!file.exists()){ return null; }
        return file;
    }
    public static File loadFile(@NonNull String path){
        File file = loadPath(path);
        if(file == null){
            throw new BusinessException("没有找到文件:" + path);
        }
        if(!file.isFile()){ return null; }
        return file;
    }
    public static File isDirectory(@NonNull String path){
        File file = loadPath(path);
        if(file == null){
            throw new BusinessException("没有找到文件:" + path);
        }
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
        if(dir == null){
            throw new BusinessException("没有找到文件:" + dirPath);
        }
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
        if(file == null){
            throw new BusinessException("没有找到文件:" + filePath);
        }
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

//数据指纹================================================================>

    /** 对撞算法 **/
    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";

    /**
     *
     * @param bytes 数据
     * @param algorithm 对撞算法
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] MD(byte[] bytes, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(bytes);
        return md.digest();
    }

    /**
     *
     * @param is 数据流
     * @param algorithm 对撞算法
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static byte[] MD(InputStream is, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            md.update(buffer, 0, length);
        }
        return md.digest();
    }

    /**
     *
     * @param datas 数据
     * @param algorithm 对撞算法
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String MD(String datas, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(datas.getBytes());
        return new String(Hex.encodeHex(md.digest()));
    }

    /**
     *
     * @param file 文件路径
     * @param algorithm 对撞算法
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String MD(File file, String algorithm) throws IOException, NoSuchAlgorithmException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return new String(Hex.encodeHex(MD(is, algorithm)));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] sha256(byte[] bytes) {
        return DigestUtils.sha256(bytes);
    }
    public static String sha256Hex(String datas) {
        return DigestUtils.sha256Hex(datas);
    }
    public static byte[] sha256(InputStream is) throws IOException {
        return DigestUtils.sha256(is);
    }
    public static String sha256Hex(File file) throws IOException {
        return DigestUtils.sha256Hex(new FileInputStream(file));
    }

    public static byte[] sha384(byte[] bytes) {
        return DigestUtils.sha384(bytes);
    }
    public static String sha384Hex(String datas) {
        return DigestUtils.sha384Hex(datas);
    }
    public static byte[] sha384(InputStream is) throws IOException {
        return DigestUtils.sha384(is);
    }
    public static String sha384Hex(File file) throws IOException {
        return DigestUtils.sha384Hex(new FileInputStream(file));
    }



    public static byte[] sha512(byte[] bytes) {
        return DigestUtils.sha512(bytes);
    }
    public static String sha512Hex(String datas) {
        return DigestUtils.sha512Hex(datas);
    }
    public static byte[] sha512(InputStream is) throws IOException {
        return DigestUtils.sha512(is);
    }
    public static String sha512Hex(File file) throws IOException {
        return DigestUtils.sha512Hex(new FileInputStream(file));
    }
//数据指纹<================================================================

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
