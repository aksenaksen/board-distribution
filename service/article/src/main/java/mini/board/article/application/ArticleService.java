package mini.board.article.application;

import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import mini.board.article.dto.request.ArticleCreateRequest;
import mini.board.article.dto.request.ArticleUpdateRequest;
import mini.board.article.dto.response.ArticlePageResponse;
import mini.board.article.dto.response.ArticleResponse;
import mini.board.article.entity.Article;
import mini.board.article.infra.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;


    @Transactional
    public ArticleResponse create(ArticleCreateRequest request){
        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.getTitle(),request.getContent(), request.getBoardId(), request.getWriterId())
        );
//      primary key 생성 전략: Snowflake 알고리즘 사용
//      → 분산 시스템 환경에서도 중복 없는 고유 ID를 생성할 수 있고,
//         정렬 가능하며 시간 기반으로 추적도 가능해 ID로 정렬 시 성능 이점이 있음

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request){
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.update(request.getTitle(), request.getContent());
        return ArticleResponse.from(article);
    }

    public ArticleResponse read(Long articleId){
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId){
        articleRepository.deleteById(articleId);
    }


    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
        return ArticlePageResponse.of(
                articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize).stream()
                        .map(ArticleResponse::from)
                        .toList(),
                articleRepository.count(
                        boardId,
                        PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
                )
        );
    }

    public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize , Long lastArticleid) {
        List<Article> response = lastArticleid == null ?
                articleRepository.findAllInfiniteScroll(boardId, pageSize) :
                articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleid);
        return response.stream()
                .map(ArticleResponse::from)
                .toList();
    }
}
