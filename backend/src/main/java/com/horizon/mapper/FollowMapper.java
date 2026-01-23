package com.horizon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.horizon.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 关注Mapper
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {
    
    /**
     * 检查是否已关注
     */
    Follow selectByFollowerAndFollowing(@Param("followerId") Long followerId, 
                                        @Param("followingId") Long followingId);
    
    /**
     * 统计关注者数量（粉丝数）
     */
    Integer countFollowers(@Param("userId") Long userId);
    
    /**
     * 统计关注数量
     */
    Integer countFollowing(@Param("userId") Long userId);
}
