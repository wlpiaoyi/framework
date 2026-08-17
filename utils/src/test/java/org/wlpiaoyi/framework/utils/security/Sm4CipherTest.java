package org.wlpiaoyi.framework.utils.security;

import org.junit.Assert;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    SM4 单元测试
 * {@code @date:}           2026/7/30
 * {@code @version:}:       1.0
 */
public class Sm4CipherTest {

    @Test
    public void testEcb() throws Exception {
        String key = StringUtils.getUUID32().substring(0, 16);
        Sm4Cipher sm4 = Sm4Cipher.build().setKey(key).loadConfig();
        String source = "国密SM4对称加密测试内容";
        byte[] encoded = sm4.encrypt(source.getBytes(StandardCharsets.UTF_8));
        byte[] decoded = sm4.decrypt(encoded);
        Assert.assertEquals(source, new String(decoded, StandardCharsets.UTF_8));
    }

    @Test
    public void testCbc() throws Exception {
        String keyStr = StringUtils.getUUID32();
        String iv = StringUtils.getUUID32().substring(0, 16);
        Sm4Cipher sm4 = Sm4Cipher.build()
                .setKey(keyStr, 128)
                .setIV(iv)
                .loadConfig();
        System.out.println("SM4 Key: " + ValueUtils.bytesToHex(sm4.getKey().getEncoded()));
        System.out.println("SM4 IV: " + sm4.getIV());
        String source = "国密SM4-CBC模式测试，包含中文与符号!@#:jdbc:mysql://10.124.10.62:13306/cityiot_nms?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=GMT%2B8&keepAlive=true&connectTimeout=10000&socketTimeout=60000&autoReconnect=true&failOverReadOnly=false";
        byte[] encoded = sm4.encrypt(source.getBytes(StandardCharsets.UTF_8));
        String pstr = new String(DataUtils.base64Encode(encoded), StandardCharsets.UTF_8);
        System.out.println("cipher(base64): " + pstr);
        byte[] decoded = sm4.decrypt(DataUtils.base64Decode(pstr.getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals(source, new String(decoded, StandardCharsets.UTF_8));
    }

    @Test
    public void testFill() throws Exception {
        Sm4Cipher sm4 = Sm4Cipher.build()
                .setKey(StringUtils.getUUID32().substring(0, 16))
                .setIV(StringUtils.getUUID32().substring(0, 16))
                .loadConfig();
        byte[] d = "填充加解密测试数据".getBytes(StandardCharsets.UTF_8);
        byte[] ed = sm4.encryptFill(d, 64);
        byte[] dd = sm4.decryptFill(ed, 64);
        Assert.assertArrayEquals(d, dd);
    }
}
