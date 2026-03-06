package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public Result<Boolean> follow(@RequestParam Long followerId, @RequestParam Long followingId) {
        try {
            return Result.success(followService.follow(followerId, followingId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping
    public Result<Boolean> unfollow(@RequestParam Long followerId, @RequestParam Long followingId) {
        try {
            return Result.success(followService.unfollow(followerId, followingId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/check")
    public Result<Boolean> isFollowing(@RequestParam Long followerId, @RequestParam Long followingId) {
        try {
            return Result.success(followService.isFollowing(followerId, followingId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/stats/{userId}")
    public Result<Map<String, Object>> getFollowStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("followerCount", followService.getFollowerCount(userId));
            stats.put("followingCount", followService.getFollowingCount(userId));
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
