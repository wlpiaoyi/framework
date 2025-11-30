package org.wlpiaoyi.framework.utils.token;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * TokenContext构建器，用于链式构建TokenContext实例
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/28</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public class TokenContextBuilder {

    private String IV;
    private String key;

    /**
     * 创建TokenContextBuilder实例
     *
     * @return TokenContextBuilder实例
     */
    public static TokenContextBuilder create() {
        return new TokenContextBuilder();
    }

    /**
     * 设置初始化向量
     *
     * @param IV 初始化向量，AES加密算法所需参数
     * @return TokenContextBuilder实例
     */
    public TokenContextBuilder setIV(String IV) {
        this.IV = IV;
        return this;
    }

    /**
     * 设置加密密钥
     *
     * @param key 加密密钥，用于AES加密算法
     * @return TokenContextBuilder实例
     */
    public TokenContextBuilder setKey(String key) {
        this.key = key;
        return this;
    }


    /**
     * 构建TokenContext实例
     *
     * @return TokenContext实例
     * @throws IllegalArgumentException 当必要参数未设置时抛出异常
     */
    public TokenContext build() {
        if (IV == null || IV.isEmpty()) {
            throw new IllegalArgumentException("IV must not be null or empty");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty");
        }
        return new TokenContext(IV, key);
    }
}
