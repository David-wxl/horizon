package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.entity.Comment;
import com.horizon.service.CommentService;
import com.horizon.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    /**
     * 创建评论
     */
    @PostMapping("/create")
    public Result<Comment> createComment(@RequestBody Comment comment) {
        try {
            Comment created = commentService.createComment(comment);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{commentId}")
    public Result<Boolean> deleteComment(@PathVariable Long commentId, 
                                          @RequestParam Long userId) {
        try {
            boolean deleted = commentService.deleteComment(commentId, userId);
            return Result.success(deleted);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取卡片的所有评论（一级评论，包含用户信息）
     */
    @GetMapping("/card/{cardId}")
    public Result<List<CommentVO>> getCardComments(@PathVariable Long cardId) {
        try {
            List<CommentVO> comments = commentService.getCardComments(cardId);
            return Result.success(comments);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取某评论的所有回复（二级评论）
     */
    @GetMapping("/replies/{parentId}")
    public Result<List<Comment>> getCommentReplies(@PathVariable Long parentId) {
        try {
            List<Comment> replies = commentService.getCommentReplies(parentId);
            return Result.success(replies);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 统计卡片评论数
     */
    @GetMapping("/count/{cardId}")
    public Result<Integer> countComments(@PathVariable Long cardId) {
        try {
            Integer count = commentService.countByCardId(cardId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
