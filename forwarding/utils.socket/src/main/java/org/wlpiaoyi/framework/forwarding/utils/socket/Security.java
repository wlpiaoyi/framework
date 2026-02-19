package org.wlpiaoyi.framework.forwarding.utils.socket;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
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
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-19 17:46:57</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class Security {

    private final RsaCipher rasE;
    private final RsaCipher rasD;
    
    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * TODO
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>key</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>type</b>
     * {@link int}
     * 0: key is private key
     * 1: key is public key
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/19 17:55</p>
     * <p><b>{@code @return:}</b>{@link }</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    @SneakyThrows
    public Security(int type) {
        if(ForwardUtils.getCONFIG_MAP() == null){
            try {
                ForwardUtils.loadMap();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(type == 0){
            String key = ReaderUtils.loadString(MapUtils.getString(ForwardUtils.getCONFIG_MAP(), "privateKey"),  null);
            this.rasE = RsaCipher.build(0, 512).setPrivateKey(key).loadConfig();
            this.rasD = RsaCipher.build(1, 512).setPrivateKey(key).loadConfig();
        }else{
            String key = ReaderUtils.loadString(MapUtils.getString(ForwardUtils.getCONFIG_MAP(), "publicKey"),  null);
            this.rasD = RsaCipher.build(0, 512).setPublicKey(key).loadConfig();
            this.rasE = RsaCipher.build(1, 512).setPublicKey(key).loadConfig();
        }
    }

    private byte[] rsaEncrypt(byte[] bytes){
        return this.rasE.encrypt(bytes);
    }

    private byte[] rsaDecrypt(byte[] bytes){
        return this.rasD.decrypt(bytes);
    }

    @SneakyThrows
    public byte[] encrypt(byte[] bytes, int offset, int len){
        byte[] aesKey = ValueUtils.hexToBytes(StringUtils.getUUID32());
        byte[] eKey = this.rsaEncrypt(aesKey);
        AesCipher aes = AesCipher.build().setKey(ValueUtils.bytesToHex(aesKey)).loadConfig();
        byte[] eData = aes.encrypt(bytes, offset, len);
        byte[] res = new byte[eKey.length + eData.length];
        System.arraycopy(eKey, 0, res, 0, eKey.length);
        System.arraycopy(eData, 0, res, eKey.length, eData.length);
        return res;
    }

    @SneakyThrows
    public byte[] decrypt(byte[] bytes, int offset, int len){
        byte[] eKey = new byte[64];
        System.arraycopy(bytes, offset, eKey, 0, eKey.length);
        byte[] eData = new byte[len - eKey.length];
        System.arraycopy(bytes, offset + eKey.length, eData, 0, eData.length);
        String key = ValueUtils.bytesToHex(this.rsaDecrypt(eKey));
        AesCipher aes = AesCipher.build().setKey(key).loadConfig();
        return aes.decrypt(eData);
    }


    public static void keyGenerator(){
        var rsa = RsaCipher.build(0, 512).loadRandomKey();
        log.info("private key: \n{}", rsa.getPrivateKey());
        log.info("public key: \n{}", rsa.getPublicKey());
    }

//    public static void main(String[] args) throws IllegalBlockSizeException, BadPaddingException {
////        keyGenerator();
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
//    }
}
