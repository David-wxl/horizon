package com.horizon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.horizon.entity.BentoCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Bento卡片Mapper
 */
@Mapper
public interface BentoCardMapper extends BaseMapper<BentoCard> {
    
    /**
     * 根据用户ID查询公开的卡片（用于用户主页）
     */
    List<BentoCard> selectPublicCardsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询广场卡片（按创建时间倒序）
     */
    List<BentoCard> selectSquareCards(@Param("limit") Integer limit, @Param("offset") Integer offset);
    
    /**
     * 根据分类查询卡片
     */
    List<BentoCard> selectCardsByCategory(@Param("userId") Long userId, @Param("category") String category);
    
    /**
     * 统计用户卡片数
     */
    Integer countUserCards(@Param("userId") Long userId);
}
