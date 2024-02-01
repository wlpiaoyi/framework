package org.wlpiaoyi.framework.utils.data;

import lombok.Getter;

/**
 * {@code @author:}         wlpia
 * {@code @description:}    文件格式
 * {@code @date:}           2024-01-31 10:58:18
 * {@code @version:}:       1.0
 */
@Getter
public enum FileType {

    /**
     * JEPG.
     */
    JPEG("FFD8FFE", ".JPEG.JPG."),

    /**
     * PNG.
     */
    PNG("89504E47", ".PNG."),

    /**
     * GIF.
     */
    GIF("47494638", ".GIF."),

    /**
     * TIFF.
     */
    TIFF("49492A00", ".TIFF."),

    /**
     * TXT.
     */
    TXT("6C657420", ".TXT."),

    /**
     * HTML.
     */
    HTML("68746D6C3E", ".HTML.HTM."),

    /**
     * ZIP Archive, OFFICE >=2007 DOC.XLSX. APK .apk
     */
    ZIP("504B0304", ".ZIP.DOCX.XLSX.APK."),

    /**
     * RAR Archive DOC.XLSX.
     */
    RAR("52617221", ".RAR."),

    /**
     * OFFICE 2003 DOC.XLS.
     */
    WXH("D0CF11E0A1B11AE1", ".DOC.XLS."),

    /**
     * Adobe Acrobat.
     */
    PDF("255044462D312E", ".PDF.");

    private final String value;
    private final String contains;


    /**
     * Constructor.
     * @param value
     */
    FileType(String value, String contains) {
        this.value = value;
        this.contains = contains;
    }

    public boolean checkType(String type){
        String contain = "." + type.toUpperCase()  + ".";
        return this.getContains().contains(contain);
    }

}
