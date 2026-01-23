package com.horizon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 关注实体类
 * 记录用户之间的关注关系
 */
@Data
@TableName("t_follow")
public class Follow {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关注者ID（我）
     */
    private Long followerId;
    
    /**
     * 被关注者ID（Ta）
     */
    private Long followingId;
    
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
