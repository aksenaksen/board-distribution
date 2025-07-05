package mini.board.articleread.application.event.handler;

import kuke.board.common.event.Event;
import kuke.board.common.event.EventType;
import kuke.board.common.event.payload.ArticleDeletedEventPayload;
import lombok.RequiredArgsConstructor;
import mini.board.articleread.infra.ArticleQueryModelRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
    private final ArticleQueryModelRepository articleQueryModelRepository;

    @Override
    public void handle(Event<ArticleDeletedEventPayload> event) {
        ArticleDeletedEventPayload payload = event.getPayload();
        articleQueryModelRepository.delete(payload.getArticleId());
    }

    @Override
    public boolean supports(Event<ArticleDeletedEventPayload> event) {
        return EventType.ARTICLE_DELETED == event.getType();
    }
}
