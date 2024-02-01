package org.wlpiaoyi.framework.utils.data;

import lombok.Cleanup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * {@code @author:}         wlpia
 * {@code @description:}
 * {@code @date:}           2024-02-01 10:39:30
 * {@code @version:}:       1.0
 */
public class FileUtilsTest {
    @Before
    public void setUp() throws Exception {}


    @Test
    public void loadString() throws IOException {

        String baseDir = "D:\\wlpia\\Documents\\Temp\\";

        String type = "xls";
        String name = "office.1.xls";
        System.out.println(type + ":" + name + ":\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        FileType fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);
        type = "xls";name = "office.1.xlsx";
        System.out.println(type + ":" + name + ":\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        System.out.println(fileType);

        type = "xls";name = "wps.1.xls";
        System.out.println(type + ":" + name + ":\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "xls";name = "wps.1.xlsx";
        System.out.println(type + ":" + name + ":\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        System.out.println(fileType);

        type = "xls";name = "wps.2.xls";
        System.out.println(type + ":" + name + ":\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "xls";name = "wps.2.xlsx";
        System.out.println(type + ":" + name + ":\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        System.out.println(fileType);

        type = "doc";name = "office.1.doc";
        System.out.println(type + ":" + name + ":\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "doc";name = "office.1.docx";
        System.out.println(type + ":" + name + ":\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "doc";name = "wps.1.doc";
        System.out.println(type + ":" + name + ":\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "doc";name = "wps.1.docx";
        System.out.println(type + ":" + name + ":\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "doc";name = "wps.1.docx";
        System.out.println(type + ":" + name + ":\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);


        type = "zip";name = "1.zip";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        System.out.println(fileType);

        type = "zip";name = "2.zip";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        System.out.println(fileType);


        type = "rar";name = "1.rar";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "rar";name = "2.rar";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "pdf";name = "office.1.pdf";
        System.out.println(type + ":" + name + ":\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "pdf";name = "1.pdf";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));

        type = "png";name = "1.png";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "png";name = "2.png";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));

        type = "jpg";name = "1.jpg";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "jpg";name = "1.jpeg";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);

        type = "jpg";name = "2.jpg";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println(fileType);


        type = "apk";name = "1.apk";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        System.out.println(fileType);

        type = "apk";name = "2.apk";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        fileType = FileUtils.getType(baseDir + type +"\\" + name);
        System.out.println("是否是XLSX:" + FileUtils.isTypeXlsx(baseDir + type +"\\" + name));
        System.out.println("是否是DOCX:" + FileUtils.isTypeDocx(baseDir + type +"\\" + name));
        System.out.println("是否是APK:" + FileUtils.isTypeApk(baseDir + type +"\\" + name));
        System.out.println(fileType);


        type = "mp4";name = "1.mp4";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        type = "mp4";name = "2.mp4";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        type = "mp4";name = "3.mp4";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        type = "mp4";name = "4.mp4";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));
        type = "mp4";name = "5.mp4";
        System.out.println(type + ":" + name + ":\t\t\t\t" + FileUtils.getFileContent(baseDir + type +"\\" + name));


        String path = "D:\\wlpia\\Documents\\Temp\\1.mp4";
        fileType = FileUtils.getType(path);
        System.out.println();
    }

    @After
    public void tearDown() throws Exception {}
}
