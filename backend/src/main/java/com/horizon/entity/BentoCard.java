package com.horizon.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Bento 卡片实体类
 * 核心业务实体，存储用户创建的各类卡片内容
 */
@Data
@TableName(value = "t_bento_card", autoResultMap = true)
public class BentoCard {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 所属用户ID
     */
    private Long userId;
    
    /**
     * 卡片标题
     */
    private String title;
    
    /**
     * 卡片描述
     */
    private String description;
    
    /**
     * 卡片类型
     * image: 图片卡片（OOTD、摄影）
     * code: 代码卡片（代码片段、技术笔记）
     * text: 文本卡片（思考、日记）
     * media: 书影音卡片（书籍、电影、音乐）
     * link: 链接卡片（收藏的网站）
     * collection: 合集卡片（多内容组合）
     */
    private String cardType;
    
    /**
     * 卡片内容
     * 存储文本、Markdown、代码、图片URL/Base64等
     */
    private String content;
    
    /**
     * Grid X 坐标
     */
    private Integer gridX;
    
    /**
     * Grid Y 坐标
     */
    private Integer gridY;
    
    /**
     * 卡片宽度（1-4）
     */
    private Integer gridWidth;
    
    /**
     * 卡片高度（1-4）
     */
    private Integer gridHeight;
    
    /**
     * 背景色（可选，十六进制）
     */
    private String bgColor;
    
    /**
     * 边框样式
     */
    private String borderStyle;
    
    /**
     * 卡片主题：auto/light/dark
     */
    private String cardTheme;
    
    /**
     * 标签（逗号分隔）
     */
    private String tags;
    
    /**
     * 分类：ootd/edc/code/reading/travel
     */
    private String category;
    
    /**
     * 是否公开到广场：0-私密，1-公开
     */
    private Integer isPublic;
    
    /**
     * 状态：0-正常，1-归档，2-草稿
     */
    private Integer status;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 查看数
     */
    private Integer viewCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 排序权重（用户自定义顺序）
     */
    private Integer sortOrder;
    
    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isPinned;
    
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
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
