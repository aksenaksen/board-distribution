package mini.board.comment.application;

import kuke.board.common.event.EventType;
import kuke.board.common.event.payload.CommentCreatedEventPayload;
import kuke.board.common.outboxmessagerelay.OutboxEventPublisher;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import mini.board.comment.dto.request.CommentCreateRequestV2;
import mini.board.comment.dto.response.CommentPageResponse;
import mini.board.comment.dto.response.CommentResponse;
import mini.board.comment.entity.ArticleCommentCount;
import mini.board.comment.entity.Comment;
import mini.board.comment.entity.CommentPath;
import mini.board.comment.entity.CommentV2;
import mini.board.comment.infra.ArticleCommentCountRepository;
import mini.board.comment.infra.CommentRepositoryV2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {

    private final Snowflake snowflake = new Snowflake();
    private final CommentRepositoryV2 commentRepository;
    private final ArticleCommentCountRepository articleCommentCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public CommentResponse create(CommentCreateRequestV2 request){
        CommentV2 parent = findParent(request);
        CommentPath parentCommentPath = parent == null ? CommentPath.create("") : parent.getCommentPath();
        CommentV2 comment = commentRepository.save(
                CommentV2.create(
                        snowflake.nextId(),
                        request.getContent(),
                        request.getArticleId(),
                        request.getWriterId(),
                        parentCommentPath.createChildCommentPath(
                                commentRepository.findDescendantsTopPath(request.getArticleId(), parentCommentPath.getPath())
                                        .orElse(null)
                        )
                )
        );
        int result = articleCommentCountRepository.increase(request.getArticleId());
        if (result == 0) {
            articleCommentCountRepository.save(
                    ArticleCommentCount.init(request.getArticleId(), 1L)
            );
        }

        outboxEventPublisher.publish(
                EventType.COMMENT_CREATED,
                CommentCreatedEventPayload.builder()
                        .articleId(comment.getArticleId())
                        .createdAt(comment.getCreatedAt())
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .path(comment.getCommentPath().getPath())
                        .deleted(comment.getDeleted())
                        .writerId(comment.getWriterId())
                        .articleCommentCount(count(comment.getArticleId()))
                        .build(),
                comment.getArticleId()
        );
        return CommentResponse.from(comment);
    }

    private CommentV2 findParent(CommentCreateRequestV2 request) {
        String parentPath = request.getParentPath();
        if(parentPath == null){
            return null;
        }
        return commentRepository.findByPath(parentPath)
                .filter(not(CommentV2::getDeleted))
                .orElseThrow();
    }

    public CommentResponse read(Long commentId){
        return CommentResponse.from(
                commentRepository.findById(commentId).orElseThrow()
        );
    }

    @Transactional
    public void delete(Long commentId){
        commentRepository.findById(commentId)
                .filter(not(CommentV2::getDeleted))
                .ifPresent(comment -> {
                    if(hasChild(comment)){
                        comment.delete();
                    }
                    else{
                        delete(comment);
                    }

                    outboxEventPublisher.publish(
                            EventType.COMMENT_DELETED,
                            CommentCreatedEventPayload.builder()
                                    .articleId(comment.getArticleId())
                                    .createdAt(comment.getCreatedAt())
                                    .commentId(comment.getCommentId())
                                    .content(comment.getContent())
                                    .path(comment.getCommentPath().getPath())
                                    .deleted(comment.getDeleted())
                                    .writerId(comment.getWriterId())
                                    .articleCommentCount(comment.getArticleId())
                                    .build(),
                            comment.getArticleId()
                    );
                });
    }

    private boolean hasChild(CommentV2 comment) {
        return commentRepository.findDescendantsTopPath(
                comment.getArticleId(),
                        comment.getCommentPath().getPath())
                .isPresent();
    }

    private void delete(CommentV2 comment) {
        commentRepository.delete(comment);
        articleCommentCountRepository.decrease(comment.getArticleId());
        if(!comment.isRoot()){
            commentRepository.findByPath(comment.getCommentPath().getParentPath())
                    .filter(CommentV2::getDeleted)
                    .filter(not(this::hasChild))
                    .ifPresent(this::delete);
        }
    }

    public CommentPageResponse readAll(Long articleId, Long page, Long pageSize){
        return CommentPageResponse.of(
                commentRepository.findAll(articleId, (page-1)*pageSize, pageSize).stream()
                        .map(CommentResponse::from)
                        .toList(),
                commentRepository.count(articleId,PageLimitCalculator.calculatePageLimit(page,pageSize,10L))
        );
    }

    public List<CommentResponse> readAllInfiniteScroll(Long articleId, String lastPath, Long pageSize){
        List<CommentV2> comments = lastPath == null ?
                commentRepository.findAllInfiniteScroll(articleId,pageSize) :
                commentRepository.findAllInfiniteScroll(articleId,lastPath,pageSize);
        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    public Long count(Long articleId) {
        return articleCommentCountRepository.findById(articleId)
                .map(ArticleCommentCount::getCommentCount)
                .orElse(0L);
    }


}
