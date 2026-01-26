package com.horizon.vo;

import lombok.Data;

/**
 * 系统监控数据VO
 */
@Data
public class SystemMonitorVO {
    
    /**
     * CPU负载百分比
     */
    private Double cpuUsage;
    
    /**
     * 内存占用百分比
     */
    private Double memoryUsage;
    
    /**
     * 内存总量（MB）
     */
    private Long totalMemory;
    
    /**
     * 已用内存（MB）
     */
    private Long usedMemory;
    
    /**
     * 存储空间总量（GB）
     */
    private Double totalStorage;
    
    /**
     * 已用存储空间（GB）
     */
    private Double usedStorage;
    
    /**
     * 存储使用百分比
     */
    private Double storageUsage;
}
