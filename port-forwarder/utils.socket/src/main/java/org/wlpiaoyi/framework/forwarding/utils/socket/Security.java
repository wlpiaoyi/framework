package org.wlpiaoyi.framework.forwarding.utils.socket;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.security.AesCipher;
import org.wlpiaoyi.framework.utils.security.RsaCipher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;

/**
 * RSA + AES 混合加密实现类。
 * <p>
 * 加密流程（每次加密独立）：
 * <ol>
 *   <li>生成随机 AES 密钥（32 字节，取 UUID 的 hex）。</li>
 *   <li>用 RSA 加密 AES 密钥（512bit RSA 输出固定 64 字节）。</li>
 *   <li>用 AES 加密实际业务数据。</li>
 *   <li>最终输出格式：{@code [64-byte RSA加密后的AESKey] + [AES加密后的数据]}。</li>
 * </ol>
 * </p>
 * <p>
 * 解密流程为上述逆过程：先拆分前 64 字节用 RSA 解密得到 AES 密钥，再用 AES 解密后续数据。
 * </p>
 * <p>
 * {@code type} 参数决定使用哪一侧密钥：
 * <ul>
 *   <li>{@code type=0}：使用<b>私钥</b>（response 侧）。</li>
 *   <li>{@code type=1}：使用<b>公钥</b>（request 侧）。</li>
 * </ul>
 * </p>
 */
@Slf4j
public class Security {

    /** RSA 加密器 */
    private final RsaCipher rasE;
    /** RSA 解密器 */
    private final RsaCipher rasD;

