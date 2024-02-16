package com.example.demo.article.domain;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Article {
    private Long id;
    private Long boardId;
    private String subject;
    private String content;
    private String username;
    private LocalDateTime createdAt;

    public Article(Long id, Long boardId, String subject, String content, String username, LocalDateTime createdAt) {
        this.id = id;
        this.boardId = boardId;
        this.subject = subject;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
    }
}
