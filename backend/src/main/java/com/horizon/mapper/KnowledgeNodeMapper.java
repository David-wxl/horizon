package com.horizon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.horizon.entity.KnowledgeNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识节点Mapper
 */
@Mapper
public interface KnowledgeNodeMapper extends BaseMapper<KnowledgeNode> {
    
    /**
     * 递归查询子节点（用于构建树形结构）
     */
    List<KnowledgeNode> selectChildrenRecursive(@Param("parentId") Long parentId, 
                                                @Param("nodeType") Integer nodeType, 
                                                @Param("userId") Long userId);
}
