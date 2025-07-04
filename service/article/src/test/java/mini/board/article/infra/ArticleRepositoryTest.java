package mini.board.article.infra;

import lombok.extern.slf4j.Slf4j;
import mini.board.article.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    void findAllTest(){
        List<Article> articles = articleRepository.findAll(1L, 30L,1499970L);
        log.info("article size: {}", articles.size());
//        for(Article article : articles) {
//            log.info("article : {}", article );
//        }
    }

    @Test
    void countTest(){
        Long count = articleRepository.count(1L, 10000L);
        log.info("count: {}", count);
    }

    @Test
    void findInfiniteScrollTest(){
        List<Article> articles = articleRepository.findAllInfiniteScroll(1L,30L);
        for(Article article : articles){
            log.info("article: {}", article.getArticleId());
        }

        Long lastId = articles.getLast().getArticleId();
        log.info("-------------------last-id: {}", lastId);
        List<Article> articles2 = articleRepository.findAllInfiniteScroll(1L, 30L, lastId);
        for(Article article : articles2){
            log.info("article: {}", article.getArticleId());
        }
    }

}