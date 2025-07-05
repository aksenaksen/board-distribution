package mini.board.articleread.presentation;

import lombok.RequiredArgsConstructor;
import mini.board.articleread.application.ArticleReadService;
import mini.board.articleread.application.response.ArticleReadResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleReadController {

    private final ArticleReadService articleReadService;

    @GetMapping("/v1/articles/{articleId}")
    public ArticleReadResponse readResponse(@PathVariable("articleId") long articleId){
        return articleReadService.read(articleId);
    }
}
