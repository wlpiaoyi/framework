package org.wlpiaoyi.framework.utils.socket;

public interface IReader {

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * TODO
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>writer</b>
     * {@link IWriter}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>clientId</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>readBytes</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>readLen</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 20:48</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    void read(IWriter writer, int clientId, byte[] readBytes, int readLen);

}
