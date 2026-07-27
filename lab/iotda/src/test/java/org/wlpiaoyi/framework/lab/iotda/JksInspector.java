package org.wlpiaoyi.framework.lab.iotda;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * 直接用 SDK 同样的方式（null 密码）加载 JKS，打印里面的证书，便于诊断 TLS 信任问题。
 */
public class JksInspector {
    public static void main(String[] args) throws Exception {
        String path = args.length > 0 ? args[0]
                : "c:/Home/Document/Develop/Java/framework/lab/iotda/src/main/resources/mqtts_ca_cert.247.jks";
        try (FileInputStream raw = new FileInputStream(path)) {
            byte[] head = raw.readNBytes(4);
            System.out.printf("magic bytes: %02X %02X %02X %02X%n",
                    head[0] & 0xFF, head[1] & 0xFF, head[2] & 0xFF, head[3] & 0xFF);
            System.out.println("(JKS=FE ED FE ED, PKCS12 starts with 30 82)");
        }

        char[] pwd = args.length > 1 ? args[1].toCharArray() : null;
        String type = args.length > 2 ? args[2] : "JKS";
        try (FileInputStream in = new FileInputStream(path)) {
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(in, pwd);
            System.out.println("JKS path = " + path);
            System.out.println("type     = " + type);
            System.out.println("password = " + (pwd == null ? "<null>" : "<" + pwd.length + " chars>"));
            System.out.println("entries  = " + ks.size());
            Enumeration<String> aliases = ks.aliases();
            int idx = 0;
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate cert = ks.getCertificate(alias);
                System.out.println("---- [" + (++idx) + "] alias=" + alias);
                if (cert instanceof X509Certificate x) {
                    System.out.println("    subject : " + x.getSubjectX500Principal());
                    System.out.println("    issuer  : " + x.getIssuerX500Principal());
                    System.out.println("    valid   : " + x.getNotBefore() + " ~ " + x.getNotAfter());
                } else {
                    System.out.println("    type    : " + cert.getType());
                }
            }
        }
    }
}
