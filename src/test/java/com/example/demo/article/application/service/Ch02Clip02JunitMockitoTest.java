package com.example.demo.article.application.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.example.demo.article.application.port.out.CommandArticlePort;
import com.example.demo.article.application.port.out.LoadArticlePort;
import com.example.demo.article.application.port.out.LoadBoardPort;
import com.example.demo.article.domain.Article;
import com.example.demo.article.domain.ArticleFixtures;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;

class Ch02Clip02JunitMockitoTest {
    private ArticleService sut;

    private LoadArticlePort loadArticlePort;
    private CommandArticlePort commandArticlePort;
    private LoadBoardPort loadBoardPort;

    private TestMock testMock;

    @BeforeEach
    void setUp() {
        loadArticlePort = Mockito.mock(LoadArticlePort.class);
        commandArticlePort = Mockito.mock(CommandArticlePort.class);
        loadBoardPort = Mockito.mock(LoadBoardPort.class);
        testMock = Mockito.mock(TestMock.class);

        sut = new ArticleService(loadArticlePort, commandArticlePort, loadBoardPort, testMock);
    }

    @Test
    @DisplayName("articleId 로 조회시 Article 반환")
    void return_Article() {
        Article articleById = loadArticlePort.findArticleById(1L).orElse(null);
        System.out.println(articleById);

        int test = testMock.test(1, 2);
        System.out.println(test);


//        var article = ArticleFixtures.article();
//        Mockito.when(loadArticlePort.findArticleById(any()))
//            .thenReturn(Optional.of(article));
//
//        var result = sut.getArticleById(1L);
//
//        then(result)
//            .isNotNull()
//            .hasFieldOrPropertyWithValue("id", article.getId())
//            .hasFieldOrPropertyWithValue("board.id", article.getBoard().getId())
//            .hasFieldOrPropertyWithValue("subject", article.getSubject())
//            .hasFieldOrPropertyWithValue("content", article.getContent())
//            .hasFieldOrPropertyWithValue("username", article.getUsername())
//            .hasFieldOrPropertyWithValue("createdAt", article.getCreatedAt());
    }

    @Test
    @DisplayName("BDDStyle Board의 Article 목록 조회")
    void getArticlesByBoard_listArticles() {
        var article1 = ArticleFixtures.article(1L);
        var article2 = ArticleFixtures.article(2L);
        BDDMockito.given(loadArticlePort.findArticlesByBoardId(any()))
            .willReturn(List.of(article1, article2));

        var result = sut.getArticlesByBoard(5L);

        then(result)
            .hasSize(2)
            .extracting("board.id").containsOnly(5L);
    }

    @Test
    @DisplayName("Article 삭제")
    void deleteArticle() {
        BDDMockito.willDoNothing()
            .given(commandArticlePort).deleteArticle(any());

        sut.deleteArticle(1L);

        verify(commandArticlePort).deleteArticle(1L);
    }
}
