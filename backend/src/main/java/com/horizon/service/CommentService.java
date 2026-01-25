package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.Comment;
import com.horizon.entity.User;
import com.horizon.mapper.CommentMapper;
import com.horizon.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务
 */
@Service
@RequiredArgsConstructor
public class CommentService extends ServiceImpl<CommentMapper, Comment> {
    
    private final BentoCardService bentoCardService;
    private final UserService userService;
    
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
     * 获取卡片的所有评论（一级评论，包含用户信息）
     */
    public List<CommentVO> getCardComments(Long cardId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCardId, cardId)
               .eq(Comment::getParentId, 0)
               .eq(Comment::getStatus, 0)
               .orderByDesc(Comment::getCreateTime);
        
        List<Comment> comments = this.list(wrapper);
        
        // 转换为 CommentVO 并填充用户信息
        return comments.stream().map(comment -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(comment, vo);
            
            // 获取用户信息
            User user = userService.getById(comment.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            
            return vo;
        }).collect(Collectors.toList());
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
