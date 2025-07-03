package mini.board.hotarticle.infra;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotArticleListRepositoryTest {

    @Autowired
    HotArticleListRepository hotArticleListRepository;

    @Test
    void addTest() throws InterruptedException {
        LocalDateTime now = LocalDateTime.of(2024, 7,23,0,0);
        long limit = 5L;

        hotArticleListRepository.add(1L,now,2L,limit, Duration.ofSeconds(30));
        hotArticleListRepository.add(2L,now,10L,limit, Duration.ofSeconds(30));
        hotArticleListRepository.add(3L,now,4L,limit, Duration.ofSeconds(30));
        hotArticleListRepository.add(4L,now,2L,limit, Duration.ofSeconds(30));
        hotArticleListRepository.add(5L,now,2L,limit, Duration.ofSeconds(30));
        hotArticleListRepository.add(6L,now,2L,limit, Duration.ofSeconds(30));
        hotArticleListRepository.add(7L,now,2L,limit, Duration.ofSeconds(30));
        hotArticleListRepository.add(8L,now,2L,limit, Duration.ofSeconds(30));

        List<Long> articleIds = hotArticleListRepository.readAll("20240723");

        Assertions.assertThat(articleIds).hasSize(Long.valueOf(limit).intValue());
        Assertions.assertThat(articleIds.getFirst()).isEqualTo(2L);

        TimeUnit.SECONDS.sleep(40);

        Assertions.assertThat(hotArticleListRepository.readAll("20240723")).isEmpty();


    }
}