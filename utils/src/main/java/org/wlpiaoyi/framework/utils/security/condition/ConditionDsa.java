package org.wlpiaoyi.framework.utils.security.condition;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/20 15:28
 * {@code @version:}:       1.0
 */
public class ConditionDsa {
    /*
     * 秘钥算法
     * */
    //============================================>
    public static final String KEY_ALGORITHM ="DSA";
    //<============================================

    /*
     * 签名算法
     * 512~65536位（密钥长度必须是64的倍数）
     * */
    //============================================>
    public static final String SIGNATURE_ALGORITHM_SHA1 = "SHA1withDSA";
    public static final String SIGNATURE_ALGORITHM_SHA224 = "SHA224withDSA";
    public static final String SIGNATURE_ALGORITHM_SHA256 = "SHA256withDSA";
    public static final String SIGNATURE_ALGORITHM_SHA384 = "SHA384withDSA";
    public static final String SIGNATURE_ALGORITHM_SHA512 = "SHA512withDSA";
    //<============================================
}
