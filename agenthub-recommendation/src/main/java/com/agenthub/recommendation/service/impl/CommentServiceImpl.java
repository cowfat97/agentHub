package com.agenthub.recommendation.service.impl;

import com.agenthub.common.domain.exception.CommentNotFoundException;
import com.agenthub.common.dto.CommentDTO;
import com.agenthub.common.enums.CommentStatus;
import com.agenthub.common.exception.AgentHubException;
import com.agenthub.common.utils.XssUtils;
import com.agenthub.recommendation.domain.entity.Comment;
import com.agenthub.recommendation.domain.repository.CommentRepository;
import com.agenthub.recommendation.dto.CommentCreateRequest;
import com.agenthub.recommendation.dto.CommentListResponse;
import com.agenthub.recommendation.dto.CommentReviewResult;
import com.agenthub.recommendation.service.CommentReviewService;
import com.agenthub.recommendation.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentReviewService commentReviewService;

    private CommentDTO toDTO(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDTO.builder()
                .id(comment.getId())
                .articleId(comment.getArticleId())
                .commenterId(comment.getCommenterId())
                .commenterName(comment.getCommenterName())
                .commenterType(comment.getCommenterType())
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .rootId(comment.getRootId())
                .replyToId(comment.getReplyToId())
                .replyToName(comment.getReplyToName())
                .status(comment.getStatus() != null ? comment.getStatus().getCode() : null)
                .reviewReason(comment.getReviewReason())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    private CommentDTO toDTOWithReplies(Comment comment, List<Comment> replies) {
        CommentDTO dto = toDTO(comment);
        if (dto != null && replies != null && !replies.isEmpty()) {
            dto.setReplies(replies.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    @Override
    @Transactional
    public CommentDTO create(CommentCreateRequest request) {
        Long commentId = commentRepository.nextId();
        Comment comment;

        // XSS过滤评论内容
        String sanitizedContent = XssUtils.sanitizeComment(request.getContent());
        String sanitizedCommenterName = XssUtils.escape(request.getCommenterName());

        if (request.getParentId() == null) {
            comment = Comment.createForArticle(
                    commentId,
                    request.getArticleId(),
                    request.getCommenterId(),
                    sanitizedCommenterName,
                    request.getCommenterType(),
                    sanitizedContent
            );
        } else {
            Comment parentComment = commentRepository.findById(request.getParentId());
            if (parentComment == null) {
                throw new CommentNotFoundException(request.getParentId());
            }

            if (!parentComment.getArticleId().equals(request.getArticleId())) {
                throw new AgentHubException("父评论不属于该文章");
            }

            Long rootId = parentComment.getRootId();

            // XSS过滤回复目标名称
            String sanitizedReplyToName = request.getReplyToName() != null
                    ? XssUtils.escape(request.getReplyToName()) : null;

            comment = Comment.createReply(
                    commentId,
                    request.getArticleId(),
                    request.getCommenterId(),
                    sanitizedCommenterName,
                    request.getCommenterType(),
                    sanitizedContent,
                    request.getParentId(),
                    rootId,
                    request.getReplyToId(),
                    sanitizedReplyToName
            );
        }

        Comment saved = commentRepository.save(comment);
        log.info("创建评论成功: id={}, articleId={}", commentId, request.getArticleId());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public CommentDTO approve(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        comment.approve();
        Comment updated = commentRepository.update(comment);

        log.info("评论审核通过: id={}", id);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public CommentDTO reject(Long id, String reason) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        comment.reject(reason);
        Comment updated = commentRepository.update(comment);

        log.info("评论审核拒绝: id={}, reason={}", id, reason);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void delete(Long id, Long commenterId, String commenterType) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        if (!comment.isCommenter(commenterId, commenterType)) {
            throw new AgentHubException("只有评论者可以删除评论");
        }

        comment.delete();
        commentRepository.update(comment);

        log.info("删除评论成功: id={}, commenterId={}", id, commenterId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        comment.delete();
        commentRepository.update(comment);

        log.info("删除评论成功（简化版）: id={}", id);
    }

    @Override
    public CommentDTO getById(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }
        return toDTO(comment);
    }

    @Override
    public CommentListResponse getArticleComments(Long articleId, int pageNum, int pageSize) {
        List<Comment> topComments = commentRepository.findByArticleIdAndStatusWithPage(
                articleId, CommentStatus.APPROVED, pageNum, pageSize);

        long total = commentRepository.countByArticleIdAndStatus(articleId, CommentStatus.APPROVED);

        List<CommentDTO> commentDTOs = topComments.stream()
                .map(comment -> {
                    List<Comment> replies = commentRepository.findRepliesByRootId(comment.getId())
                            .stream()
                            .filter(c -> c.getStatus() == CommentStatus.APPROVED)
                            .collect(Collectors.toList());
                    return toDTOWithReplies(comment, replies);
                })
                .collect(Collectors.toList());

        return CommentListResponse.builder()
                .comments(commentDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public CommentListResponse getReplies(Long rootId, int pageNum, int pageSize) {
        List<Comment> replies = commentRepository.findRepliesByRootId(rootId)
                .stream()
                .filter(c -> c.getStatus() == CommentStatus.APPROVED && !c.getId().equals(rootId))
                .collect(Collectors.toList());

        long total = replies.size();

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, replies.size());
        List<Comment> pagedReplies = start < replies.size() ? replies.subList(start, end) : new ArrayList<>();

        List<CommentDTO> commentDTOs = pagedReplies.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return CommentListResponse.builder()
                .comments(commentDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    public CommentListResponse getPendingComments(int pageNum, int pageSize) {
        List<Comment> pendingComments = commentRepository.findByStatus(CommentStatus.PENDING);

        long total = pendingComments.size();

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, pendingComments.size());
        List<Comment> pagedComments = start < pendingComments.size()
                ? pendingComments.subList(start, end)
                : new ArrayList<>();

        List<CommentDTO> commentDTOs = pagedComments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return CommentListResponse.builder()
                .comments(commentDTOs)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .hasMore((long) pageNum * pageSize < total)
                .build();
    }

    @Override
    @Transactional
    public CommentDTO like(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new CommentNotFoundException(id);
        }

        if (comment.isApproved()) {
            comment.like();
            commentRepository.update(comment);
        }

        return toDTO(comment);
    }

    @Override
    public long countByArticleId(Long articleId) {
        return commentRepository.countByArticleIdAndStatus(articleId, CommentStatus.APPROVED);
    }
}