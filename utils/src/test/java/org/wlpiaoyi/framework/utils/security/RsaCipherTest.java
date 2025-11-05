package org.wlpiaoyi.framework.utils.security;

import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/21 11:37
 * {@code @version:}:       1.0
 */
public class RsaCipherTest {

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIwgd+H2N2wAAPEHEi8ypKdwaB2I\n" +
            "ouHQGfI/oXpA8hJFBnq7h/OF/xVm2TN+i5Y4GOCK2TdfgtGa10ed0xwUb13eu6oFtuo1VHCAiSzC\n" +
            "CbIVutyVysY4l7HvhAJvH1KlHRLRQU4sFNNgdrdYJwSV4hcUU62pgBGIyDFadTetVnW/AgMBAAEC\n" +
            "gYAziVd+IEe27XNrMl4SRM6BFJr+TbwWUCrSyWtS4uMFLCTba/Bu9Nfh368/vKmLCLvBjd+g+XxM\n" +
            "KeZGnTnBKJTihnKw4AwqmVN1Sr1RTnXwJ6eNGSitNEqaYhGU4aEwr+714ZkVsVY5v7vTjZJ2hTDr\n" +
            "ksdZd0llGHG1umy7CYyE0QJBAMVPc6813nJ6rF/v8KQqVfIhO1qChb4BH47zaegMGOS4NYEgdNjK\n" +
            "YmOIHh47+GvVQj5aTbmPScXZySEJ4Z5eYQ8CQQC1zqzDaPTN4Ts46JfrpNJhUjJOFr/dqAUfifln\n" +
            "UsGYrPtthviDrMzemnT+hq9HIXRM+fYsWn8QN0/teainakBRAkBYvty0kNElwoFngT9GR3hyuHm+\n" +
            "wvgutsif/mHDKjXEIgqGsrd7jsPkKqQJS0X4EmqCKxHMhWNUJxmsz4n4NlEHAkA09qR1uNm4MGkk\n" +
            "Rv4a88Ul/OASx6XVWOFFMtipNP6ZD6ufWLaFBY4ZOz3h+DKPsjtDQX5ppWNmwfZS5CIxw05BAkAn\n" +
            "HGet1e6kl9bGv+8LXsE2/JHHr97dS52I6xWkdW5yp5/OmV0X90NF4P7Fb5zE870lWG3/orBdRqgp\n" +
            "4JTodTCj";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMIHfh9jdsAADxBxIvMqSncGgdiKLh0BnyP6F6\n" +
            "QPISRQZ6u4fzhf8VZtkzfouWOBjgitk3X4LRmtdHndMcFG9d3ruqBbbqNVRwgIkswgmyFbrclcrG\n" +
            "OJex74QCbx9SpR0S0UFOLBTTYHa3WCcEleIXFFOtqYARiMgxWnU3rVZ1vwIDAQAB";

    @Test
    public void test() throws Exception {
        byte[] shaBytes = DataUtils.sha256("admin".getBytes(StandardCharsets.UTF_8));
        System.out.println("shaBase64:" + new String(DataUtils.base64Encode(shaBytes)));
        RsaCipher RSACipher = RsaCipher.build(0).loadRandomKey().loadConfig();
        System.out.println("privateKey:\n" + RSACipher.getPrivateKey());
        System.out.println("publicKey:\n" + RSACipher.getPublicKey());
        String eText = new String(
                DataUtils.base64Encode(
                        RSACipher.encrypt(shaBytes)
                ),
                StandardCharsets.UTF_8
        );
        System.out.println("eText:" + eText);
        byte[] dBytes = RSACipher.decrypt(
                        DataUtils.base64Decode(eText.getBytes(StandardCharsets.UTF_8))
                );
        String dText = new String(DataUtils.base64Encode(dBytes));
        System.out.println("dText:" + dText);
        System.out.println("oText:" + new String(DataUtils.base64Encode(shaBytes)));
    }

    @Test
    public void test1() throws Exception {
        RsaCipher RSACipher = RsaCipher.build(0).setPrivateKey(this.privateKey).setPublicKey(this.publicKey).loadConfig();
        System.out.println("privateKey:\n" + RSACipher.getPrivateKey());
        System.out.println("publicKey:\n" + RSACipher.getPublicKey());
        String text = "20260210";
        System.out.println("text:" + text);
        String eText = new String(
                DataUtils.base64Encode(
                        RSACipher.encrypt(text.getBytes(StandardCharsets.UTF_8))
                ),
                StandardCharsets.UTF_8
        );
        System.out.println("eText:" + eText);

        String dText = new String(
                RSACipher.decrypt(
                        DataUtils.base64Decode(eText.getBytes(StandardCharsets.UTF_8))
                ),
                StandardCharsets.UTF_8
        );


        System.out.println("dText:" + dText);
    }
}
