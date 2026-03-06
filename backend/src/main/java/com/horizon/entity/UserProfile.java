package com.horizon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_user_profile")
public class UserProfile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String pageTitle;
    
    private String bio;
    
    private String customUrl;
    
    private String themeColor;
    
    private String bgStyle;
    
    private Integer totalCards;
    
    private Integer totalViews;
    
    private Integer totalLikes;
    
    private String github;
    
    private String twitter;
    
    private String website;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
