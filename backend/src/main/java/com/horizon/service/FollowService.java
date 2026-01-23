package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.Follow;
import com.horizon.mapper.FollowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 关注服务
 */
@Service
public class FollowService extends ServiceImpl<FollowMapper, Follow> {
    
    /**
     * 关注用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean followUser(Long followerId, Long followingId) {
        // 不能关注自己
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }
        
        // 检查是否已关注
        if (hasFollowed(followerId, followingId)) {
            throw new RuntimeException("已经关注过了");
        }
        
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        
        return this.save(follow);
    }
    
    /**
     * 取消关注
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unfollowUser(Long followerId, Long followingId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, followerId)
               .eq(Follow::getFollowingId, followingId);
        
        return this.remove(wrapper);
    }
    
    /**
     * 检查是否已关注
     */
    public boolean hasFollowed(Long followerId, Long followingId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, followerId)
               .eq(Follow::getFollowingId, followingId);
        return this.count(wrapper) > 0;
    }
    
    /**
     * 统计粉丝数
     */
    public Integer countFollowers(Long userId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowingId, userId);
        return (int) this.count(wrapper);
    }
    
    /**
     * 统计关注数
     */
    public Integer countFollowing(Long userId) {
        LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follow::getFollowerId, userId);
        return (int) this.count(wrapper);
    }
}
