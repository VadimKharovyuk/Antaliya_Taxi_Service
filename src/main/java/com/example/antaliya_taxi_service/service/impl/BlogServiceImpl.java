package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.*;
import com.example.antaliya_taxi_service.dto.blog.*;
import com.example.antaliya_taxi_service.maper.BlogMapper;
import com.example.antaliya_taxi_service.model.Blog;
import com.example.antaliya_taxi_service.repository.BlogRepository;
import com.example.antaliya_taxi_service.service.BlogService;
import com.example.antaliya_taxi_service.service.StorageService;
import com.example.antaliya_taxi_service.service.ViewCounterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final ViewCounterService viewCounterService;
    private final StorageService storageService;
    private final BlogMapper blogMapper;

    protected Blog findEntityById(Long id) {
        return blogRepository.findById(id).orElse(null);
    }

    // Публичный метод API, возвращающий DTO (для использования в контроллерах)
    @Override
    public BlogDto findById(Long id) {
        return blogMapper.toDto(findEntityById(id));
    }

    @Override
    @Transactional
    public void incrementViews(Long blogId, HttpServletRequest request) {
        viewCounterService.incrementViewCount(blogId, request);
    }

    @Override
    public BlogDetailDto getBlogDetailDto(Long id) {
        Blog blog = findEntityById(id);
        if (blog == null) {
            return null;
        }

        // Найти ID предыдущего и следующего блога для навигации
        Long nextId = blogRepository.findNextBlogId(id).orElse(null);
        Long prevId = blogRepository.findPrevBlogId(id).orElse(null);

        return blogMapper.toDetailDto(blog,
                nextId != null ? nextId.toString() : null,
                prevId != null ? prevId.toString() : null);
    }

    @Override
    public BlogListDto getAllBlogs(Pageable pageable) {
        // Получаем страницу блогов из репозитория
        Page<Blog> blogPage = blogRepository.findAll(pageable);

        // Преобразуем содержимое страницы в список BlogCardDto
        List<BlogCardDto> blogCards = blogPage.getContent().stream()
                .map(blogMapper::toCardDto)
                .collect(Collectors.toList());

        // Создаем и возвращаем объект BlogListDto с информацией о пагинации
        return BlogListDto.builder()
                .blogs(blogCards)
                .totalPages(blogPage.getTotalPages())
                .currentPage(blogPage.getNumber())
                .totalItems(blogPage.getTotalElements())
                .itemsPerPage(blogPage.getSize())
                .build();
    }

    @Override
    public BlogListDto getPublishedBlogs(Pageable pageable) {
        // Получаем страницу опубликованных блогов из репозитория
        Page<Blog> blogPage = blogRepository.findByIsPublishedTrue(pageable);

        // Преобразуем содержимое страницы в список BlogCardDto
        List<BlogCardDto> blogCards = blogPage.getContent().stream()
                .map(blogMapper::toCardDto)
                .collect(Collectors.toList());

        // Создаем и возвращаем объект BlogListDto с информацией о пагинации
        return BlogListDto.builder()
                .blogs(blogCards)
                .totalPages(blogPage.getTotalPages())
                .currentPage(blogPage.getNumber())
                .totalItems(blogPage.getTotalElements())
                .itemsPerPage(blogPage.getSize())
                .build();
    }

    @Override
    @Transactional
    public BlogDto createBlog(CreateBlogDto createBlogDto) throws IOException {
        // Создаем блог без изображения
        Blog blog = blogMapper.createBlogFromDto(createBlogDto);

        // Загружаем изображение, если оно есть
        if (createBlogDto.getImage() != null && !createBlogDto.getImage().isEmpty()) {
            StorageService.StorageResult result = storageService.uploadImage(createBlogDto.getImage());
            blog.setUrl(result.getUrl());
            blog.setImageId(result.getImageId());
        }

        // Сохраняем блог
        blog = blogRepository.save(blog);

        return blogMapper.toDto(blog);
    }

    @Override
    @Transactional
    public BlogDto updateBlog(UpdateBlogDto updateBlogDto) throws IOException {
        Blog blog = findEntityById(updateBlogDto.getId());
        if (blog == null) {
            return null;
        }

        // Обновляем текстовые поля блога
        blogMapper.updateBlogFromDto(blog, updateBlogDto);

        // Обновляем изображение, если нужно
        if (Boolean.TRUE.equals(updateBlogDto.getUpdateImage()) &&
                updateBlogDto.getImage() != null &&
                !updateBlogDto.getImage().isEmpty()) {

            // Удаляем старое изображение
            if (blog.getImageId() != null) {
                storageService.deleteImage(blog.getImageId());
            }

            // Загружаем новое изображение
            StorageService.StorageResult result = storageService.uploadImage(updateBlogDto.getImage());
            blog.setUrl(result.getUrl());
            blog.setImageId(result.getImageId());
        }

        // Сохраняем обновленный блог
        blog = blogRepository.save(blog);

        return blogMapper.toDto(blog);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id) {
        Blog blog = findEntityById(id);
        if (blog != null && blog.getImageId() != null) {
            storageService.deleteImage(blog.getImageId());
            blogRepository.deleteById(id);
        }
    }
}