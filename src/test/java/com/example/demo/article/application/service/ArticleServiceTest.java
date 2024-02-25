package com.example.demo.article.application.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.demo.article.application.port.out.LoadArticlePort;
import com.example.demo.article.application.port.out.LoadBoardPort;
import com.example.demo.article.domain.Article;
import com.example.demo.article.domain.Board;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ArticleServiceTest {
    private ArticleService sut;

    private LoadArticlePort loadArticlePort;

    @BeforeEach
    void setUp() {
        loadArticlePort = Mockito.mock(LoadArticlePort.class);

        sut = new ArticleService(loadArticlePort);
    }

    @Test
    @DisplayName("articleId로 Article 한개 조회")
    void given_articleId_when_getById_then_return_Article() {
        var board = new Board(5L, "board");
        var article = new Article(1L, board, "", "", "", LocalDateTime.now());
        given(loadArticlePort.findArticleById(any()))
            .willReturn(Optional.of(article));


        var result = sut.getById(1L);

        then(result)
            .isNotNull()
            .hasFieldOrPropertyWithValue("id", article.getId())
            .hasFieldOrPropertyWithValue("board.id", board.getId())
            .hasFieldOrPropertyWithValue("subject", article.getSubject())
            .hasFieldOrPropertyWithValue("content", article.getContent())
            .hasFieldOrPropertyWithValue("username", article.getUsername())
            .hasFieldOrPropertyWithValue("createdAt", article.getCreatedAt());
    }

    @Test
    @DisplayName("boardId로 같은 Board의 Article 목록 조회")
    void getArticlesByBoard_listArticles() {
        var board = new Board(5L, "board");
        var article1 = new Article(1L, board, "article1", "", "", LocalDateTime.now());
        var article2 = new Article(2L, board, "article2", "", "", LocalDateTime.now());
        given(loadArticlePort.findArticlesByBoardId(any()))
            .willReturn(List.of(article1, article2));


        var result = sut.getArticlesByBoard(5L);

        then(result)
            .hasSize(2)
            .extracting("board.id").containsOnly(5L);
    }
}