package org.wlpiaoyi.framework.utils.security.condition;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/20 15:14
 * {@code @version:}:       1.0
 */
public class ConditionAes {

    /** 秘钥算法 **/
    public static final String KEY_ALGORITHM = "AES";

    /**
     * 签名算法
     * 512~65536位（密钥长度必须是64的倍数）
     * */
    public static final String SIGNATURE_ALGORITHM_SHA1PRNG = "SHA1PRNG";

    /**
     * AES/CBC/NoPadding 要求
     * 密钥必须是16字节长度的；Initialization vector (IV) 必须是16字节
     * 待加密内容的字节长度必须是16的倍数，如果不是16的倍数，就会出如下异常：
     * javax.crypto.IllegalBlockSizeException: Input length not multiple of 16 bytes
     *
     *  由于固定了位数，所以对于被加密数据有中文的, 加、解密不完整
     *
     *  可 以看到，在原始数据长度为16的整数n倍时，假如原始数据长度等于16*n，则使用NoPadding时加密后数据长度等于16*n，
     *  其它情况下加密数据长 度等于16*(n+1)。在不足16的整数倍的情况下，假如原始数据长度等于16*n+m[其中m小于16]，
     *  除了NoPadding填充之外的任何方 式，加密数据长度都等于16*(n+1).
     */
    public static final String CIPHER_ALGORITHM_AEN = "AES/EBC/NoPadding";
    /** 加密-解密算法 / 工作模式 / 填充方式 **/
    public static final String CIPHER_ALGORITHM_AE5_7 = "AES/ECB/PKCS5Padding";
    /** 加密-解密算法 / 工作模式 / 填充方式 **/
    public static final String CIPHER_ALGORITHM_AC5_7 = "AES/CBC/PKCS5Padding";
}
