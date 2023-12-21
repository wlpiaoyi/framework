package org.wlpiaoyi.framework.utils.security;


import lombok.Getter;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/21 10:31
 * {@code @version:}:       1.0
 */
public abstract class Security {

    /**
     * 初始化加密类配置
     */
    abstract <T extends Security> T loadConfig();
}
