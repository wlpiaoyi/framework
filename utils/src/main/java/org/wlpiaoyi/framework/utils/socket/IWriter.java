package org.wlpiaoyi.framework.utils.socket;

public interface IWriter {

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * TODO
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>clientId</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>writeBytes</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>Len</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 20:42</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    void write(int clientId, byte[] writeBytes, int Len);

    String getServerHost();

    int getServerPort();

}
