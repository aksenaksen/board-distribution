package mini.board.article.presentation;

import lombok.Getter;
import mini.board.article.application.ArticleService;
import mini.board.article.dto.request.ArticleCreateRequest;
import mini.board.article.dto.request.ArticleUpdateRequest;
import mini.board.article.dto.response.ArticlePageResponse;
import mini.board.article.dto.response.ArticleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/v1/articles/{articleId}")
    public ArticleResponse read(@PathVariable Long articleId) {
        return articleService.read(articleId);
    }

    @GetMapping("/v1/articles")
    public ArticlePageResponse readAll(@RequestParam("boardId") Long boardId,
                                       @RequestParam("page") Long page,
                                       @RequestParam("pageSize") Long pageSize) {
        return articleService.readAll(boardId, page, pageSize);
    }

    @GetMapping("/v1/articles/infinite-scroll")
    public List<ArticleResponse> readAllInfiniteScroll(@RequestParam("boardId") Long boardId,
                                                     @RequestParam("pageSize") Long pageSize,
                                                     @RequestParam(value = "lastArticleId" , required = false) Long lastArticleId
                                                     ){
        return articleService.readAllInfiniteScroll(boardId, pageSize, lastArticleId);
    }


    @PostMapping("/v1/articles")
    public ArticleResponse create(@RequestBody ArticleCreateRequest request) {
        return articleService.create(request);
    }

    @PutMapping("/v1/articles/{articleId}")
    public ArticleResponse update(@PathVariable Long articleId, @RequestBody ArticleUpdateRequest request) {
        return articleService.update(articleId, request);
    }

    @DeleteMapping("/v1/articles/{articleId}")
    public void delete(@PathVariable Long articleId) {
        articleService.delete(articleId);
    }

    @GetMapping("/v1/articles/boards/{boardId}/count")
    public Long count(@PathVariable Long boardId) {
        return articleService.count(boardId);
    }

}
