package mini.board.article.infra;

import mini.board.article.entity.BoardArticleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {

    @Query(
            value = """
            UPDATE board_article_count
            SET article_count = article_count + 1
            WHERE article_id = :articleId
            """, nativeQuery = true
    )
    @Modifying
    int increase(@Param("articleId") Long board_id);


    @Query(
            value = """
            UPDATE board_article_count
            SET article_count = article_count - 1
            WHERE article_id = :articleId
            """, nativeQuery = true
    )
    @Modifying
    int decrease(@Param("articleId") Long articleId);

}
