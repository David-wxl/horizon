package com.horizon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horizon.common.Result;
import com.horizon.entity.BentoCard;
import com.horizon.service.BentoCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Bento卡片控制器
 */
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class BentoCardController {
    
    private final BentoCardService bentoCardService;
    
    /**
     * 创建卡片
     */
    @PostMapping("/create")
    public Result<BentoCard> createCard(@RequestBody BentoCard card) {
        try {
            BentoCard created = bentoCardService.createCard(card);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新卡片
     */
    @PutMapping("/update")
    public Result<Boolean> updateCard(@RequestBody BentoCard card) {
        try {
            boolean updated = bentoCardService.updateCard(card);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除卡片
     */
    @DeleteMapping("/delete/{cardId}")
    public Result<Boolean> deleteCard(@PathVariable Long cardId, @RequestParam Long userId) {
        try {
            boolean deleted = bentoCardService.deleteCard(cardId, userId);
            return Result.success(deleted);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取卡片详情
     */
    @GetMapping("/detail/{cardId}")
    public Result<BentoCard> getCardDetail(@PathVariable Long cardId) {
        try {
            BentoCard card = bentoCardService.getById(cardId);
            if (card == null) {
                return Result.error("卡片不存在");
            }
            // 增加查看数
            bentoCardService.incrementViewCount(cardId);
            return Result.success(card);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有卡片
     */
    @GetMapping("/user/{userId}")
    public Result<List<BentoCard>> getUserCards(@PathVariable Long userId, 
                                                 @RequestParam(required = false) Boolean publicOnly) {
        try {
            List<BentoCard> cards = bentoCardService.getUserCards(userId, publicOnly);
            return Result.success(cards);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的公开卡片
     */
    @GetMapping("/user/{userId}/public")
    public Result<List<BentoCard>> getUserPublicCards(@PathVariable Long userId) {
        try {
            List<BentoCard> cards = bentoCardService.getUserCards(userId, true);
            return Result.success(cards);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据分类获取卡片
     */
    @GetMapping("/category/{userId}/{category}")
    public Result<List<BentoCard>> getCardsByCategory(@PathVariable Long userId, 
                                                       @PathVariable String category) {
        try {
            List<BentoCard> cards = bentoCardService.getCardsByCategory(userId, category);
            return Result.success(cards);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取广场卡片（分页）
     */
    @GetMapping("/square")
    public Result<Page<BentoCard>> getSquareCards(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            Page<BentoCard> page = bentoCardService.getSquareCards(pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 统计用户卡片数
     */
    @GetMapping("/count/{userId}")
    public Result<Integer> countUserCards(@PathVariable Long userId) {
        try {
            Integer count = bentoCardService.countUserCards(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
