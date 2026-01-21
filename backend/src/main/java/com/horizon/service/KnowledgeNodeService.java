package com.horizon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.horizon.entity.KnowledgeNode;
import com.horizon.mapper.KnowledgeNodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识树节点服务
 */
@Service
@RequiredArgsConstructor
public class KnowledgeNodeService extends ServiceImpl<KnowledgeNodeMapper, KnowledgeNode> {
    
    /**
     * 获取主蓝图树（全局只读）
     */
    public List<KnowledgeNode> getMasterTree() {
        LambdaQueryWrapper<KnowledgeNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeNode::getNodeType, 0)  // 主蓝图
               .eq(KnowledgeNode::getStatus, 0)     // 正常状态
               .orderByAsc(KnowledgeNode::getSortOrder);
        return buildTree(this.list(wrapper));
    }
    
    /**
     * 获取用户的第二大脑树
     */
    public List<KnowledgeNode> getUserTree(Long userId) {
        LambdaQueryWrapper<KnowledgeNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeNode::getNodeType, 1)  // 第二大脑
               .eq(KnowledgeNode::getUserId, userId)
               .eq(KnowledgeNode::getStatus, 0)
               .orderByAsc(KnowledgeNode::getSortOrder);
        return buildTree(this.list(wrapper));
    }
    
    /**
     * 创建节点
     */
    public KnowledgeNode createNode(KnowledgeNode node) {
        // 验证父节点是否存在
        if (node.getParentId() != null && node.getParentId() > 0) {
            KnowledgeNode parent = this.getById(node.getParentId());
            if (parent == null) {
                throw new RuntimeException("父节点不存在");
            }
        }
        
        this.save(node);
        return node;
    }
    
    /**
     * 构建树形结构（递归）
     */
    private List<KnowledgeNode> buildTree(List<KnowledgeNode> nodes) {
        List<KnowledgeNode> tree = new ArrayList<>();
        
        // 找出所有根节点
        for (KnowledgeNode node : nodes) {
            if (node.getParentId() == null || node.getParentId() == 0) {
                tree.add(node);
            }
        }
        
        // 为每个根节点递归构建子树
        for (KnowledgeNode root : tree) {
            buildChildren(root, nodes);
        }
        
        return tree;
    }
    
    /**
     * 递归构建子节点
     */
    private void buildChildren(KnowledgeNode parent, List<KnowledgeNode> allNodes) {
        List<KnowledgeNode> children = new ArrayList<>();
        
        for (KnowledgeNode node : allNodes) {
            if (node.getParentId() != null && node.getParentId().equals(parent.getId())) {
                children.add(node);
                buildChildren(node, allNodes);  // 递归
            }
        }
        
        // 这里需要在KnowledgeNode实体中添加一个children字段（不存储到数据库）
        // 暂时不设置，后续需要添加@TableField(exist = false)
    }
}
