package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.entity.Notification;
import com.horizon.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * 获取用户通知列表
     */
    @GetMapping("/list")
    public Result<List<Notification>> getNotifications(@RequestParam Long userId,
                                                       @RequestParam(required = false) Integer limit) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId, limit);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error("获取通知列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> getUnreadCount(@RequestParam Long userId) {
        try {
            long count = notificationService.getUnreadCount(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取未读数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记单个通知为已读
     */
    @PutMapping("/read/{notificationId}")
    public Result<Boolean> markAsRead(@PathVariable Long notificationId,
                                     @RequestParam Long userId) {
        try {
            boolean success = notificationService.markAsRead(notificationId, userId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("标记已读失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Boolean> markAllAsRead(@RequestParam Long userId) {
        try {
            boolean success = notificationService.markAllAsRead(userId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("标记全部已读失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/delete/{notificationId}")
    public Result<Boolean> deleteNotification(@PathVariable Long notificationId,
                                              @RequestParam Long userId) {
        try {
            boolean success = notificationService.deleteNotification(notificationId, userId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("删除通知失败: " + e.getMessage());
        }
    }
}
