package org.wlpiaoyi.framework.utils.security.condition;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    国密 SM2 算法常量
 * {@code @date:}           2026/7/30
 * {@code @version:}:       1.0
 */
public class ConditionSm2 {

    /**
     * 密钥对算法（椭圆曲线）
     * SM2 密钥通过 EC + sm2p256v1 曲线生成
     */
    public static final String KEY_PAIR_ALGORITHM = "EC";

    /** 加密算法 **/
    public static final String CIPHER_ALGORITHM = "SM2";

    /** BouncyCastle Provider 名称 **/
    public static final String PROVIDER = "BC";

    /** SM2 推荐曲线 **/
    public static final String CURVE_NAME = "sm2p256v1";

    /** 签名算法（SM3withSM2） **/
    public static final String SIGNATURE_ALGORITHM = "SM3withSM2";
}
