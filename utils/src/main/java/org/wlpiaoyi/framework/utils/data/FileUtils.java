package org.wlpiaoyi.framework.utils.data;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <p><b>{@code @description:}</b>
 * 文件工具,用于判断文件的真实格式
 * </p>
 * <p><b>{@code @date:}</b>         2024/1/31 11:18</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
@Slf4j
public class FileUtils {

    /**
     * <p><b>{@code @description:}</b> 
     * 获取文件输入流
     * </p>
     * 
     * <p><b>@param</b> <b>filePath</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @throws:}</b></p>
     * <p><b>{@code @date:}</b>2024/1/31 18:24</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    static String getFileContent(String filePath) throws IOException {
        InputStream inputStream = new FileInputStream(filePath);
        try{
            return getFileContent(inputStream);
        }finally {
            inputStream.close();
        }
    }
    static String getFileContent(InputStream inputStream) throws IOException {
        byte[] b = new byte[28];
        try {
            /**
             * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             *从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             * 之所以从输入流中读取20个字节数据，是因为不同格式的文件头魔数长度是不一样的，比如 EML("44656C69766572792D646174653A")和GIF("47494638")
             * 为了提高识别精度所以获取的字节数相应地长一点
             */
            inputStream.read(b, 0, b.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return bytesToHexString(b);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 将文件头转换成16进制字符串
     * </p>
     *
     * <p><b>@param</b> <b>src</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/1/31 18:23</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    private static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static final Set<String> DOCX_FILE_DIRS = new HashSet() {{
        add("_rels/");add("docProps/");add("word/");add("[Content_Types].xml");
    }};
    /**
     *
     * @param filePath
     * @return: boolean
     * @author: wlpia
     * @date: 2024/1/31 18:25
     */
    @SneakyThrows
    public static boolean isTypeDocx(String filePath){
        FileInputStream input = new FileInputStream(filePath);
        try{
            return isTypeDocx(input);
        }finally {
            input.close();
        }
    }
    @SneakyThrows
    public static boolean isTypeDocx(InputStream inputStream){
        //获取ZIP输入流(一定要指定字符集Charset.forName("GBK")否则会报java.lang.IllegalArgumentException: MALFORMED)
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream), Charset.forName("GBK"));
        try{
            //定义ZipEntry置为null,避免由于重复调用zipInputStream.getNextEntry造成的不必要的问题
            ZipEntry ze = null;
            Set<String> tempDirs = new HashSet(){{
               addAll(DOCX_FILE_DIRS);
            }};
            int i = tempDirs.size();
            //循环遍历
            while ((ze = zipInputStream.getNextEntry()) != null) {
                if(i <= 0){
                    break;
                }
                for (String docxFileDir : tempDirs){
                    if(ze.getName().startsWith(docxFileDir)){
                        i --;
                        tempDirs.remove(docxFileDir);
                        break;
                    }
                }
            }
            return i <= 0;
        }finally {
            //一定记得关闭流
            zipInputStream.closeEntry();
        }
    }



    private static final Set<String> XLSX_FILE_DIRS = new HashSet() {{
            add("_rels/");add("docProps/");add("xl/");add("[Content_Types].xml");
    }};
    /**
     *
     * @param filePath
     * @return: boolean
     * @author: wlpia
     * @date: 2024/1/31 18:25
     */
    @SneakyThrows
    public static boolean isTypeXlsx(String filePath){
        FileInputStream input = new FileInputStream(filePath);
        try{
            return isTypeXlsx(input);
        }finally {
            input.close();
        }
    }
    @SneakyThrows
    public static boolean isTypeXlsx(InputStream inputStream){
        //获取ZIP输入流(一定要指定字符集Charset.forName("GBK")否则会报java.lang.IllegalArgumentException: MALFORMED)
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream), Charset.forName("GBK"));
        try{
            //定义ZipEntry置为null,避免由于重复调用zipInputStream.getNextEntry造成的不必要的问题
            ZipEntry ze = null;
            Set<String> tempDirs = new HashSet(){{
                addAll(XLSX_FILE_DIRS);
            }};
            int i = tempDirs.size();
            //循环遍历
            while ((ze = zipInputStream.getNextEntry()) != null) {
                if(i <= 0){
                    break;
                }
                for (String xlsxFileDir : tempDirs){
                    if(ze.getName().startsWith(xlsxFileDir)){
                        tempDirs.remove(xlsxFileDir);
                        i --;
                        break;
                    }
                }
            }
            return i <= 0;
        }finally {
            //一定记得关闭流
            zipInputStream.closeEntry();
        }
    }


    private static final Set<String> APK_FILE_DIRS = new HashSet() {{
        add("assets/");add("dc/");add("io/");add("lib/");
        add("META-INF/");add("res/");add("AndroidManifest.xml");
    }};
    /**
     *
     * @param filePath
     * @return: boolean
     * @author: wlpia
     * @date: 2024/1/31 18:25
     */
    @SneakyThrows
    public static boolean isTypeApk(String filePath){
        FileInputStream input = new FileInputStream(filePath);
        try{
            return isTypeApk(input);
        }finally {
            input.close();
        }
    }
    @SneakyThrows
    public static boolean isTypeApk(InputStream inputStream){
        //获取ZIP输入流(一定要指定字符集Charset.forName("GBK")否则会报java.lang.IllegalArgumentException: MALFORMED)
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream), Charset.forName("GBK"));
        try{
            //定义ZipEntry置为null,避免由于重复调用zipInputStream.getNextEntry造成的不必要的问题
            ZipEntry ze = null;
            Set<String> tempDirs = new HashSet(){{
                addAll(APK_FILE_DIRS);
            }};
            int i = tempDirs.size();
            //循环遍历
            while ((ze = zipInputStream.getNextEntry()) != null) {
                if(i <= 0){
                    break;
                }
                for (String apkFileDir : tempDirs){
                    if(ze.getName().startsWith(apkFileDir)){
                        tempDirs.remove(apkFileDir);
                        i --;
                        break;
                    }
                }
            }
            return i <= 0;
        }finally {
            //一定记得关闭流
            zipInputStream.closeEntry();
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * 判断文件类型格式
     * </p>
     *
     * <p><b>@param</b> <b>filePath</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/1/31 18:23</p>
     * <p><b>{@code @return:}</b>{@link FileType}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static FileType getType(String filePath) throws IOException {
        FileInputStream input = new FileInputStream(filePath);
        try{
            return getType(input);
        }finally {
            input.close();
        }
    }
    public static FileType getType(InputStream inputStream) throws IOException {
        String fileHead = getFileContent(inputStream);
        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();
        FileType[] fileTypes = FileType.values();
        for (FileType type : fileTypes) {
            if (!fileHead.startsWith(type.getValue().toUpperCase())) {
                continue;
            }
            return type;
        }
        return null;
    }

}
