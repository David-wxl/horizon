package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.entity.BentoCard;
import com.horizon.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public Result<Boolean> addFavorite(@RequestParam Long userId, @RequestParam Long cardId) {
        try {
            return Result.success(favoriteService.addFavorite(userId, cardId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping
    public Result<Boolean> removeFavorite(@RequestParam Long userId, @RequestParam Long cardId) {
        try {
            return Result.success(favoriteService.removeFavorite(userId, cardId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/check")
    public Result<Boolean> isFavorited(@RequestParam Long userId, @RequestParam Long cardId) {
        try {
            return Result.success(favoriteService.isFavorited(userId, cardId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list/{userId}")
    public Result<List<BentoCard>> getUserFavorites(@PathVariable Long userId) {
        try {
            return Result.success(favoriteService.getUserFavorites(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/count/{cardId}")
    public Result<Long> countByCard(@PathVariable Long cardId) {
        try {
            return Result.success(favoriteService.countByCard(cardId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
