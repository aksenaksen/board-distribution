package mini.board.view.application;

import lombok.RequiredArgsConstructor;
import mini.board.view.entity.ArticleViewCount;
import mini.board.view.infra.ArticleViewCountBackUpRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackUpProcessor {

    private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;


    @Transactional
    public void backUp(Long articleId, Long viewCount) {
        int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);
        if(result == 0){
            articleViewCountBackUpRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {},
                        () -> articleViewCountBackUpRepository.save(
                                ArticleViewCount.init(articleId,viewCount)
                        )
                    );
        }


    }
}
