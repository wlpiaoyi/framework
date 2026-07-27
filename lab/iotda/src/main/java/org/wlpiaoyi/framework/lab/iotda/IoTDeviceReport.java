package org.wlpiaoyi.framework.lab.iotda;

import com.huaweicloud.sdk.iot.device.IoTDevice;
import com.huaweicloud.sdk.iot.device.client.requests.ServiceProperty;
import com.huaweicloud.sdk.iot.device.transport.ActionListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>华为云 IoTDA 设备属性上报构建器</p>
 * <p>
 * 本类采用 Builder 模式（流式 API），用于分步骤组装一个或多个服务(Service)的物模型属性，
 * 最终通过 {@link #report(IoTDevice)} 或 {@link #report(IoTDevice, ActionListener)}
 * 将属性批量上报到华为云 IoT 设备接入平台。
 * </p>
 * <p>典型使用流程：</p>
 * <pre>
 *     IoTDeviceReport.build()
 *         .service("serviceId_1", Map.of("temperature", 26.5))
 *         .startService("serviceId_2")
 *             .setProperty("humidity", 60)
 *         .stopService("serviceId_2")
 *         .report(device);
 * </pre>
 * <p><b>{@code @date:}</b>2026-07-02 11:27:12</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
class IoTDeviceReport {

    /**
     * 缓存所有待上报的服务属性，Key 为 serviceId，Value 为该服务下的属性集合。
     * 使用 Map 可在同一上报对象中同时维护多个服务的属性。
     */
    private final Map<String, ServiceProperty> propertyMap = new HashMap<>();

    /**
     * 当前正在编辑的服务属性，为 null 时表示未进入任何服务编辑上下文。
     */
    private ServiceProperty currentService = null;

    private IoTDeviceReport() {}

    /**
     * 创建一个新的设备属性上报构建器实例。
     *
     * @return IoTDeviceReport 实例
     */
    public static IoTDeviceReport build() {
        return new IoTDeviceReport();
    }

    /**
     * 开始编辑指定服务的属性。
     * <p>
     * 同一时刻只能有一个服务处于编辑状态；若当前已有服务在编辑中，且不是目标服务，
     * 则抛出运行时异常，提示先调用 {@link #stopService(String)} 结束上一个服务。
     * </p>
     *
     * @param serviceId 服务 ID（物模型中的 service_id）
     * @return 当前 IoTDeviceReport 实例，支持链式调用
     */
    public IoTDeviceReport startService(String serviceId) {
        if (this.currentService != null) {
            if (this.currentService.getServiceId().equals(serviceId)) {
                log.debug("service [{}] 已处于编辑状态，继续复用", serviceId);
                return this;
            }
            log.warn("尝试开始编辑 service [{}]，但当前 service [{}] 尚未结束", serviceId, this.currentService.getServiceId());
            throw new RuntimeException("请先结束上一个service");
        }
        synchronized (propertyMap) {
            if (this.currentService != null) {
                if (this.currentService.getServiceId().equals(serviceId)) {
                    log.debug("service [{}] 已处于编辑状态，继续复用", serviceId);
                    return this;
                }
                log.warn("尝试开始编辑 service [{}]，但当前 service [{}] 尚未结束", serviceId, this.currentService.getServiceId());
                throw new RuntimeException("请先结束上一个service");
            }
            ServiceProperty serviceProperty = this.propertyMap.get(serviceId);
            if (serviceProperty == null) {
                serviceProperty = new ServiceProperty();
                serviceProperty.setServiceId(serviceId);
                serviceProperty.setProperties(new HashMap<>());
                this.propertyMap.put(serviceId, serviceProperty);
                log.debug("新建 service [{}] 并进入编辑状态", serviceId);
            } else {
                log.debug("复用已存在的 service [{}] 并进入编辑状态", serviceId);
            }
            this.currentService = serviceProperty;
        }
        return this;
    }

    /**
     * 一次性完成“开始服务 -> 批量设置属性 -> 结束服务”的便捷方法。
     * <p>
     * 适用于只需要设置一次属性、不需要链式多次设置的场景。
     * </p>
     *
     * @param serviceId  服务 ID
     * @param properties 该服务下的属性键值对
     * @return 当前 IoTDeviceReport 实例，支持链式调用
     */
    public IoTDeviceReport service(String serviceId, Map<String, Object> properties) {
        synchronized (propertyMap) {
            return this.startService(serviceId)
                    .setAllProperty(properties)
                    .stopService(serviceId);
        }
    }

    /**
     * 批量替换当前服务下的所有属性。
     * <p>
     * 调用前必须先通过 {@link #startService(String)} 进入服务编辑上下文。
     * 该方法会先清空当前服务已有属性，再写入新的属性集合。
     * </p>
     *
     * @param properties 待写入的属性键值对
     * @return 当前 IoTDeviceReport 实例，支持链式调用
     */
    public IoTDeviceReport setAllProperty(Map<String, Object> properties) {
        if (this.currentService == null) {
            log.error("调用 setAllProperty 时未先调用 startService，无法设置属性");
            throw new RuntimeException("请先调用startService方法");
        }
        String serviceId = this.currentService.getServiceId();
        this.currentService.getProperties().clear();
        this.currentService.getProperties().putAll(properties);
        log.debug("service [{}] 的属性已被批量替换为: {}", serviceId, properties);
        return this;
    }

    /**
     * 为当前服务设置单个属性。
     * <p>
     * 调用前必须先通过 {@link #startService(String)} 进入服务编辑上下文。
     * 若该属性已存在则覆盖，不存在则新增。
     * </p>
     *
     * @param propertyId 属性 ID
     * @param value      属性值
     * @return 当前 IoTDeviceReport 实例，支持链式调用
     */
    public IoTDeviceReport setProperty(String propertyId, Object value) {
        if (this.currentService == null) {
            log.error("调用 setProperty 时未先调用 startService，无法设置属性");
            throw new RuntimeException("请先调用startService方法");
        }
        this.currentService.getProperties().put(propertyId, value);
        log.debug("service [{}] 设置属性 [{}] = {}", this.currentService.getServiceId(), propertyId, value);
        return this;
    }

    /**
     * 从当前服务中移除指定属性。
     * <p>
     * 调用前必须先通过 {@link #startService(String)} 进入服务编辑上下文。
     * </p>
     *
     * @param propertyId 待移除的属性 ID
     * @return 当前 IoTDeviceReport 实例，支持链式调用
     */
    public IoTDeviceReport removeProperty(String propertyId) {
        if (this.currentService == null) {
            log.error("调用 removeProperty 时未先调用 startService，无法移除属性");
            throw new RuntimeException("请先调用startService方法");
        }
        Object removed = this.currentService.getProperties().remove(propertyId);
        if (removed != null) {
            log.debug("service [{}] 移除属性 [{}]，原值为: {}", this.currentService.getServiceId(), propertyId, removed);
        } else {
            log.debug("service [{}] 中不存在属性 [{}]，无需移除", this.currentService.getServiceId(), propertyId);
        }
        return this;
    }

    /**
     * 结束当前服务的编辑状态。
     * <p>
     * 结束编辑后，{@link #currentService} 置为 null，允许开始编辑下一个服务。
     * </p>
     *
     * @param serviceId 服务 ID（当前仅用于保持 API 语义一致性）
     * @return 当前 IoTDeviceReport 实例，支持链式调用
     */
    public IoTDeviceReport stopService(String serviceId) {
        if (this.currentService != null) {
            log.debug("结束 service [{}] 的编辑状态", this.currentService.getServiceId());
        }
        this.currentService = null;
        return this;
    }

    /**
     * 使用默认回调将所有已组装的属性上报到华为云 IoT 平台。
     * <p>
     * 默认回调仅打印成功/失败日志，失败时附带异常堆栈。
     * 如需自定义上报结果处理，请使用 {@link #report(IoTDevice, ActionListener)}。
     * </p>
     *
     * @param device 已建立连接的华为云 IoT 设备实例
     * @return 当前 IoTDeviceReport 实例，支持链式调用
     */
    public IoTDeviceReport report(IoTDevice device) {
        this.report(device, new ActionListener() {
            @Override
            public void onSuccess(Object var1) {
                log.debug("deviceId:{} 属性上报成功", device.getDeviceId());
            }

            @Override
            public void onFailure(Object var1, Throwable var2) {
                log.error("deviceId:{} 属性上报失败", device.getDeviceId(), var2);
            }
        });
        return this;
    }

    /**
     * 将所有已组装的属性上报到华为云 IoT 平台，并使用自定义回调处理结果。
     *
     * @param device   已建立连接的华为云 IoT 设备实例
     * @param listener 上报结果监听器
     */
    public void report(IoTDevice device, ActionListener listener) {
        if (device == null) {
            log.error("上报失败：device 实例为空");
            throw new IllegalArgumentException("device 不能为空");
        }
        if (propertyMap.isEmpty()) {
            log.warn("deviceId:{} 没有可上报的属性，跳过上报", device.getDeviceId());
            return;
        }
        log.info("deviceId:{} 准备上报属性，共 {} 个服务", device.getDeviceId(), propertyMap.size());
        device.getClient().reportProperties(new ArrayList<>() {{
            addAll(propertyMap.values());
        }}, listener);
    }

}
