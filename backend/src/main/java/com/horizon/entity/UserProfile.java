package com.horizon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户个人主页配置实体类
 * 存储用户的个性化配置信息
 */
@Data
@TableName("t_user_profile")
public class UserProfile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID（唯一）
     */
    private Long userId;
    
    /**
     * 个人主页标题
     */
    private String pageTitle;
    
    /**
     * 个人简介
     */
    private String bio;
    
    /**
     * 自定义URL（用户名缩写）
     * 例如：horizon.com/demo
     */
    private String customUrl;
    
    /**
     * 主题色（十六进制）
     */
    private String themeColor;
    
    /**
     * 背景风格：warm/cool/dark
     */
    private String bgStyle;
    
    /**
     * 总卡片数（冗余统计字段）
     */
    private Integer totalCards;
    
    /**
     * 总浏览量
     */
    private Integer totalViews;
    
    /**
     * 总点赞数
     */
    private Integer totalLikes;
    
    /**
     * GitHub
     */
    private String github;
    
    /**
     * Twitter
     */
    private String twitter;
    
    /**
     * 个人网站
     */
    private String website;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
