package org.wlpiaoyi.framework.utils.security.condition;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    国密 SM4 算法常量
 * {@code @date:}           2026/7/30
 * {@code @version:}:       1.0
 */
public class ConditionSm4 {

    /** 密钥算法 **/
    public static final String KEY_ALGORITHM = "SM4";

    /** BouncyCastle Provider 名称 **/
    public static final String PROVIDER = "BC";

    /** 密钥长度（位），SM4 固定 128 **/
    public static final int KEY_SIZE = 128;

    /** IV 长度（字节），与分组长度一致 **/
    public static final int IV_SIZE = 16;

    /**
     * 签名/随机数算法
     */
    public static final String SIGNATURE_ALGORITHM_SHA1PRNG = "SHA1PRNG";

    /** 加密-解密算法 / 工作模式 / 填充方式 **/
    public static final String CIPHER_ALGORITHM_ECB = "SM4/ECB/PKCS5Padding";

    /** 加密-解密算法 / 工作模式 / 填充方式 **/
    public static final String CIPHER_ALGORITHM_CBC = "SM4/CBC/PKCS5Padding";
}
