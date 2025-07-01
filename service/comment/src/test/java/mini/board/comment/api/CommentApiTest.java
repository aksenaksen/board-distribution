package mini.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mini.board.comment.dto.request.CommentCreateRequest;
import mini.board.comment.dto.response.CommentPageResponse;
import mini.board.comment.dto.response.CommentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");

//    private Long articleId;
//     private String content;
//     private Long parentCommentId;
//     private Long writerId;

    @Test
    void create(){
        CommentCreateRequest request = new CommentCreateRequest();
        ReflectionTestUtils.setField(request,"articleId", 1L);
        ReflectionTestUtils.setField(request,"content", "my content");
        ReflectionTestUtils.setField(request,"parentCommentId", null);
        ReflectionTestUtils.setField(request,"writerId", 1L);
        CommentResponse response = createComment(request);

        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my content2", response.getCommentId() , 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my content3", response.getCommentId() , 1L));

        log.info("commentId = {}", response.getCommentId());
        log.info("commentId = {}", response2.getCommentId());
        log.info("commentId = {}", response3.getCommentId());

    }

    CommentResponse createComment(CommentCreateRequest request){
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read(){
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 198353051600101376L)
                .retrieve()
                .body(CommentResponse.class);

        log.info("response = {}", response.getCommentId());

    }

    @Test
    void delete(){
        restClient.delete()
                .uri("/v1/comments/{commentId}", 198353052669648896L)
                .retrieve();
    }

    @Test
    void readAll(){
        CommentPageResponse response = restClient.get()
                .uri("/v1/comments?articleId=1&pageSize=10&page=1")
                .retrieve()
                .body(CommentPageResponse.class);

        log.info("response = {}", response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }


    }


//comment.getCommentId() = 198355640976789504
//        comment.getCommentId() = 198355641056481286
//        comment.getCommentId() = 198355640976789505
//        comment.getCommentId() = 198355641056481281
//        comment.getCommentId() = 198355640976789506
//        comment.getCommentId() = 198355641060675584
//        comment.getCommentId() = 198355640976789507
//        comment.getCommentId() = 198355641056481283
//        comment.getCommentId() = 198355640976789508
//        comment.getCommentId() = 198355641056481282

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> responses1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for (CommentResponse comment : responses1) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        Long lastParentCommentId = responses1.getLast().getParentCommentId();
        Long lastCommentId = responses1.getLast().getCommentId();

        List<CommentResponse> responses2 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
                        .formatted(lastParentCommentId, lastCommentId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage");
        for (CommentResponse comment : responses2) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }





    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class CommentCreateRequest
    {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }


}
