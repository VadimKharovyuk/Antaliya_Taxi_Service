package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.model.Blog;
import com.example.antaliya_taxi_service.repository.BlogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ViewCounterService {

    private final BlogRepository blogRepository;
    private Map<String, Set<Long>> viewRegistry = new ConcurrentHashMap<>(); // IP -> Set<BlogId>

    @Autowired
    public ViewCounterService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Transactional
    public void incrementViewCount(Long blogId, HttpServletRequest request) {
        String visitorIdentifier = getVisitorIdentifier(request);

        // Получаем множество просмотренных блогов для данного пользователя
        Set<Long> viewedBlogs = viewRegistry.computeIfAbsent(visitorIdentifier, k -> new HashSet<>());

        // Если этот блог еще не просматривался текущим пользователем
        if (viewedBlogs.add(blogId)) {
            Blog blog = blogRepository.findById(blogId).orElse(null);
            if (blog != null) {
                if (blog.getViews() == null) {
                    blog.setViews(1);
                } else {
                    blog.setViews(blog.getViews() + 1);
                }
                blogRepository.save(blog);
            }
        }
    }

    private String getVisitorIdentifier(HttpServletRequest request) {
        // Получаем IP-адрес посетителя
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Можно добавить к IP информацию о сессии или User-Agent для большей точности
        String userAgent = request.getHeader("User-Agent");
        return ip + "_" + userAgent;
    }
}
