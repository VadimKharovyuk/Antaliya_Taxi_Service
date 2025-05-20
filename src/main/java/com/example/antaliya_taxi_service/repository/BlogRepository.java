package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findByIsPublishedTrue(Pageable pageable);

    // Найти следующий блог по ID
    @Query("SELECT b.id FROM Blog b WHERE b.id > :id AND b.isPublished = true ORDER BY b.id ASC LIMIT 1")
    Optional<Long> findNextBlogId(@Param("id") Long id);

    // Найти предыдущий блог по ID
    @Query("SELECT b.id FROM Blog b WHERE b.id < :id AND b.isPublished = true ORDER BY b.id DESC LIMIT 1")
    Optional<Long> findPrevBlogId(@Param("id") Long id);

}
