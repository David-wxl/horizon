package com.horizon.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horizon.common.Result;
import com.horizon.entity.BentoCard;
import com.horizon.entity.Comment;
import com.horizon.entity.User;
import com.horizon.mapper.UserMapper;
import com.horizon.service.AdminService;
import com.horizon.service.BentoCardService;
import com.horizon.service.CommentService;
import com.horizon.service.UserService;
import com.horizon.vo.AdminStatsVO;
import com.horizon.vo.SystemMonitorVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    private final BentoCardService bentoCardService;
    private final CommentService commentService;
    private final UserService userService;
    private final UserMapper userMapper;
    
    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public Result<AdminStatsVO> getStats() {
        try {
            AdminStatsVO stats = adminService.getStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取系统监控数据
     */
    @GetMapping("/monitor")
    public Result<SystemMonitorVO> getMonitor() {
        try {
            SystemMonitorVO monitor = adminService.getMonitor();
            return Result.success(monitor);
        } catch (Exception e) {
            return Result.error("获取监控数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取审核列表（最新卡片和评论）
     */
    @GetMapping("/audit-list")
    public Result<Map<String, Object>> getAuditList() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 获取最新10条卡片
            Page<BentoCard> cardPage = new Page<>(1, 10);
            LambdaQueryWrapper<BentoCard> cardWrapper = new LambdaQueryWrapper<>();
            cardWrapper.orderByDesc(BentoCard::getCreateTime);
            bentoCardService.page(cardPage, cardWrapper);
            
            // 获取最新10条评论
            Page<Comment> commentPage = new Page<>(1, 10);
            LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
            commentWrapper.orderByDesc(Comment::getCreateTime);
            commentService.page(commentPage, commentWrapper);
            
            result.put("recentCards", cardPage.getRecords());
            result.put("recentComments", commentPage.getRecords());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取审核列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 禁用/启用用户
     */
    @PostMapping("/user/toggle-status")
    public Result<Boolean> toggleUserStatus(@RequestParam Long userId) {
        try {
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 切换状态：0-正常 1-禁用
            user.setStatus(user.getStatus() == 0 ? 1 : 0);
            boolean updated = userService.updateById(user);
            
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除卡片（管理员）
     */
    @DeleteMapping("/card/delete/{cardId}")
    public Result<Boolean> deleteCard(@PathVariable Long cardId) {
        try {
            boolean deleted = bentoCardService.removeById(cardId);
            return Result.success(deleted);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除评论（管理员）
     */
    @DeleteMapping("/comment/delete/{commentId}")
    public Result<Boolean> deleteComment(@PathVariable Long commentId) {
        try {
            boolean deleted = commentService.removeById(commentId);
            return Result.success(deleted);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard-stats")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总用户数
            stats.put("totalUsers", userMapper.selectCount(null));
            
            // 总卡片数
            LambdaQueryWrapper<BentoCard> cardWrapper = new LambdaQueryWrapper<>();
            stats.put("totalCards", bentoCardService.count(cardWrapper));
            
            // 总评论数
            LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
            stats.put("totalComments", commentService.count(commentWrapper));
            
            // 待审核数量（status = 0）
            LambdaQueryWrapper<BentoCard> pendingWrapper = new LambdaQueryWrapper<>();
            pendingWrapper.eq(BentoCard::getStatus, 0);
            stats.put("pendingAudit", bentoCardService.count(pendingWrapper));
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取审核队列（待审核的卡片，status=0）
     */
    @GetMapping("/audit-queue")
    public Result<List<Map<String, Object>>> getAuditQueue() {
        try {
            LambdaQueryWrapper<BentoCard> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BentoCard::getStatus, 0)  // 只获取待审核的卡片
                   .orderByDesc(BentoCard::getCreateTime)
                   .last("LIMIT 10");
            
            List<BentoCard> cards = bentoCardService.list(wrapper);
            
            List<Map<String, Object>> result = cards.stream().map(card -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", card.getId());
                item.put("title", card.getTitle());
                item.put("cardType", card.getCardType());
                item.put("status", card.getStatus());
                item.put("createTime", card.getCreateTime());
                
                // 获取作者信息
                User author = userMapper.selectById(card.getUserId());
                item.put("authorName", author != null ? author.getNickname() : "未知用户");
                
                return item;
            }).collect(Collectors.toList());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取审核队列失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取已通过的卡片列表
     */
    @GetMapping("/approved-cards")
    public Result<List<Map<String, Object>>> getApprovedCards() {
        try {
            LambdaQueryWrapper<BentoCard> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BentoCard::getStatus, 1)  // status=1表示已通过
                   .orderByDesc(BentoCard::getCreateTime)
                   .last("LIMIT 10");
            
            List<BentoCard> cards = bentoCardService.list(wrapper);
            
            List<Map<String, Object>> result = cards.stream().map(card -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", card.getId());
                item.put("title", card.getTitle());
                item.put("cardType", card.getCardType());
                item.put("status", card.getStatus());
                item.put("createTime", card.getCreateTime());
                
                // 获取作者信息
                User author = userMapper.selectById(card.getUserId());
                item.put("authorName", author != null ? author.getNickname() : "未知用户");
                
                return item;
            }).collect(Collectors.toList());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取已通过卡片失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取周活动数据（过去7天每天的新增卡片数）
     */
    @GetMapping("/weekly-activity")
    public Result<List<Map<String, Object>>> getWeeklyActivity() {
        try {
            List<Map<String, Object>> weekData = new ArrayList<>();
            LocalDate today = LocalDate.now();
            
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
                
                LambdaQueryWrapper<BentoCard> wrapper = new LambdaQueryWrapper<>();
                wrapper.between(BentoCard::getCreateTime, dayStart, dayEnd);
                long count = bentoCardService.count(wrapper);
                
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", date.toString());
                dayData.put("dayOfWeek", date.getDayOfWeek().toString().substring(0, 1));
                dayData.put("count", count);
                
                weekData.add(dayData);
            }
            
            return Result.success(weekData);
        } catch (Exception e) {
            return Result.error("获取周活动数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 审核卡片（通过/拒绝）
     */
    @PostMapping("/audit/{cardId}")
    public Result<Boolean> auditCard(@PathVariable Long cardId, @RequestParam Integer status) {
        try {
            BentoCard card = bentoCardService.getById(cardId);
            if (card == null) {
                return Result.error("卡片不存在");
            }
            
            // status: 1=通过, 2=拒绝/封禁
            card.setStatus(status);
            boolean updated = bentoCardService.updateById(card);
            
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("审核失败: " + e.getMessage());
        }
    }
}
