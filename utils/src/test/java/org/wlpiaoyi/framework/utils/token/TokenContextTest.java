package org.wlpiaoyi.framework.utils.token;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.token.model.AuthBody;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class TokenContextTest {

    private TokenContext tokenContext;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");;

    @Before
    public void setUp() throws Exception {
        log("开始设置测试环境...");

        // 使用Builder模式创建TokenContext实例
        tokenContext = TokenContextBuilder.create()
                .setIV(StringUtils.getUUID32().substring(0, 16)) // 16位IV
                .setKey(StringUtils.getUUID32()) // 32位密钥
//                .setFillSize(32)
                .build();

        log("TokenContext实例创建成功");
        log("测试环境设置完成");
    }

    /**
     * 测试TokenContext的基本加密解密功能
     */
    @Test
    public void testMakeAndResolveToken() {
        log("=== 开始执行Token生成和解析测试 ===");

        // 创建测试认证信息
        String authValue = "test-auth-body-valuetantianshiwoe为哦为安静的覅偶尔我为为u围殴日";
        long expireTime = System.currentTimeMillis() + 3600000; // 1小时后过期
        AuthBody testAuthBody = new TokenContext.AuthBodyImpl()
                .setTokenValue(authValue.getBytes())
                .setExpireSeconds(expireTime);

        log("创建测试认证信息: value=" + authValue + ", expireTime=" + dateFormat.format(new Date(expireTime)));

        // 执行Token生成
        log("开始执行Token生成操作...");
        String token = tokenContext.makeToken(testAuthBody);
        assertNotNull("Token生成结果不应为null", token);
        assertFalse("生成的Token不应该为空", token.isEmpty());
        log("Token生成成功，Token: " + token);
        log("Token生成成功，Token长度: " + token.length());

        // 执行Token解析
        log("开始执行Token解析操作...");
        AuthBody resolvedAuthBody = tokenContext.resolveToken(token, new TokenContext.AuthBodyImpl());
        assertNotNull("解析结果不应为null", resolvedAuthBody);
        log("Token解析成功");

        // 验证解析结果
        assertArrayEquals("认证信息值应一致", testAuthBody.getTokenValue(), resolvedAuthBody.getTokenValue());
        assertEquals("过期时间应一致", testAuthBody.getExpireSeconds(), resolvedAuthBody.getExpireSeconds());

        log("验证通过: 解析后的认证信息值=" + new String(resolvedAuthBody.getTokenValue()) +
                ", 过期时间=" + dateFormat.format(new Date(resolvedAuthBody.getExpireSeconds())));
        log("=== Token生成和解析测试完成 ===");
    }

    /**
     * 测试认证信息过期判断功能
     */
    @Test
    public void testAuthBodyExpireCheck() {
        log("=== 开始执行认证信息过期判断测试 ===");

        // 测试未过期认证信息
        AuthBody validAuthBody = new TokenContext.AuthBodyImpl()
                .setTokenValue("valid-auth".getBytes())
                .setExpireSeconds(System.currentTimeMillis() + 3600000); // 1小时后过期

        log("创建未过期认证信息，过期时间: " + dateFormat.format(new Date(validAuthBody.getExpireSeconds())) +
                ", 当前时间: " + dateFormat.format(new Date(System.currentTimeMillis())));

        boolean isValidAuthExpired = tokenContext.isExpire(validAuthBody);
        assertFalse("未过期的认证信息应返回false", isValidAuthExpired);
        log("未过期认证信息判断结果: " + isValidAuthExpired);

        // 测试已过期认证信息
        AuthBody expiredAuthBody = new TokenContext.AuthBodyImpl()
                .setTokenValue("expired-auth".getBytes())
                .setExpireSeconds(System.currentTimeMillis() - 3600000); // 1小时前过期

        log("创建已过期认证信息，过期时间: " + dateFormat.format(new Date(expiredAuthBody.getExpireSeconds())) +
                ", 当前时间: " + dateFormat.format(new Date(System.currentTimeMillis())));

        boolean isExpiredAuthExpired = tokenContext.isExpire(expiredAuthBody);
        assertTrue("已过期的认证信息应返回true", isExpiredAuthExpired);
        log("已过期认证信息判断结果: " + isExpiredAuthExpired);

        // 测试Token字符串的过期检查
        log("开始测试Token字符串过期检查...");
        String validToken = tokenContext.makeToken(validAuthBody);
        boolean isTokenExpired = tokenContext.isExpire(validToken);
        assertFalse("未过期的Token应返回false", isTokenExpired);
        log("未过期Token过期检查结果: " + isTokenExpired);

        String expiredToken = tokenContext.makeToken(expiredAuthBody);
        boolean isExpiredTokenExpired = tokenContext.isExpire(expiredToken);
        assertTrue("已过期的Token应返回true", isExpiredTokenExpired);
        log("已过期Token过期检查结果: " + isExpiredTokenExpired);

        log("=== 认证信息过期判断测试完成 ===");
    }

    /**
     * 测试无效Token解析情况
     */
    @Test
    public void testResolveInvalidToken() {
        log("=== 开始执行无效Token解析测试 ===");

        // 使用无效Token尝试解析
        String invalidToken = "invalid-token-string";
        log("使用无效Token尝试解析，Token内容: " + invalidToken);

        AuthBody result = tokenContext.resolveToken(invalidToken, new TokenContext.AuthBodyImpl());

        assertNull("使用无效Token解析应返回null", result);
        log("无效Token解析结果为null，符合预期");
        log("=== 无效Token解析测试完成 ===");
    }

    /**
     * 测试边界条件：空认证信息值
     */
    @Test
    public void testEmptyAuthBodyValue() {
        log("=== 开始执行空认证信息值测试 ===");

        // 创建空值认证信息
        AuthBody emptyAuthBody = new TokenContext.AuthBodyImpl()
                .setTokenValue(new byte[0]) // 空字节数组
                .setExpireSeconds(System.currentTimeMillis() + 3600000);

        log("创建空值认证信息，认证信息值长度: 0, 过期时间: " +
                dateFormat.format(new Date(emptyAuthBody.getExpireSeconds())));

        // 执行Token生成解析
        log("开始执行空值认证信息Token生成...");
        String token = tokenContext.makeToken(emptyAuthBody);
        assertNotNull("生成空值认证信息Token不应为null", token);
        log("空值认证信息Token生成成功，Token长度: " + token.length());

        log("开始执行空值认证信息Token解析...");
        AuthBody resolvedAuthBody = tokenContext.resolveToken(token, new TokenContext.AuthBodyImpl());
        assertNotNull("解析空值认证信息Token不应为null", resolvedAuthBody);
        assertEquals("解析后的认证信息值长度应为0", 0, resolvedAuthBody.getTokenValue().length);
        log("空值认证信息Token解析成功，解析后认证信息值长度: " + resolvedAuthBody.getTokenValue().length);

        log("=== 空认证信息值测试完成 ===");
    }

    @After
    public void tearDown() throws Exception {
        log("开始清理测试环境...");
        tokenContext = null;
        log("TokenContext实例已释放");
        log("测试环境清理完成");
    }

    /**
     * 打印带日期和时间的测试日志的辅助方法
     */
    private void log(String message) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [TokenContextTest] " + message);
    }
}
