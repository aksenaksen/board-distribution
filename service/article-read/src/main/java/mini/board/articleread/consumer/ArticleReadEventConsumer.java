package mini.board.articleread.consumer;

import kuke.board.common.event.Event;
import kuke.board.common.event.EventPayload;
import kuke.board.common.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.board.articleread.application.ArticleReadService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleReadEventConsumer {
    private final ArticleReadService articleReadService;

    @KafkaListener( topics = {
            EventType.Topic.BOARD_VIEW,
            EventType.Topic.BOARD_LIKE,
            EventType.Topic.BOARD_COMMENT,
            EventType.Topic.BOARD_ARTICLE
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[ArticleReadEventConsumer.listen] Received message: {}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if (event != null){
            articleReadService.handleEvent(event);
        }
        ack.acknowledge();
    }


}
