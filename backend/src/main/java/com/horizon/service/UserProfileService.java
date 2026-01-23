package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.UserProfile;
import com.horizon.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户配置服务
 */
@Service
@RequiredArgsConstructor
public class UserProfileService extends ServiceImpl<UserProfileMapper, UserProfile> {
    
    /**
     * 获取用户配置（如果不存在则创建默认配置）
     */
    public UserProfile getOrCreateProfile(Long userId) {
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = this.getOne(wrapper);
        
        if (profile == null) {
            profile = createDefaultProfile(userId);
        }
        
        return profile;
    }
    
    /**
     * 创建默认配置
     */
    @Transactional(rollbackFor = Exception.class)
    public UserProfile createDefaultProfile(Long userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setPageTitle("我的展览馆");
        profile.setThemeColor("#f6d365");
        profile.setBgStyle("warm");
        profile.setTotalCards(0);
        profile.setTotalViews(0);
        profile.setTotalLikes(0);
        
        this.save(profile);
        return profile;
    }
    
    /**
     * 更新用户配置
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProfile(UserProfile profile) {
        return this.updateById(profile);
    }
    
    /**
     * 更新统计信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStats(Long userId, Integer totalCards, Integer totalViews, Integer totalLikes) {
        UserProfile profile = getOrCreateProfile(userId);
        
        if (totalCards != null) {
            profile.setTotalCards(totalCards);
        }
        if (totalViews != null) {
            profile.setTotalViews(totalViews);
        }
        if (totalLikes != null) {
            profile.setTotalLikes(totalLikes);
        }
        
        this.updateById(profile);
    }
    
    /**
     * 增加浏览量
     */
    @Transactional(rollbackFor = Exception.class)
    public void incrementTotalViews(Long userId, Integer count) {
        UserProfile profile = getOrCreateProfile(userId);
        profile.setTotalViews(profile.getTotalViews() + count);
        this.updateById(profile);
    }
    
    /**
     * 根据自定义URL获取配置
     */
    public UserProfile getByCustomUrl(String customUrl) {
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getCustomUrl, customUrl);
        return this.getOne(wrapper);
    }
}
