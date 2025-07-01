package mini.board.article.presentation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import mini.board.article.dto.request.ArticleCreateRequest;
import mini.board.article.dto.response.ArticlePageResponse;
import mini.board.article.dto.response.ArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest(){
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));

        log.info(response.toString());
    }

    @Test
    void readTest(){
        ArticleResponse response = read(198072434217783296L);
        log.info(response.toString());
    }


    @Test
    void updateTest(){
        update(198072434217783296L);
        ArticleResponse response = read(198072434217783296L);
        log.info(response.toString());
    }

    @Test
    void readAllTest(){
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);
        log.info("response.getArticleCount : {}" , response.getArticleCount());
        for(ArticleResponse article: response.getArticles()){
            log.info ("articleId = {}" , article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest(){
        List<ArticleResponse> articles1 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});
        log.info("firstpage");
        for(ArticleResponse response: articles1){
            log.info("articleResponse Id = {}" , response.getArticleId());
        }


        Long lastArticleId = articles1.getLast().getArticleId();

        List<ArticleResponse> articles2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId="+lastArticleId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});

        log.info("secondpage");
        for(ArticleResponse response: articles2){
            log.info("articleResponse Id = {}" , response.getArticleId());
        }

    }

//    @Test
//    void deleteTest(){
//        delete(198072434217783296L);
//
//    }




    void delete(Long articleId){
        restClient.delete()
                .uri("/v1/articles/{articleid}",articleId)
                .retrieve();
    }

    void update(Long articleId){
        restClient.put()
                .uri("/v1/articles/{articleId}" , articleId)
                .body(new ArticleUpdateRequest("hi 2", "my content1234"))
                .retrieve()
                .body(ArticleResponse.class);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleid}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    ArticleResponse create(ArticleCreateRequest request){
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public class ArticleUpdateRequest {
        private String title;
        private String content;
    }


}