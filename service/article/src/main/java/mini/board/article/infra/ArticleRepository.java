package mini.board.article.infra;

import mini.board.article.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

//    article_id, board_id에 인덱스 설정 (secondary index 사용)
//    기존 OFFSET 기반 페이징은 board_id index로 조건을 만족하는 row를 찾은 후
//    OFFSET만큼 건너뛰며 스캔해야 하며, 이후 각 row에 대해 다시 primary key (clustering index) lookup이 발생함
//    → 예: 1,200만건 중 900만 번째부터 가져올 경우 약 6초 소요됨
//
//    개선 방식:
//    1. 서브쿼리에서 board_id 조건 + article_id 정렬 후 limit/offset → 이때 `article_id`만 조회하므로 covering index로 처리됨
//    2. 이후 메인 쿼리에서 해당 article_id로 article 테이블과 join하여 실제 row fetch
//    → 인덱스 → PK로 바로 접근 가능하여 성능 개선

    @Query(
            value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, " +
                    "article.created_at, article.modified_at " +
                    "from (" +
                    "   select article_id from article " +
                    "   where board_id = :boardId " +
                    "   order by article_id desc " +
                    "   limit :limit offset :offset " +
                    ") t left join article on t.article_id = article.article_id ",
            nativeQuery = true
    )
    List<Article> findAll(
            @Param("boardId") Long boardId,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );


//  페이징 목록 조회와 함께 count 쿼리를 실행할 때 성능을 고려하여 전체 개수를 세지 않고,
//  특정 board_id에 해당하는 article_id 중 상위 :limit 개까지만 조회 후 count를 수행
//  → 예: "더 보기" 버튼 등에서 전체 개수를 알 필요 없이 다음 페이지 유무 판단용으로 사용할 수 있음
//  → 서브쿼리에서 article_id만 조회하므로 covering index가 적용되어 빠르게 count 가능
//  참고: 전체 개수가 필요한 경우에는 limit 없이 count(*)를 사용해야 함

    @Query(
            value = "select count(*) from (" +
                    "   select article_id from article where board_id = :boardId limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long count(@Param("boardId") Long boardId, @Param("limit") Long limit);


//  무한 스크롤(Infinite Scroll) 방식에 적합한 Keyset Pagination 구현
//  기존 OFFSET 기반 페이징은 데이터 삽입/삭제 시 중복 또는 누락이 발생할 수 있음
//  예시:
//    - 데이터: [1, 2, 3], [4, 5, 6]
//    - 1번 삭제 후 → 다음 페이지: [3, 4, 5] (중복 발생)
//    - 1 앞에 0 추가 시 → 다음 페이지: [3, 4, 5] (중복 발생)
//  → 이러한 문제를 방지하기 위해 '마지막으로 조회된 article_id'를 기준으로 다음 페이지를 가져옴
//     (즉, 마지막 article_id보다 작은 값들을 가져오도록 설정)
//
//  장점:
//    - 정렬 기준 컬럼(article_id)에 인덱스가 있어 성능 저하 없이 빠르게 조회 가능
//    - 중복/누락 없이 안정적인 페이징 가능

    @Query(
            value = """
            SELECT * FROM article
            WHERE board_id = :boardId
            ORDER BY article_id DESC
            LIMIT :limit
""",
            nativeQuery = true
    )
    List<Article> findAllInfiniteScroll(@Param("boardId") Long boardId, @Param("limit") Long limit);


    @Query(
            value = """
            SELECT * FROM article
            WHERE board_id = :boardId
            AND article_id < :lastArticleId
            ORDER BY article_id DESC
            LIMIT :limit
""",
            nativeQuery = true
    )
    List<Article> findAllInfiniteScroll(@Param("boardId") Long boardId,
                                        @Param("limit") Long limit,
                                        @Param("lastArticleId") Long lastArticleId);
}
