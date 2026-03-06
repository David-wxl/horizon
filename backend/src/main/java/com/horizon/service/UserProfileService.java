package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.UserProfile;
import com.horizon.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService extends ServiceImpl<UserProfileMapper, UserProfile> {

    public UserProfile getByUserId(Long userId) {
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = this.getOne(wrapper);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setPageTitle("我的展览馆");
            profile.setThemeColor("#f6d365");
            profile.setBgStyle("warm");
            profile.setTotalCards(0);
            profile.setTotalViews(0);
            profile.setTotalLikes(0);
            this.save(profile);
        }
        return profile;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateProfile(UserProfile profile) {
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, profile.getUserId());
        UserProfile existing = this.getOne(wrapper);
        if (existing != null) {
            profile.setId(existing.getId());
            return this.updateById(profile);
        } else {
            return this.save(profile);
        }
    }
}
