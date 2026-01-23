package com.horizon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 点赞实体类
 * 记录用户对卡片的点赞行为
 */
@Data
@TableName("t_like")
public class Like {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 点赞用户ID
     */
    private Long userId;
    
    /**
     * 被点赞卡片ID
     */
    private Long cardId;
    
    /**
     * 卡片所有者ID（冗余字段，方便查询）
     */
    private Long cardOwnerId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
