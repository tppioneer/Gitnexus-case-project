package com.example.telecom.change.event;

import com.example.telecom.change.domain.NetworkChangePlan;
import com.example.telecom.change.service.ChangePlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary test: verifies Spring Event listener behavior.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class ChangeEventListenerTest {

    @Autowired
    private ChangeApprovedListener approvedListener;

    @Autowired
    private ChangePlanService planService;

    @Test
    void approvedListener_isRegisteredAsBean() {
        assertNotNull(approvedListener, "ChangeApprovedListener must be a Spring bean");
    }

    @Test
    void approve_publishesEventAndListenerReceives() throws Exception {
        approvedListener.clear();

        // Create a plan, then approve it
        NetworkChangePlan plan = new NetworkChangePlan(
                "CHG-EVT-001", "Event Test", "east",
                com.example.telecom.change.domain.DeviceFamily.ROUTER,
                com.example.telecom.change.domain.ChangeRisk.LOW);
        planService.createPlan(plan);
        planService.approve("CHG-EVT-001", "approver-1");

        assertEquals(1, approvedListener.count(),
                "Listener must receive exactly one ChangeApprovedEvent");
        assertEquals("CHG-EVT-001", approvedListener.received().get(0).getPlanId());
        assertTrue(approvedListener.received().get(0).isApproved());
    }

    @Test
    void approvedEvent_carriesCorrectData() {
        ChangeApprovedEvent event = new ChangeApprovedEvent("CHG-X", "op-1", true);
        assertEquals("CHG-X", event.getPlanId());
        assertEquals("op-1", event.getApproverId());
        assertTrue(event.isApproved());
    }

    @Test
    void completedEvent_carriesCorrectData() {
        ChangeCompletedEvent event = new ChangeCompletedEvent("CHG-Y", true, "done");
        assertEquals("CHG-Y", event.getPlanId());
        assertTrue(event.isSuccess());
        assertEquals("done", event.getMessage());
    }
}
