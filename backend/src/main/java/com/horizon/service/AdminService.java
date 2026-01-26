package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.horizon.entity.BentoCard;
import com.horizon.entity.Comment;
import com.horizon.entity.Like;
import com.horizon.entity.User;
import com.horizon.mapper.BentoCardMapper;
import com.horizon.mapper.CommentMapper;
import com.horizon.mapper.LikeMapper;
import com.horizon.mapper.UserMapper;
import com.horizon.vo.AdminStatsVO;
import com.horizon.vo.SystemMonitorVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 管理员服务
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserMapper userMapper;
    private final BentoCardMapper bentoCardMapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    
    /**
     * 获取统计数据
     */
    public AdminStatsVO getStats() {
        AdminStatsVO stats = new AdminStatsVO();
        
        // 总用户数
        stats.setTotalUsers(userMapper.selectCount(null));
        
        // 今日新增用户
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.ge(User::getCreateTime, todayStart);
        stats.setTodayNewUsers(userMapper.selectCount(userWrapper));
        
        // 总卡片数
        stats.setTotalCards(bentoCardMapper.selectCount(null));
        
        // 今日新增卡片
        LambdaQueryWrapper<BentoCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.ge(BentoCard::getCreateTime, todayStart);
        stats.setTodayNewCards(bentoCardMapper.selectCount(cardWrapper));
        
        // 总评论数
        stats.setTotalComments(commentMapper.selectCount(null));
        
        // 总点赞数
        stats.setTotalLikes(likeMapper.selectCount(null));
        
        // API调用总数（模拟数据）
        stats.setApiCalls(stats.getTotalCards() * 3 + stats.getTotalComments() * 2 + stats.getTotalLikes());
        
        return stats;
    }
    
    /**
     * 获取系统监控数据
     */
    public SystemMonitorVO getMonitor() {
        SystemMonitorVO monitor = new SystemMonitorVO();
        
        // 获取操作系统信息
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getSystemLoadAverage();
        monitor.setCpuUsage(cpuLoad > 0 ? cpuLoad * 10 : Math.random() * 40 + 10); // 模拟CPU使用率
        
        // 获取内存信息
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long totalMemory = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024); // MB
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024); // MB
        
        monitor.setTotalMemory(totalMemory);
        monitor.setUsedMemory(usedMemory);
        monitor.setMemoryUsage((double) usedMemory / totalMemory * 100);
        
        // 存储空间（模拟数据）
        monitor.setTotalStorage(100.0); // 100GB
        monitor.setUsedStorage(45.5 + Math.random() * 10); // 45-55GB
        monitor.setStorageUsage(monitor.getUsedStorage() / monitor.getTotalStorage() * 100);
        
        return monitor;
    }
}
