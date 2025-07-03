package mini.board.hotarticle.consumer;


import kuke.board.common.event.Event;
import kuke.board.common.event.EventPayload;
import kuke.board.common.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.board.hotarticle.application.HotArticleService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotArticleEventConsumer {

    private final HotArticleService hotArticleService;

    @KafkaListener(
            topics = {
                    EventType.Topic.BOARD_ARTICLE,
                    EventType.Topic.BOARD_COMMENT,
                    EventType.Topic.BOARD_LIKE,
                    EventType.Topic.BOARD_VIEW
            }
    )
    public void listen(String message, Acknowledgment ack){
        log.info("[HotArticleEventConsumer.listen] received message: {}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if(event!=null){
            hotArticleService.handleEvent(event);
        }
        ack.acknowledge();
    }

}
