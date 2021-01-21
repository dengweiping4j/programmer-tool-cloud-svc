package com.programmer.blog.domain.mapper;

import com.programmer.blog.domain.BlogPaper;
import com.programmer.blog.domain.dto.BlogPaperDTO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 实体转换类
 *
 * @author dengweiping
 * @date 2021/1/21 17:09
 */
public final class BlogPaperMapper {

    /**
     * 静态类方法,不支持new对象
     */
    private BlogPaperMapper() {
    }

    /**
     * BlogPaperDTO 转化为 BlogPaper
     *
     * @param dto 转换前对象
     * @return 转换后对象
     */
    public static BlogPaper toEntity(BlogPaperDTO dto) {
        if (dto == null) {
            return null;
        }
        BlogPaper blogPaper = new BlogPaper();
        blogPaper.setId(dto.getId());
        blogPaper.setTitle(dto.getTitle());
        blogPaper.setDescription(dto.getDescription());
        blogPaper.setContent(dto.getContent());
        blogPaper.setAuthor(dto.getAuthor());
        blogPaper.setCreateDate(new Date(dto.getCreateDate()));
        return blogPaper;
    }

    /**
     * BlogPaper 转化为 BlogPaperDTO
     *
     * @param blogPaper 转换前对象
     * @return 转换后对象
     */
    public static BlogPaperDTO toDTO(BlogPaper blogPaper) {
        if (blogPaper == null) {
            return null;
        }

        BlogPaperDTO dto = new BlogPaperDTO();
        dto.setId(blogPaper.getId());
        dto.setTitle(blogPaper.getTitle());
        dto.setDescription(blogPaper.getDescription());
        dto.setContent(blogPaper.getContent());
        dto.setAuthor(blogPaper.getAuthor());
        if (blogPaper.getCreateDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createDate = sdf.format(blogPaper.getCreateDate());
            dto.setCreateDate(createDate);
        }
        return dto;
    }
}
