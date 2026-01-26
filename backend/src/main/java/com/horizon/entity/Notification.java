package com.horizon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
@TableName("t_notification")
public class Notification {
    
    /**
     * 通知ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 接收用户ID
     */
    private Long userId;
    
    /**
     * 通知类型：audit_pass(审核通过)、audit_reject(审核拒绝)、comment(评论)、like(点赞)、system(系统通知)
     */
    private String type;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 关联卡片ID（可选）
     */
    private Long cardId;
    
    /**
     * 关联卡片标题（冗余存储，方便显示）
     */
    private String cardTitle;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
}
