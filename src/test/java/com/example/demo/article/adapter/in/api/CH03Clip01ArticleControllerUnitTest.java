package com.example.demo.article.adapter.in.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.article.adapter.in.api.dto.ArticleDto;
import com.example.demo.article.application.port.in.CreateArticleUseCase;
import com.example.demo.article.application.port.in.DeleteArticleUseCase;
import com.example.demo.article.application.port.in.GetArticleUseCase;
import com.example.demo.article.application.port.in.ModifyArticleUseCase;
import com.example.demo.article.domain.ArticleFixtures;
import com.example.demo.common.api.GlobalControllerAdvice;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

class CH03Clip01ArticleControllerUnitTest {
    private MockMvc mockMvc;

    private GetArticleUseCase getArticleUseCase;
    private CreateArticleUseCase createArticleUseCase;
    private ModifyArticleUseCase modifyArticleUseCase;
    private DeleteArticleUseCase deleteArticleUseCase;

    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
        .serializers(LocalTimeSerializer.INSTANCE)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .modules(new JavaTimeModule())
        .build();

    @BeforeEach
    void setUp() {
        getArticleUseCase = Mockito.mock(GetArticleUseCase.class);
        createArticleUseCase = Mockito.mock(CreateArticleUseCase.class);
        modifyArticleUseCase = Mockito.mock(ModifyArticleUseCase.class);
        deleteArticleUseCase = Mockito.mock(DeleteArticleUseCase.class);

        mockMvc = MockMvcBuilders
            .standaloneSetup(new ArticleController(getArticleUseCase, createArticleUseCase, modifyArticleUseCase, deleteArticleUseCase))
            .alwaysDo(print())
            .setControllerAdvice(new GlobalControllerAdvice())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper), new ResourceHttpMessageConverter())
            .build();
    }

    @Nested
    @DisplayName("GET /articles/{articleId}")
    class GetArticle {
        @Test
        @DisplayName("Article이 있으면, 200 OK return response")
        void returnResponse() throws Exception {
            var article = ArticleFixtures.article();
            given(getArticleUseCase.getArticleById(any()))
                .willReturn(article);

            Long articleId = 1L;
            mockMvc.perform(get("/articles/{articleId}", articleId))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("articleId 에 해당하는 Article이 없으면 400 Not Found")
        void getArticle() throws Exception {
            given(getArticleUseCase.getArticleById(any()))
                .willThrow(new ResourceNotFoundException("article not exists"));

            Long articleId = 1L;
            mockMvc.perform(get("/articles/{articleId}", articleId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /articles")
    class PostArticle {
        @Test
        @DisplayName("생성된 articleId 반환")
        void returnArticleId() throws Exception {
            var createdArticle = ArticleFixtures.article();
            given(createArticleUseCase.createArticle(any()))
                .willReturn(createdArticle);

            var body = objectMapper.writeValueAsString(Map.of("boardId", 5L, "subject", "subject", "content", "content", "username", "user"));
            mockMvc
                .perform(
                    post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(
                    status().isOk()
                );
        }

        @ParameterizedTest(name = "{0}")
        @DisplayName("비정상 패러미터이면 BadRequest")
        @CsvSource(
            value = {
                "subject is null,,content,user",
                "content is null,subject,,user",
                "username is null,subject,content,",
                "username is empty,subject,content,''"
            }
        )
        void invalidParam_BadRequest(String desc, String subject, String content, String username) throws Exception {
            var body = objectMapper.writeValueAsString(new ArticleDto.CreateArticleRequest(5L, subject, content, username));
            mockMvc
                .perform(
                    post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andDo(print())
                .andExpect(
                    status().isBadRequest()
                );
        }
    }
}
