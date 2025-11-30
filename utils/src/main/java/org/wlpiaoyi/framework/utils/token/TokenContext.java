package org.wlpiaoyi.framework.utils.token;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.security.AesCipher;
import org.wlpiaoyi.framework.utils.token.model.AuthBody;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.charset.StandardCharsets;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 提供基于 AES 加密的 Token 编解码功能，支持 Token 的加密、解密以及过期判断操作。
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/28 10:09</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
@Slf4j
public class TokenContext {

    /**
     * 用于执行 AES 加密与解密的核心工具实例
     */
    private final AesCipher aesCipher;

    /**
     * 构造函数，初始化 TokenContext 实例
     *
     * <p><b>{@code @param}</b> <b>IV</b>
     * 初始化向量，AES 加密算法所需参数
     * {@link String}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>key</b>
     * 加密密钥，用于 AES 加密算法
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/28 11:12</p>
     * <p><b>{@code @return:}</b>{@link }</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    TokenContext(String IV, String key){
        this.aesCipher = AesCipher.build().setIV(IV).setKey(key).loadConfig();
    }

    /**
     * 对给定的认证信息进行加密处理，返回Base64编码的字符串Token
     *
     * <p><b>{@code @param}</b> <b>authBody</b>
     * 待加密的认证信息对象
     * {@link AuthBody}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/28 11:12</p>
     * <p><b>{@code @return:}</b>{@link String} 返回Base64编码的加密Token字符串，若失败则返回 null</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public String makeToken(AuthBody authBody){
        // 将 expireSeconds 转换为字节数组
        byte[] expireSecondsBytes = ValueUtils.toBytes(authBody.getExpireSeconds());

        // 拼接完整的待加密数据结构：第一个字节表示 expireSeconds 字节数长度，
        // 接着是 expireSeconds 字节内容，最后是 tokenValue 内容
        byte[] dataBytes = new byte[1 + expireSecondsBytes.length + authBody.getTokenValue().length];
        int curIndex = 0;
        dataBytes[curIndex++] = (byte) expireSecondsBytes.length;
        for (int i = 0; i < expireSecondsBytes.length; i++) {
            dataBytes[i + curIndex] = expireSecondsBytes[i];
        }
        curIndex += expireSecondsBytes.length;
        for (int i = 0; i < authBody.getTokenValue().length; i++) {
            dataBytes[i + curIndex] = authBody.getTokenValue()[i];
        }

        // 执行加密并转换为Base64字符串
        try {
            return new String(
                    DataUtils.base92Encode(this.aesCipher.encrypt(dataBytes)),
                    StandardCharsets.UTF_8
            ).replaceAll("\n|\r", "");
        } catch (IllegalBlockSizeException e) {
            log.error("Token encryption failed due to illegal block size. Token data length: {}", dataBytes.length, e);
        } catch (BadPaddingException e) {
            log.error("Token encryption failed due to bad padding. Token data length: {}", dataBytes.length, e);
        }
        return null;
    }

    /**
     * 解析传入的Token字符串，并将其还原成认证信息对象
     *
     * <p><b>{@code @param}</b> <b>token</b>
     * base92编码的已加密Token字符串
     * {@link String}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>authBody</b>
     * 用于接收解析结果的目标认证信息实例
     * {@link T}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/28 11:50</p>
     * <p><b>{@code @return:}</b>{@link T} 返回解析完成的认证信息对象，若失败则返回 null</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public <T extends AuthBody> T resolveToken(String token, T authBody){
        try {
            // 执行解密及去除填充
            byte[] data = this.aesCipher.decrypt(DataUtils.base92Decode(token.getBytes(StandardCharsets.UTF_8)));

            // 解析出 expireSeconds 长度及其值
            int curIndex = 0;
            byte expireSecondsBytesLen = data[curIndex++];
            byte[] expireSecondsBytes = new byte[expireSecondsBytesLen];
            for (int i = 0; i < expireSecondsBytesLen; i++) {
                expireSecondsBytes[i] = data[i + curIndex];
            }
            curIndex += expireSecondsBytesLen;
            long expireSeconds = ValueUtils.toLong(expireSecondsBytes);

            // 提取剩余部分作为 tokenValue
            byte[] dataBytes = new byte[data.length - curIndex];
            for (int i = 0; i < dataBytes.length; i++) {
                dataBytes[i] = data[i + curIndex];
            }

            // 设置解析结果到目标认证信息中
            return (T) authBody.setExpireSeconds(expireSeconds).setTokenValue(dataBytes);
        } catch (IllegalBlockSizeException e) {
            log.error("Token decryption failed due to illegal block size. Encrypted data length: {}", token.length(), e);
        } catch (BadPaddingException e) {
            log.error("Token decryption failed due to bad padding. Encrypted data length: {}", token.length(), e);
        }
        return null;
    }

    /**
     * 判断给定认证信息是否已过期
     *
     * @param authBody 待检查的认证信息对象
     * @return 如果认证信息已过期返回 true，否则返回 false
     */
    public boolean isExpire(AuthBody authBody){
        return authBody.getExpireSeconds() < System.currentTimeMillis();
    }

    /**
     * 判断给定的Token字符串是否已经过期
     *
     * @param token base92编码的加密Token字符串
     * @return 如果解密失败或认证信息已过期返回 true，否则返回 false
     */
    public boolean isExpire(String token){
        AuthBody authBody = this.resolveToken(token, new AuthBodyImpl());
        return authBody == null || this.isExpire(authBody);
    }

    /**
     * 默认实现的认证信息类型，用于内部反序列化操作
     */
    static class AuthBodyImpl implements AuthBody {
        private byte[] tokenValue;
        private long expireSeconds;

        @Override
        public byte[] getTokenValue() {
            return tokenValue;
        }

        @Override
        public AuthBody setTokenValue(byte[] tokenValue) {
            this.tokenValue = tokenValue;
            return this;
        }

        @Override
        public long getExpireSeconds() {
            return this.expireSeconds;
        }

        @Override
        public AuthBody setExpireSeconds(long expireSeconds) {
            this.expireSeconds = expireSeconds;
            return this;
        }
    }
}
