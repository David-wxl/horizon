package com.horizon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体类
 * 支持二级评论（回复）
 */
@Data
@TableName("t_comment")
public class Comment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 被评论卡片ID
     */
    private Long cardId;
    
    /**
     * 评论用户ID
     */
    private Long userId;
    
    /**
     * 卡片所有者ID
     */
    private Long cardOwnerId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID（0表示一级评论）
     */
    private Long parentId;
    
    /**
     * 回复给谁（二级评论时有值）
     */
    private Long replyToUserId;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 状态：0-正常，1-已删除，2-被举报
     */
    private Integer status;
    
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
