package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    
    private final LikeService likeService;
    
    /**
     * 点赞卡片
     */
    @PostMapping("/card")
    public Result<Boolean> likeCard(@RequestParam Long userId,
                                     @RequestParam Long cardId,
                                     @RequestParam Long cardOwnerId) {
        try {
            boolean liked = likeService.likeCard(userId, cardId, cardOwnerId);
            return Result.success(liked);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 取消点赞
     */
    @DeleteMapping("/card")
    public Result<Boolean> unlikeCard(@RequestParam Long userId,
                                       @RequestParam Long cardId) {
        try {
            boolean unliked = likeService.unlikeCard(userId, cardId);
            return Result.success(unliked);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否已点赞
     */
    @GetMapping("/check")
    public Result<Boolean> checkLike(@RequestParam Long userId,
                                      @RequestParam Long cardId) {
        try {
            boolean hasLiked = likeService.hasLiked(userId, cardId);
            return Result.success(hasLiked);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 统计卡片点赞数
     */
    @GetMapping("/count/{cardId}")
    public Result<Integer> countLikes(@PathVariable Long cardId) {
        try {
            Integer count = likeService.countByCardId(cardId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
