package com.horizon.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论视图对象（包含用户信息）
 */
@Data
public class CommentVO {
    private Long id;
    private Long cardId;
    private Long userId;
    private Long cardOwnerId;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    
    // 用户信息
    private String username;
    private String nickname;
    private String avatar;
}
