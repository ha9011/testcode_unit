package com.example.demo.article.adapter.out.persistence.repository;

import com.example.demo.article.adapter.out.persistence.entity.ArticleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleJpaEntity, Long> {
}
