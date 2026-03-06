package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.dto.LoginDTO;
import com.horizon.dto.RegisterDTO;
import com.horizon.entity.User;
import com.horizon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    /**
     * 检查用户名是否已存在
     */
    @GetMapping("/checkUsername")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        return Result.success(userService.checkUsernameExists(username));
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.success(List.of());
            }
            return Result.success(userService.searchUsers(keyword.trim()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Validated @RequestBody RegisterDTO registerDTO) {
        try {
            Map<String, Object> result = userService.register(registerDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> result = userService.login(loginDTO);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public Result<User> getUserInfo(@PathVariable Long userId) {
        try {
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            // 清除敏感信息
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新个人资料
     */
    @PostMapping("/updateProfile")
    public Result<User> updateProfile(@RequestBody User user) {
        try {
            // 处理空字符串字段：将空字符串转换为 null，避免数据库报错
            if (user.getBirthday() != null && user.getBirthday().trim().isEmpty()) {
                user.setBirthday(null);
            }
            if (user.getGender() != null && user.getGender().trim().isEmpty()) {
                user.setGender(null);
            }
            if (user.getLocation() != null && user.getLocation().trim().isEmpty()) {
                user.setLocation(null);
            }
            if (user.getBio() != null && user.getBio().trim().isEmpty()) {
                user.setBio(null);
            }
            
            boolean updated = userService.updateById(user);
            if (updated) {
                User updatedUser = userService.getById(user.getId());
                updatedUser.setPassword(null);
                return Result.success(updatedUser);
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> params) {
        try {
            Long userId = Long.parseLong(params.get("userId"));
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return Result.error("参数不完整");
            }
            if (newPassword.length() < 6) {
                return Result.error("新密码长度不能少于6位");
            }
            
            boolean changed = userService.changePassword(userId, oldPassword, newPassword);
            return Result.success(changed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
