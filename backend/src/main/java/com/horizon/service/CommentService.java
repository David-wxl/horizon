package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.Comment;
import com.horizon.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务
 */
@Service
@RequiredArgsConstructor
public class CommentService extends ServiceImpl<CommentMapper, Comment> {
    
    private final BentoCardService bentoCardService;
    
    /**
     * 创建评论
     */
    @Transactional(rollbackFor = Exception.class)
    public Comment createComment(Comment comment) {
        // 设置默认值
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0);
        }
        if (comment.getStatus() == null) {
            comment.setStatus(0);
        }
        
        boolean saved = this.save(comment);
        
        // 更新卡片评论数
        if (saved) {
            bentoCardService.updateCommentCount(comment.getCardId(), 1);
        }
        
        return comment;
    }
    
    /**
     * 删除评论（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        // 验证评论所有权
        Comment comment = this.getById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }
        
        boolean removed = this.removeById(commentId);
        
        // 更新卡片评论数
        if (removed) {
            bentoCardService.updateCommentCount(comment.getCardId(), -1);
        }
        
        return removed;
    }
    
    /**
     * 获取卡片的所有评论（一级评论）
     */
    public List<Comment> getCardComments(Long cardId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCardId, cardId)
               .eq(Comment::getParentId, 0)
               .eq(Comment::getStatus, 0)
               .orderByDesc(Comment::getCreateTime);
        
        return this.list(wrapper);
    }
    
    /**
     * 获取某评论的所有回复（二级评论）
     */
    public List<Comment> getCommentReplies(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId)
               .eq(Comment::getStatus, 0)
               .orderByAsc(Comment::getCreateTime);
        
        return this.list(wrapper);
    }
    
    /**
     * 统计卡片评论数
     */
    public Integer countByCardId(Long cardId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCardId, cardId)
               .eq(Comment::getStatus, 0);
        return (int) this.count(wrapper);
    }
}
