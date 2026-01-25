package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.dto.LoginDTO;
import com.horizon.dto.RegisterDTO;
import com.horizon.entity.User;
import com.horizon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * 测试接口
     */
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("HORIZON Backend is running!");
    }
}
