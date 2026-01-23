package com.horizon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.horizon.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞Mapper
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {
    
    /**
     * 检查用户是否已点赞某卡片
     */
    Like selectByUserIdAndCardId(@Param("userId") Long userId, @Param("cardId") Long cardId);
    
    /**
     * 统计卡片点赞数
     */
    Integer countByCardId(@Param("cardId") Long cardId);
}