    /**
     * 构造 Security 实例。
     *
     * @param type 密钥类型：0 表示使用私钥；1 表示使用公钥
     */
    @SneakyThrows
    public Security(int type) {
        // 若配置尚未加载，主动加载一次
        if (ForwardUtils.getCONFIG_MAP() == null) {
            try {
                ForwardUtils.loadMap();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (type == 0) {
            // 私钥侧：rasE 用私钥加密，rasD 用私钥解密
            String key = ReaderUtils.loadString(MapUtils.getString(ForwardUtils.getCONFIG_MAP(), "privateKey"), null);
            this.rasE = RsaCipher.build(0, 512).setPrivateKey(key).loadConfig();
            this.rasD = RsaCipher.build(1, 512).setPrivateKey(key).loadConfig();
        } else {
            // 公钥侧：rasD 用公钥解密，rasE 用公钥加密
            String key = ReaderUtils.loadString(MapUtils.getString(ForwardUtils.getCONFIG_MAP(), "publicKey"), null);
            this.rasD = RsaCipher.build(0, 512).setPublicKey(key).loadConfig();
            this.rasE = RsaCipher.build(1, 512).setPublicKey(key).loadConfig();
        }
    }

    /** 使用 rasE 对字节数组进行 RSA 加密 */
    private byte[] rsaEncrypt(byte[] bytes) {
        return this.rasE.encrypt(bytes);
    }

    /** 使用 rasD 对字节数组进行 RSA 解密 */
    private byte[] rsaDecrypt(byte[] bytes) {
        return this.rasD.decrypt(bytes);
    }

    /**
     * 对数据进行 RSA+AES 混合加密。
     *
     * @param bytes  原始数据
     * @param offset 数据起始偏移
     * @param len    数据长度
     * @return 加密后的字节数组（前 64 字节为 RSA 加密后的 AES 密钥，后续为 AES 密文）
     */
    @SneakyThrows
    public byte[] encrypt(byte[] bytes, int offset, int len) {
        // 生成随机 AES 密钥（16 字节 hex → 32 字符 → 16 字节原始值）
        byte[] aesKey = ValueUtils.hexToBytes(StringUtils.getUUID32());
        // 用 RSA 加密 AES 密钥
        byte[] eKey = this.rsaEncrypt(aesKey);
        // 用 AES 加密业务数据
        AesCipher aes = AesCipher.build().setKey(ValueUtils.bytesToHex(aesKey)).loadConfig();
        byte[] eData = aes.encrypt(bytes, offset, len);
        // 拼接：RSA密钥(64B) + AES密文
        byte[] res = new byte[eKey.length + eData.length];
        System.arraycopy(eKey, 0, res, 0, eKey.length);
        System.arraycopy(eData, 0, res, eKey.length, eData.length);
        return res;
    }

    /**
     * 对数据进行 RSA+AES 混合解密。
     *
     * @param bytes  加密后的字节数组（前 64 字节为 RSA 加密后的 AES 密钥）
     * @param offset 数据起始偏移
     * @param len    数据总长度
     * @return 解密后的原始字节数组
     */
    @SneakyThrows
    public byte[] decrypt(byte[] bytes, int offset, int len) {
        // 拆分前 64 字节为 RSA 加密的 AES 密钥
        byte[] eKey = new byte[64];
        System.arraycopy(bytes, offset, eKey, 0, eKey.length);
        // 剩余部分为 AES 密文
        byte[] eData = new byte[len - eKey.length];
        System.arraycopy(bytes, offset + eKey.length, eData, 0, eData.length);
        // RSA 解密得到 AES 密钥
        String key = ValueUtils.bytesToHex(this.rsaDecrypt(eKey));
        // AES 解密得到原始数据
        AesCipher aes = AesCipher.build().setKey(key).loadConfig();
        return aes.decrypt(eData);
    }

    /**
     * 生成 512-bit RSA 密钥对并打印到日志。
     * <p>
     * 供命令行手动生成公私钥使用，生成后需写入 config.json 的 {@code privateKey} 和 {@code publicKey} 字段。
     * </p>
     */
    public static void keyGenerator() {
        var rsa = RsaCipher.build(0, 512).loadRandomKey();
        log.info("private key: \n{}", rsa.getPrivateKey());
        log.info("public key: \n{}", rsa.getPublicKey());
    }

    public static void main(String[] args) throws IllegalBlockSizeException, BadPaddingException {
        keyGenerator();
////        String privateKey = """
////                MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAu+i/Tl2VphlrYYLbstTCT3S4nXOz
////                uWkCubUtAdvSXu5J7BN5V8gldFwzJ9G1hd4znVxAiSeJKRZOwOw1yWA2bQIDAQABAkAithiqxv5X
////                J8Bui5/+ba90QoU87qvfPu72M6nb+0ml59EkMYA2MK5UattMDVjmD4tRaqtf6SSerVjbsZwylsSZ
////                AiEA5trW0j4QN4hzqHIbePxyq+3mwkfBCsoCYMb5NEmuRBUCIQDQYGkDlDLvkvl4VJscwu2WHlFS
////                aEbwDoBW8dsymImG+QIhALeyCWCQfC0ESiwOaGf0UXcXvhh4KlbgedXyXlhKDP4lAiEAyX8A7UoI
////                eyxHejluCMDh19EXWDWnqwiKyAh1qxZfnpkCIQCOCQclETIUInUkwdlhUhzXXnh7dcHZ3TMIWpnk
////                aA1S4A==
////                """;
////        String publicKey = """
////                MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALvov05dlaYZa2GC27LUwk90uJ1zs7lpArm1LQHb0l7u
////                SewTeVfIJXRcMyfRtYXeM51cQIkniSkWTsDsNclgNm0CAwEAAQ==
////                """;
//        var rsa1 = new Security(0);
//        var rsa2 = new Security(1);
//        byte[] eBytes = rsa1.rsaEncrypt("123456饿哦的".getBytes());
//        System.out.println("len:" + eBytes.length + " eBytes:" + ValueUtils.bytesToHex(eBytes));
//        byte[] dBytes = rsa2.rsaDecrypt(eBytes);
//        System.out.println("dBytes:" + new String(dBytes));
//        eBytes = rsa2.rsaEncrypt(StringUtils.getUUID32().getBytes());
//        System.out.println("len:" + eBytes.length + " eBytes:" + ValueUtils.bytesToHex(eBytes));
//        dBytes = rsa1.rsaDecrypt(eBytes);
//        System.out.println("dBytes:" + new String(dBytes));
//        eBytes = rsa1.encrypt("1231231".getBytes());
//        System.out.println("len:" + eBytes.length + " eBytes:" + ValueUtils.bytesToHex(eBytes));
//        dBytes = rsa2.decrypt(eBytes);
//        System.out.println("dBytes:" + new String(dBytes));
//
    }
}
