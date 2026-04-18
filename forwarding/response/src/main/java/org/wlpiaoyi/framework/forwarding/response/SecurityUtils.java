package org.wlpiaoyi.framework.forwarding.response;

import lombok.Getter;
import org.wlpiaoyi.framework.forwarding.utils.socket.Security;

/**
 * response 侧加密工具单例持有者。
 * <p>
 * 使用 {@code new Security(0)} 初始化，type=0 表示使用<b>私钥</b>进行 RSA 加解密。
 * 与 request 侧的 {@code new Security(1)}（公钥侧）配对，构成完整的 RSA+AES 混合加密链路。
 * </p>
 *
 * @see Security
 */
public class SecurityUtils {

    /** 单例 Security 实例，全局复用以避免重复初始化密钥 */
    @Getter
    private static final Security security = new Security(0);
}
