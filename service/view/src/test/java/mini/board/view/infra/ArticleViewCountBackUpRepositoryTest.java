package mini.board.view.infra;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mini.board.view.entity.ArticleViewCount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleViewCountBackUpRepositoryTest {

    @Autowired
    ArticleViewCountBackUpRepository articleViewCountRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Transactional
    void updateViewCountTest(){

        articleViewCountRepository.save(
                ArticleViewCount.init(
                        1L,
                        0L
                )
        );
        entityManager.flush();
        entityManager.clear();


        Assertions.assertThat(articleViewCountRepository.updateViewCount(1L, 100L)).isEqualTo(1L);
        Assertions.assertThat(articleViewCountRepository.updateViewCount(1L, 300L)).isEqualTo(1L);
        Assertions.assertThat(articleViewCountRepository.updateViewCount(1L, 200L)).isEqualTo(0L);


    }

}