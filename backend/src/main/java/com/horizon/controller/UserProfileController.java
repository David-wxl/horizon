package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.entity.UserProfile;
import com.horizon.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public Result<UserProfile> getProfile(@PathVariable Long userId) {
        try {
            return Result.success(userProfileService.getByUserId(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<Boolean> updateProfile(@RequestBody UserProfile profile) {
        try {
            return Result.success(userProfileService.updateProfile(profile));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
