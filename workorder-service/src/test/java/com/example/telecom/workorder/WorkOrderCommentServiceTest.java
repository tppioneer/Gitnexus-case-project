package com.example.telecom.workorder;

import com.example.telecom.common.workorder.WorkOrder;
import com.example.telecom.workorder.domain.WorkOrderComment;
import com.example.telecom.workorder.dto.WorkOrderCommentRequest;
import com.example.telecom.workorder.dto.WorkOrderCommentResponse;
import com.example.telecom.workorder.repository.WorkOrderCommentRepository;
import com.example.telecom.workorder.repository.WorkOrderRepository;
import com.example.telecom.workorder.service.WorkOrderCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderCommentServiceTest {

    private WorkOrderCommentService commentService;
    private WorkOrderCommentRepository commentRepository;
    private WorkOrderRepository workOrderRepository;
    private String workOrderId;

    @BeforeEach
    void setUp() {
        commentRepository = new WorkOrderCommentRepository();
        workOrderRepository = new WorkOrderRepository();
        commentService = new WorkOrderCommentService(commentRepository, workOrderRepository);

        // Add a work order via fixture factory
        WorkOrder wo = WorkOrderFixtureFactory.createdWorkOrder("EAST");
        wo.setWorkOrderId("wo-test-comment");
        workOrderRepository.save(wo);
        workOrderId = wo.getWorkOrderId();
    }

    @Test
    void shouldAddComment() {
        WorkOrderCommentRequest request = new WorkOrderCommentRequest();
        request.setWorkOrderId(workOrderId);
        request.setAuthor("operator-1");
        request.setContent("Investigating the issue");
        request.setInternal(false);

        WorkOrderCommentResponse response = commentService.addComment(request);
        assertNotNull(response);
        assertNotNull(response.getCommentId());
        assertEquals(workOrderId, response.getWorkOrderId());
        assertEquals("operator-1", response.getAuthor());
        assertEquals("Investigating the issue", response.getContent());
        assertFalse(response.isInternal());
    }

    @Test
    void shouldGetCommentsByWorkOrderId() {
        WorkOrderCommentRequest request1 = new WorkOrderCommentRequest();
        request1.setWorkOrderId(workOrderId);
        request1.setAuthor("operator-1");
        request1.setContent("First comment");
        request1.setInternal(false);
        commentService.addComment(request1);

        WorkOrderCommentRequest request2 = new WorkOrderCommentRequest();
        request2.setWorkOrderId(workOrderId);
        request2.setAuthor("operator-2");
        request2.setContent("Second comment");
        request2.setInternal(true);
        commentService.addComment(request2);

        List<WorkOrderCommentResponse> comments = commentService.getCommentsByWorkOrderId(workOrderId);
        assertNotNull(comments);
        assertEquals(2, comments.size());
    }

    @Test
    void shouldDeleteComment() {
        WorkOrderCommentRequest request = new WorkOrderCommentRequest();
        request.setWorkOrderId(workOrderId);
        request.setAuthor("operator-1");
        request.setContent("Comment to delete");
        request.setInternal(false);

        WorkOrderCommentResponse response = commentService.addComment(request);
        assertNotNull(response.getCommentId());

        commentService.deleteComment(response.getCommentId());

        List<WorkOrderCommentResponse> comments = commentService.getCommentsByWorkOrderId(workOrderId);
        assertTrue(comments.isEmpty());
    }

    @Test
    void shouldUpdateComment() {
        WorkOrderCommentRequest addRequest = new WorkOrderCommentRequest();
        addRequest.setWorkOrderId(workOrderId);
        addRequest.setAuthor("operator-1");
        addRequest.setContent("Original content");
        addRequest.setInternal(false);

        WorkOrderCommentResponse added = commentService.addComment(addRequest);
        assertNotNull(added.getCommentId());

        WorkOrderCommentResponse updated = commentService.updateComment(added.getCommentId(), "Updated content").orElse(null);
        assertNotNull(updated);
        assertEquals("Updated content", updated.getContent());
    }

    @Test
    void shouldGetPublicCommentsByWorkOrder() {
        WorkOrderCommentRequest internalComment = new WorkOrderCommentRequest();
        internalComment.setWorkOrderId(workOrderId);
        internalComment.setAuthor("op-1");
        internalComment.setContent("Internal note");
        internalComment.setInternal(true);
        commentService.addComment(internalComment);

        WorkOrderCommentRequest publicComment = new WorkOrderCommentRequest();
        publicComment.setWorkOrderId(workOrderId);
        publicComment.setAuthor("op-1");
        publicComment.setContent("Public note");
        publicComment.setInternal(false);
        commentService.addComment(publicComment);

        List<WorkOrderCommentResponse> publicComments = commentService.getPublicCommentsByWorkOrder(workOrderId);
        assertEquals(1, publicComments.size());
        assertEquals("Public note", publicComments.get(0).getContent());
    }

    @Test
    void shouldGetInternalCommentsByWorkOrder() {
        WorkOrderCommentRequest internalComment = new WorkOrderCommentRequest();
        internalComment.setWorkOrderId(workOrderId);
        internalComment.setAuthor("op-1");
        internalComment.setContent("Internal note");
        internalComment.setInternal(true);
        commentService.addComment(internalComment);

        List<WorkOrderCommentResponse> internalComments = commentService.getInternalCommentsByWorkOrder(workOrderId);
        assertEquals(1, internalComments.size());
        assertTrue(internalComments.get(0).isInternal());
    }

    @Test
    void shouldGetCommentAnalytics() {
        WorkOrderCommentRequest request = new WorkOrderCommentRequest();
        request.setWorkOrderId(workOrderId);
        request.setAuthor("op-1");
        request.setContent("Test comment");
        request.setInternal(false);
        commentService.addComment(request);

        Map<String, Object> analytics = commentService.getCommentAnalytics(workOrderId);
        assertNotNull(analytics);
        assertEquals(1L, analytics.get("totalComments"));
        assertEquals(0L, analytics.get("internalComments"));
        assertEquals(1L, analytics.get("publicComments"));
    }

    @Test
    void shouldCountCommentsByWorkOrder() {
        WorkOrderCommentRequest request = new WorkOrderCommentRequest();
        request.setWorkOrderId(workOrderId);
        request.setAuthor("op-1");
        request.setContent("Test");
        request.setInternal(false);
        commentService.addComment(request);

        assertEquals(1, commentService.countCommentsByWorkOrder(workOrderId));
    }

    @Test
    void shouldSearchComments() {
        WorkOrderCommentRequest request = new WorkOrderCommentRequest();
        request.setWorkOrderId(workOrderId);
        request.setAuthor("op-1");
        request.setContent("Fiber optic cable issue");
        request.setInternal(false);
        commentService.addComment(request);

        List<WorkOrderCommentResponse> results = commentService.searchComments("fiber");
        assertFalse(results.isEmpty());
    }
}
