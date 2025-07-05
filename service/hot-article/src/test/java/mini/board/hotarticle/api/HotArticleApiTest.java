package mini.board.hotarticle.api;

import mini.board.hotarticle.application.response.HotArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class HotArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9004");

    @Test
    void readAllTest(){
        List<HotArticleResponse> response = restClient.get()
                .uri("/v1/hot-articles/articles/date/{dateStr}","20250705")
                .retrieve()
                .body(new ParameterizedTypeReference<List<HotArticleResponse>>() {
                });
        for(HotArticleResponse hotArticleResponse : response){
            System.out.println("response = " + hotArticleResponse);
        }
    }
}
