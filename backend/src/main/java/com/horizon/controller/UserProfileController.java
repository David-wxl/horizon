package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.entity.UserProfile;
import com.horizon.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户配置控制器
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    /**
     * 获取用户配置
     */
    @GetMapping("/{userId}")
    public Result<UserProfile> getProfile(@PathVariable Long userId) {
        try {
            UserProfile profile = userProfileService.getOrCreateProfile(userId);
            return Result.success(profile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据自定义URL获取配置
     */
    @GetMapping("/url/{customUrl}")
    public Result<UserProfile> getProfileByCustomUrl(@PathVariable String customUrl) {
        try {
            UserProfile profile = userProfileService.getByCustomUrl(customUrl);
            if (profile == null) {
                return Result.error("用户不存在");
            }
            return Result.success(profile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户配置
     */
    @PutMapping("/update")
    public Result<Boolean> updateProfile(@RequestBody UserProfile profile) {
        try {
            boolean updated = userProfileService.updateProfile(profile);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新统计信息
     */
    @PostMapping("/stats/{userId}")
    public Result<Void> updateStats(@PathVariable Long userId,
                                     @RequestParam(required = false) Integer totalCards,
                                     @RequestParam(required = false) Integer totalViews,
                                     @RequestParam(required = false) Integer totalLikes) {
        try {
            userProfileService.updateStats(userId, totalCards, totalViews, totalLikes);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
