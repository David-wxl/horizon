package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.BentoCard;
import com.horizon.entity.User;
import com.horizon.mapper.BentoCardMapper;
import com.horizon.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Bento卡片服务
 */
@Service
@RequiredArgsConstructor
public class BentoCardService extends ServiceImpl<BentoCardMapper, BentoCard> {
    
    private final BentoCardMapper bentoCardMapper;
    private final UserMapper userMapper;
    
    /**
     * 创建卡片
     */
    @Transactional(rollbackFor = Exception.class)
    public BentoCard createCard(BentoCard card) {
        // 设置默认值
        if (card.getIsPublic() == null) {
            card.setIsPublic(1);
        }
        if (card.getStatus() == null) {
            card.setStatus(0);
        }
        if (card.getLikeCount() == null) {
            card.setLikeCount(0);
        }
        if (card.getViewCount() == null) {
            card.setViewCount(0);
        }
        if (card.getCommentCount() == null) {
            card.setCommentCount(0);
        }
        if (card.getIsPinned() == null) {
            card.setIsPinned(0);
        }
        
        this.save(card);
        return card;
    }
    
    /**
     * 更新卡片
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCard(BentoCard card) {
        return this.updateById(card);
    }
    
    /**
     * 删除卡片（支持作者和管理员删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCard(Long cardId, Long userId) {
        // 验证卡片是否存在
        BentoCard card = this.getById(cardId);
        if (card == null) {
            throw new RuntimeException("卡片不存在");
        }
        
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查权限：作者或管理员可以删除
        boolean isOwner = card.getUserId().equals(userId);
        boolean isAdmin = "ADMIN".equals(user.getRole());
        
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("无权删除此卡片（作者ID=" + card.getUserId() + ", 用户ID=" + userId + ", 角色=" + user.getRole() + "）");
        }
        
        return this.removeById(cardId);
    }
    
    /**
     * 获取用户的所有卡片
     */
    public List<BentoCard> getUserCards(Long userId, Boolean publicOnly) {
        LambdaQueryWrapper<BentoCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BentoCard::getUserId, userId);
        if (publicOnly != null && publicOnly) {
            wrapper.eq(BentoCard::getIsPublic, 1);
            wrapper.eq(BentoCard::getStatus, 1);
        }
        wrapper.orderByDesc(BentoCard::getIsPinned)
               .orderByAsc(BentoCard::getSortOrder)
               .orderByDesc(BentoCard::getCreateTime);
        return this.list(wrapper);
    }
    
    /**
     * 根据分类获取卡片
     */
    public List<BentoCard> getCardsByCategory(Long userId, String category) {
        LambdaQueryWrapper<BentoCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BentoCard::getUserId, userId)
               .eq(BentoCard::getCategory, category)
               .eq(BentoCard::getStatus, 1)  // 只显示已通过审核的卡片
               .orderByDesc(BentoCard::getCreateTime);
        
        return this.list(wrapper);
    }
    
    /**
     * 获取广场卡片（分页）
     */
    public Page<BentoCard> getSquareCards(Integer pageNum, Integer pageSize) {
        Page<BentoCard> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BentoCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BentoCard::getIsPublic, 1)
               .eq(BentoCard::getStatus, 1)  // 只显示已通过审核的卡片（status=1）
               .orderByDesc(BentoCard::getCreateTime);
        
        return this.page(page, wrapper);
    }
    
    /**
     * 增加查看数
     */
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long cardId) {
        BentoCard card = this.getById(cardId);
        if (card != null) {
            card.setViewCount(card.getViewCount() + 1);
            this.updateById(card);
        }
    }
    
    /**
     * 更新点赞数
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLikeCount(Long cardId, Integer delta) {
        BentoCard card = this.getById(cardId);
        if (card != null) {
            card.setLikeCount(Math.max(0, card.getLikeCount() + delta));
            this.updateById(card);
        }
    }
    
    /**
     * 更新评论数
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCommentCount(Long cardId, Integer delta) {
        BentoCard card = this.getById(cardId);
        if (card != null) {
            card.setCommentCount(Math.max(0, card.getCommentCount() + delta));
            this.updateById(card);
        }
    }
    
    /**
     * 统计用户卡片数
     */
    public Integer countUserCards(Long userId) {
        LambdaQueryWrapper<BentoCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BentoCard::getUserId, userId);
        return (int) this.count(wrapper);
    }
}
