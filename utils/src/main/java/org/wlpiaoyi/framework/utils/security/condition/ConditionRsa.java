package org.wlpiaoyi.framework.utils.security.condition;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/20 15:28
 * {@code @version:}:       1.0
 */
public class ConditionRsa {
    /*
     * 秘钥算法
     * */
    //============================================>
    public static final String KEY_ALGORITHM ="RSA";
    //<============================================

    /*
     * 签名算法
     * 512~65536位（密钥长度必须是64的倍数）
     * */
    //============================================>
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA1 = "SHA1withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA224 = "SHA224withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA256 = "SHA256withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA384 = "SHA384withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA512 = "SHA512withRSA";
    //<============================================
}
