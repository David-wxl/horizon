package com.horizon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.horizon.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    /**
     * 查询卡片的所有评论（按时间倒序）
     */
    List<Comment> selectByCardId(@Param("cardId") Long cardId);
    
    /**
     * 查询某评论的所有回复（二级评论）
     */
    List<Comment> selectRepliesByParentId(@Param("parentId") Long parentId);
    
    /**
     * 统计卡片评论数
     */
    Integer countByCardId(@Param("cardId") Long cardId);
}
