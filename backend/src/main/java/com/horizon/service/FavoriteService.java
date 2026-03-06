package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.Favorite;
import com.horizon.entity.BentoCard;
import com.horizon.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService extends ServiceImpl<FavoriteMapper, Favorite> {

    private final BentoCardService bentoCardService;

    @Transactional(rollbackFor = Exception.class)
    public boolean addFavorite(Long userId, Long cardId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getCardId, cardId);
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("已收藏");
        }
        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setCardId(cardId);
        return this.save(fav);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeFavorite(Long userId, Long cardId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getCardId, cardId);
        return this.remove(wrapper);
    }

    public boolean isFavorited(Long userId, Long cardId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getCardId, cardId);
        return this.count(wrapper) > 0;
    }

    public List<BentoCard> getUserFavorites(Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime);
        List<Favorite> favs = this.list(wrapper);
        List<Long> cardIds = favs.stream().map(Favorite::getCardId).collect(Collectors.toList());
        if (cardIds.isEmpty()) return List.of();
        return bentoCardService.listByIds(cardIds);
    }

    public long countByCard(Long cardId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getCardId, cardId);
        return this.count(wrapper);
    }
}
