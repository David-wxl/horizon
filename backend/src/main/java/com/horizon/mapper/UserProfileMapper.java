package com.horizon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.horizon.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户配置Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
