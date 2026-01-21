package com.horizon.controller;

import com.horizon.common.Result;
import com.horizon.entity.KnowledgeNode;
import com.horizon.service.KnowledgeNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识树控制器
 */
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {
    
    private final KnowledgeNodeService knowledgeNodeService;
    
    /**
     * 获取主蓝图树（全局只读）
     */
    @GetMapping("/master/tree")
    public Result<List<KnowledgeNode>> getMasterTree() {
        List<KnowledgeNode> tree = knowledgeNodeService.getMasterTree();
        return Result.success(tree);
    }
    
    /**
     * 获取用户的第二大脑树
     * TODO: 需要从Token中获取userId
     */
    @GetMapping("/user/tree")
    public Result<List<KnowledgeNode>> getUserTree(@RequestParam Long userId) {
        List<KnowledgeNode> tree = knowledgeNodeService.getUserTree(userId);
        return Result.success(tree);
    }
    
    /**
     * 创建节点
     */
    @PostMapping("/node")
    public Result<KnowledgeNode> createNode(@RequestBody KnowledgeNode node) {
        try {
            KnowledgeNode created = knowledgeNodeService.createNode(node);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新节点
     */
    @PutMapping("/node/{id}")
    public Result<Void> updateNode(@PathVariable Long id, @RequestBody KnowledgeNode node) {
        node.setId(id);
        knowledgeNodeService.updateById(node);
        return Result.success();
    }
    
    /**
     * 删除节点
     */
    @DeleteMapping("/node/{id}")
    public Result<Void> deleteNode(@PathVariable Long id) {
        knowledgeNodeService.removeById(id);
        return Result.success();
    }
}
