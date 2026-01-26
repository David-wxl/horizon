package com.horizon.vo;

import lombok.Data;

/**
 * 管理员统计数据VO
 */
@Data
public class AdminStatsVO {
    
    /**
     * 总用户数
     */
    private Long totalUsers;
    
    /**
     * 今日新增用户数
     */
    private Long todayNewUsers;
    
    /**
     * 总卡片数
     */
    private Long totalCards;
    
    /**
     * 今日新增卡片数
     */
    private Long todayNewCards;
    
    /**
     * 总评论数
     */
    private Long totalComments;
    
    /**
     * 总点赞数
     */
    private Long totalLikes;
    
    /**
     * API调用总数（模拟）
     */
    private Long apiCalls;
}
