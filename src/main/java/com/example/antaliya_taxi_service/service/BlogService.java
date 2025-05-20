package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.blog.*;
import com.example.antaliya_taxi_service.model.Blog;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface BlogService {

     // Заменяем на DTO в публичном API
     BlogDto findById(Long id);
     void incrementViews(Long blogId, HttpServletRequest request);

     // Остальные методы остаются без изменений
     BlogDetailDto getBlogDetailDto(Long id);
     BlogListDto getAllBlogs(Pageable pageable);
     BlogListDto getPublishedBlogs(Pageable pageable);
     BlogDto createBlog(CreateBlogDto createBlogDto) throws IOException;
     BlogDto updateBlog(UpdateBlogDto updateBlogDto) throws IOException;
     void deleteBlog(Long id);
     }
