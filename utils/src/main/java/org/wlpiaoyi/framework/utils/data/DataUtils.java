package org.wlpiaoyi.framework.utils.data;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.wlpiaoyi.framework.utils.PatternUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * collection of data tools
 */
@Slf4j
public class DataUtils {

    public static final String USER_DIR = System.getProperty("user.dir");

    @SneakyThrows
    public static boolean zipData(InputStream inputStream, OutputStream outputStream){
        byte[] buf = new byte[1024];
        // ZipOutputStream类：完成文件或文件夹的压缩
        ZipOutputStream out = new ZipOutputStream(outputStream);
        // 给列表中的文件单独命名
        out.putNextEntry(new ZipEntry("test.txt"));
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.closeEntry();
        out.close();
        return true;
    }


    @SneakyThrows
    public static boolean unZipData(InputStream inputStream, OutputStream outputStream){
        ZipInputStream zip = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zip.getNextEntry();
        if(zipEntry == null){
            return false;
        }
        byte[] byte_s = new byte[1024];
        int num;
        while ((num = zip.read(byte_s, 0, byte_s.length)) > 0) {
            outputStream.write(byte_s, 0, num);
        }
        return true;
    }

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
        if (dir.exists()) {
            return false;
        }
        return dir.mkdirs();
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
        return move.renameTo(to);
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
            log.error("read file error", e);
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
            log.error("write file error", e);
            return false;
        }
    }

    public static boolean writeFile(InputStream inputStream, String PATH){
        OutputStream outputStream = null;
        try {
            File file = new File(PATH);
            outputStream = Files.newOutputStream(file.toPath());
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            outputStream.close();
            return true;
        }catch(IOException e) {
            log.error("write file error", e);
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
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
            is = Files.newInputStream(file.toPath());
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

    public static byte[] sha1(byte[] bytes) {
        return DigestUtils.sha1(bytes);
    }
    public static String sha1(String datas) {
        return DigestUtils.sha1Hex(datas);
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

    /**
     * <p><b>{@code @description:}</b>
     * Hex字符串转换为byte数组
     * </p>
     *
     * <p><b>@param</b> <b>hexString</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/1/31 12:22</p>
     * <p><b>{@code @return:}</b>{@link byte[]}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static byte[] hexToBytes(String hexString) {
        if(!PatternUtils.isHexadecimal(hexString)){
            throw new IllegalArgumentException("Must be hex string");
        }
        // 确保输入字符串长度为偶数，因为每两个字符代表一个字节
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even length");
        }

        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            // 将每对字符转换为一个字节
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }
    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     * 
     * <p><b>@param</b> <b>bytes</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/1/31 12:25</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // 将每个字节转换为两个十六进制字符
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                // 如果结果只有一位，则前面补0
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

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

//    private static final char[] CHAR_ARRAY = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+`-={}|:<>?[];',.".toCharArray();
    private static final char[] CHAR_ARRAY = "!#$%&'()*+,-.0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{|}~".toCharArray();

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Base92编码
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>encodeBytes</b>
     * {@link byte}
     * 需要编码的字节数组
     * </p>
     *
     * <p><b>{@code @param}</b> <b>encodeOffset</b>
     * {@link int}
     * 待编码字节数组的起始索引
     * </p>
     *
     * <p><b>{@code @param}</b> <b>encodeLen</b>
     * {@link int}
     * 待编码字节数组的长度
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/30 19:57</p>
     * <p><b>{@code @return:}</b>
     * {@link byte[]}
     * 编码后的Base92字节数组
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static byte[] base92Encode(byte[] encodeBytes, int encodeOffset, int encodeLen) {
        if (encodeBytes == null || encodeBytes.length == 0) {
            return new byte[0];
        }
        return DataBaseXUtils.conversionToBaseX(encodeBytes, encodeOffset, encodeLen, null, CHAR_ARRAY);
    }
    public static byte[] base92Encode(byte[] encodeBytes) {
        return base92Encode(encodeBytes, -1, -1);
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Base92编码
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>encodeStr</b>
     * {@link String}
     * 需要编码的字符串
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/30 19:59</p>
     * <p><b>{@code @return:}</b>
     * {@link String}
     * 编码后的Base92字符串
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static String base92Encode(String encodeStr) {
        return new String(base92Encode(encodeStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Base92编码
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>encodeIn</b>
     * {@link InputStream}
     * 需要编码的输入流
     * </p>
     *
     * <p><b>{@code @param}</b> <b>encodeOut</b>
     * {@link OutputStream}
     * 待编码的输出流
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/30 21:51</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static void base92Encode(InputStream encodeIn, OutputStream encodeOut) throws IOException {
        byte[] bytes = new byte[100];
        try {
            int bFLen = 0;
            int bLen;
            while (true) {
                if(bFLen != 0){
                    encodeOut.write('\n');
                    bLen = encodeIn.read(bytes, bFLen,99);
                }else{
                    bLen = encodeIn.read(bytes);
                }
                if(bLen < 0) bLen = 0;
                byte[] encodeBytes = base92Encode(bytes, 0, bFLen + bLen);
                encodeOut.write(encodeBytes);

                if(bLen == 0) break;
                bFLen = encodeIn.read(bytes,0, 1);
                if(bFLen <= 0) break;

                switch (encodeBytes.length){
                    case 123:{} break;
                    case 122:{
                        encodeOut.write('/');
                    } break;
                    default: throw new RuntimeException("编码错误");
                }
            }
        }finally {
            try {
                encodeOut.flush();
            } catch (IOException e) {}
            try {
                encodeIn.close();
            }catch (IOException e){}
            try {
                encodeOut.close();
            }catch (IOException e){}
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Base92解码
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>decodeBytes</b>
     * {@link byte}
     * 需要解码的字节数组
     * </p>
     *
     * <p><b>{@code @param}</b> <b>decodeOffset</b>
     * {@link int}
     * 待解码字节数组的起始索引
     * </p>
     * <p><b>{@code @param}</b> <b>decodeLen</b>
     * {@link int}
     * 待解码字节数组的长度
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/30 20:05</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static void base92Decode(InputStream decodeIn, OutputStream decodeOut) throws IOException {
        byte[] bytes = new byte[123];
        try {
            int rLen = 0;
            while (true){
                int i = decodeIn.read();
                if(i < 0) break;
                if(rLen == 123 || i == '/' || i == '\n'){
                    if(rLen == 0) continue;
                    byte[] decodeBytes = base92Decode(bytes, 0, rLen);
                    decodeOut.write(decodeBytes);
                    rLen = 0;
                    if(i == '/' || i == '\n') continue;
                }
                bytes[rLen++] = (byte) i;
            }
            if (rLen > 0){
                byte[] decodeBytes = base92Decode(bytes, 0, rLen);
                decodeOut.write(decodeBytes);
            }
        }finally {
            try {
                decodeOut.flush();
            } catch (IOException e) {}
            try {
                decodeIn.close();
            }catch (IOException e){}
            try {
                decodeOut.close();
            }catch (IOException e){}
        }

    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Base92解码
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>decodeBytes</b>
     * {@link byte}
     * 需要解码的Base92字节数组
     * </p>
     *
     * <p><b>{@code @param}</b> <b>decodeOffset</b>
     * {@link int}
     * 待解码字节数组的起始索引
     * </p>
     *
     * <p><b>{@code @param}</b> <b>decodeLen</b>
     * {@link int}
     * 待解码字节数组的长度
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/30 19:58</p>
     * <p><b>{@code @return:}</b>
     * {@link byte[]}
     * 解码后的原始字节数组
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static byte[] base92Decode(byte[] decodeBytes, int decodeOffset, int decodeLen) {
        if (decodeBytes == null || decodeBytes.length == 0) {
            return new byte[0];
        }
        return DataBaseXUtils.conversionToBaseX(decodeBytes, decodeOffset, decodeLen, CHAR_ARRAY, null);
    }

    public static byte[] base92Decode(byte[] decodeBytes) {
        return base92Decode(decodeBytes, -1, -1);
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Base92解码
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>decodeStr</b>
     * {@link String}
     * 需要解码的Base92字符串
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/30 19:59</p>
     * <p><b>{@code @return:}</b>
     * {@link String}
     * 解码后的原始字符串
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static String base92Decode(String decodeStr) {
        return new String(base92Decode(decodeStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

}
