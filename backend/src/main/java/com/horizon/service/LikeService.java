package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.Like;
import com.horizon.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞服务
 */
@Service
@RequiredArgsConstructor
public class LikeService extends ServiceImpl<LikeMapper, Like> {
    
    private final BentoCardService bentoCardService;
    
    /**
     * 点赞卡片
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean likeCard(Long userId, Long cardId, Long cardOwnerId) {
        // 检查是否已点赞
        if (hasLiked(userId, cardId)) {
            throw new RuntimeException("已经点赞过了");
        }
        
        // 创建点赞记录
        Like like = new Like();
        like.setUserId(userId);
        like.setCardId(cardId);
        like.setCardOwnerId(cardOwnerId);
        
        boolean saved = this.save(like);
        
        // 更新卡片点赞数
        if (saved) {
            bentoCardService.updateLikeCount(cardId, 1);
        }
        
        return saved;
    }
    
    /**
     * 取消点赞
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeCard(Long userId, Long cardId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
               .eq(Like::getCardId, cardId);
        
        boolean removed = this.remove(wrapper);
        
        // 更新卡片点赞数
        if (removed) {
            bentoCardService.updateLikeCount(cardId, -1);
        }
        
        return removed;
    }
    
    /**
     * 检查用户是否已点赞某卡片
     */
    public boolean hasLiked(Long userId, Long cardId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
               .eq(Like::getCardId, cardId);
        return this.count(wrapper) > 0;
    }
    
    /**
     * 统计卡片点赞数
     */
    public Integer countByCardId(Long cardId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getCardId, cardId);
        return (int) this.count(wrapper);
    }
}
