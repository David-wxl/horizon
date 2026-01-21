package com.horizon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知识树节点实体类
 * 支持递归结构（parent_id）
 */
@Data
@TableName("t_knowledge_node")
public class KnowledgeNode {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 节点标题
     */
    private String title;
    
    /**
     * 节点内容（Markdown格式）
     */
    private String content;
    
    /**
     * 父节点ID（0表示根节点）
     */
    private Long parentId;
    
    /**
     * 节点类型：0-主蓝图（全局只读），1-个人第二大脑（用户可编辑）
     */
    private Integer nodeType;
    
    /**
     * 所属用户ID（nodeType=1时有效，nodeType=0时为null）
     */
    private Long userId;
    
    /**
     * 排序权重
     */
    private Integer sortOrder;
    
    /**
     * 节点图标
     */
    private String icon;
    
    /**
     * 节点颜色（十六进制）
     */
    private String color;
    
    /**
     * 节点状态：0-正常，1-已归档
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
