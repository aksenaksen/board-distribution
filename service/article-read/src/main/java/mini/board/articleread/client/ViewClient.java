package mini.board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewClient {

    private RestClient restClient;

    @Value("${endpoints.board-view-service.url}")
    private String likeServiceUrl;

    @PostConstruct
    public void initRestClient(){
        restClient = RestClient.create(likeServiceUrl);
    }

    @Cacheable(key = "#articleId", value = "articleViewCount")
    public long count(Long articleId){
        log.info("[ViewClinet.count] article : {}", articleId);
        try {
            return restClient.get()
                    .uri("/v1/article-views/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[ViewClient.count] article : {}", articleId, e);
            return 0;
        }
    }
}
