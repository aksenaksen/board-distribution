package mini.board.hotarticle.application;

import kuke.board.common.event.Event;
import kuke.board.common.event.EventPayload;
import kuke.board.common.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.board.hotarticle.application.eventHandler.EventHandler;
import mini.board.hotarticle.application.response.HotArticleResponse;
import mini.board.hotarticle.client.ArticleClient;
import mini.board.hotarticle.infra.HotArticleListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleService {

    private final ArticleClient articleClient;
    private final List<EventHandler> eventHandlers;
    private final HotArticleScoreUpdater hotArticleScoreUpdater;
    private final HotArticleListRepository hotArticleListRepository;

    public void handleEvent(Event<EventPayload> event){
        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if(eventHandler == null){
            return;
        }

        if(isArticleCreatedOrDeleted(event)){
            eventHandler.handle(event);
        }
        else{
            hotArticleScoreUpdater.update(event, eventHandler);
        }

    }

    public List<HotArticleResponse> readAll(String dateStr){
        return hotArticleListRepository.readAll(dateStr)
                .stream()
                .map(articleClient::read)
                .map(HotArticleResponse::from)
                .toList();
    }



    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(hander -> hander.supports(event))
                .findAny()
                .orElse(null);
    }

    private boolean isArticleCreatedOrDeleted(Event<EventPayload> event) {
        return EventType.ARTICLE_CREATED == event.getType() || EventType.ARTICLE_DELETED == event.getType();
    }
}
