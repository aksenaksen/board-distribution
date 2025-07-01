package mini.board.comment.application;

import mini.board.comment.entity.Comment;
import mini.board.comment.infra.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("삭제할 댓글이 자식이 있으면 삭제만 표시")
    void deleteShouldMarkDeletedIfHasChildren(){
        Long articleId = 1L;
        Long commentId = 2L;
        Comment comment = createComment(articleId, commentId);
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(2L);


        commentService.delete(commentId);

        verify(comment).delete();
    }


    @Test
    @DisplayName("하위 댓글이 삭제되고 삭제되지 않은 부모면 하위 댓글만 삭제")
    void deleteShouldMarkDeleteChildOnlyIfNotDeletedParent(){
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = createComment(articleId, commentId,parentCommentId);
        given(comment.isRoot()).willReturn(false);
        Comment parent = mock(Comment.class);
        given(parent.getDeleted()).willReturn(false);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));

        given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);

        given(commentRepository.findById(parentCommentId))
                .willReturn(Optional.of(parent));

        commentService.delete(commentId);

        verify(commentRepository).delete(comment);
        verify(commentRepository, never()).delete(parent);
    }

    @Test
    @DisplayName("하위 댓글이 삭제되고 삭제된 부모면 재귀적으로 모두 삭제")
    void deleteRecursivelyShouldMarkDeletedIfHasChildren(){
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = createComment(articleId, commentId,parentCommentId);
        given(comment.isRoot()).willReturn(false);
        Comment parent = createComment(articleId, parentCommentId);
        given(parent.isRoot()).willReturn(true);
        given(parent.getDeleted()).willReturn(true);


        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));

        given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);

        given(commentRepository.findById(parentCommentId))
                .willReturn(Optional.of(parent));

        given(commentRepository.countBy(articleId, parentCommentId, 2L)).willReturn(1L);
        commentService.delete(commentId);

        verify(commentRepository).delete(comment);
        verify(commentRepository).delete(parent);
    }


    private Comment createComment(Long articleId, Long commentId){
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);
        return comment;
    }

    private Comment createComment(Long articleId, Long commentId, Long parentCommentId){
        Comment comment = createComment(articleId, commentId);
        given(comment.getParentCommentId()).willReturn(parentCommentId);
        return comment;
    }



}