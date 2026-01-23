package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 关注控制器
 */
@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;
    
    /**
     * 关注用户
     */
    @PostMapping("/user")
    public Result<Boolean> followUser(@RequestParam Long followerId,
                                       @RequestParam Long followingId) {
        try {
            boolean followed = followService.followUser(followerId, followingId);
            return Result.success(followed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 取消关注
     */
    @DeleteMapping("/user")
    public Result<Boolean> unfollowUser(@RequestParam Long followerId,
                                         @RequestParam Long followingId) {
        try {
            boolean unfollowed = followService.unfollowUser(followerId, followingId);
            return Result.success(unfollowed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否已关注
     */
    @GetMapping("/check")
    public Result<Boolean> checkFollow(@RequestParam Long followerId,
                                        @RequestParam Long followingId) {
        try {
            boolean hasFollowed = followService.hasFollowed(followerId, followingId);
            return Result.success(hasFollowed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 统计粉丝数
     */
    @GetMapping("/followers/{userId}")
    public Result<Integer> countFollowers(@PathVariable Long userId) {
        try {
            Integer count = followService.countFollowers(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 统计关注数
     */
    @GetMapping("/following/{userId}")
    public Result<Integer> countFollowing(@PathVariable Long userId) {
        try {
            Integer count = followService.countFollowing(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
