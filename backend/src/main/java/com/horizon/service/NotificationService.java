package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.Notification;
import com.horizon.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务
 */
@Service
@RequiredArgsConstructor
public class NotificationService extends ServiceImpl<NotificationMapper, Notification> {
    
    private final NotificationMapper notificationMapper;
    
    /**
     * 创建审核通过通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAuditPassNotification(Long userId, Long cardId, String cardTitle) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("audit_pass");
        notification.setTitle("✅ 审核通过");
        notification.setContent("您的卡片《" + cardTitle + "》已通过审核，现已公开展示");
        notification.setCardId(cardId);
        notification.setCardTitle(cardTitle);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        this.save(notification);
    }
    
    /**
     * 创建审核拒绝通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAuditRejectNotification(Long userId, Long cardId, String cardTitle, String reason) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("audit_reject");
        notification.setTitle("❌ 审核未通过");
        notification.setContent("您的卡片《" + cardTitle + "》未通过审核。原因：" + (reason != null && !reason.trim().isEmpty() ? reason : "内容不符合社区规范"));
        notification.setCardId(cardId);
        notification.setCardTitle(cardTitle);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        this.save(notification);
    }
    
    /**
     * 获取用户的通知列表
     */
    public List<Notification> getUserNotifications(Long userId, Integer limit) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .orderByDesc(Notification::getCreateTime);
        
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        
        return this.list(wrapper);
    }
    
    /**
     * 获取未读通知数量
     */
    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, 0);
        return this.count(wrapper);
    }
    
    /**
     * 标记单个通知为已读
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long notificationId, Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getId, notificationId)
               .eq(Notification::getUserId, userId)
               .set(Notification::getIsRead, 1)
               .set(Notification::getReadTime, LocalDateTime.now());
        return this.update(wrapper);
    }
    
    /**
     * 标记所有通知为已读
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Notification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsRead, 0)
               .set(Notification::getIsRead, 1)
               .set(Notification::getReadTime, LocalDateTime.now());
        return this.update(wrapper);
    }
    
    /**
     * 删除通知
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotification(Long notificationId, Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getId, notificationId)
               .eq(Notification::getUserId, userId);
        return this.remove(wrapper);
    }
}
