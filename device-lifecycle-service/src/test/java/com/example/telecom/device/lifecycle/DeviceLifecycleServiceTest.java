package com.example.telecom.device.lifecycle;

import com.example.telecom.common.device.DeviceLifecycleStatus;
import com.example.telecom.common.exception.DomainException;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleRequest;
import com.example.telecom.device.lifecycle.dto.DeviceLifecycleResponse;
import com.example.telecom.device.lifecycle.event.DeviceLifecycleEventPublisher;
import com.example.telecom.device.lifecycle.mapper.DeviceLifecycleMapper;
import com.example.telecom.device.lifecycle.model.AuditEntry;
import com.example.telecom.device.lifecycle.repository.DeviceLifecycleRepository;
import com.example.telecom.device.lifecycle.service.DeviceLifecycleAuditService;
import com.example.telecom.device.lifecycle.service.DeviceLifecycleService;
import com.example.telecom.device.lifecycle.validator.DeviceLifecycleValidator;
import com.example.telecom.device.lifecycle.workflow.DeviceStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceLifecycleServiceTest {

    @Mock
    private DeviceLifecycleRepository repository;

    @Mock
    private DeviceStateMachine stateMachine;

    @Mock
    private DeviceLifecycleEventPublisher eventPublisher;

    @Mock
    private DeviceLifecycleAuditService auditService;

    private DeviceLifecycleValidator validator;
    private DeviceLifecycleMapper mapper;
    private DeviceLifecycleService service;

    @BeforeEach
    void setUp() {
        validator = new DeviceLifecycleValidator();
        mapper = new DeviceLifecycleMapper();
        service = new DeviceLifecycleService(repository, stateMachine, validator,
                eventPublisher, mapper, auditService);
    }

    @Test
    void testRegisterDevice() {
        DeviceLifecycleRequest request = new DeviceLifecycleRequest(
                "DEV-TEST001", "Test Device", "ROUTER",
                "Cisco", "ISR4451", "US-EAST", "10.0.0.1");

        when(repository.existsById("DEV-TEST001")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publishRegistered(anyString());
        doNothing().when(auditService).recordAction(anyString(), anyString(), anyString());

        DeviceLifecycleResponse response = service.register(request);

        assertNotNull(response);
        verify(repository).save(any());
        verify(eventPublisher).publishRegistered("DEV-TEST001");
        verify(auditService).recordAction("DEV-TEST001", "REGISTER", "system");
    }

    @Test
    void testActivateDevice() {
        String deviceId = "DEV-TEST001";
        com.example.telecom.device.lifecycle.model.LifecycleRecord record =
                new com.example.telecom.device.lifecycle.model.LifecycleRecord(
                        deviceId, "Test Device", "ROUTER", "Cisco",
                        "ISR4451", "US-EAST", "10.0.0.1",
                        com.example.telecom.device.lifecycle.model.DeviceLifecycleState.REGISTERED);

        when(repository.findById(deviceId)).thenReturn(java.util.Optional.of(record));
        when(stateMachine.isValidTransition(
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.REGISTERED,
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE)).thenReturn(true);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publishActivated(anyString());
        doNothing().when(auditService).recordAction(anyString(), anyString(), anyString());

        DeviceLifecycleResponse response = service.activate(deviceId);

        assertNotNull(response);
        verify(repository).save(any());
        verify(eventPublisher).publishActivated(deviceId);
    }

    @Test
    void testInvalidTransition() {
        String deviceId = "DEV-TEST001";
        com.example.telecom.device.lifecycle.model.LifecycleRecord record =
                new com.example.telecom.device.lifecycle.model.LifecycleRecord(
                        deviceId, "Test Device", "ROUTER", "Cisco",
                        "ISR4451", "US-EAST", "10.0.0.1",
                        com.example.telecom.device.lifecycle.model.DeviceLifecycleState.REGISTERED);

        when(repository.findById(deviceId)).thenReturn(java.util.Optional.of(record));
        when(stateMachine.isValidTransition(
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.REGISTERED,
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.RETIRED)).thenReturn(false);

        assertThrows(DomainException.class, () -> service.retire(deviceId));
    }

    @Test
    void testSuspendAndReactivate() {
        String deviceId = "DEV-TEST001";
        com.example.telecom.device.lifecycle.model.LifecycleRecord record =
                new com.example.telecom.device.lifecycle.model.LifecycleRecord(
                        deviceId, "Test Device", "ROUTER", "Cisco",
                        "ISR4451", "US-EAST", "10.0.0.1",
                        com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE);

        when(repository.findById(deviceId)).thenReturn(java.util.Optional.of(record));
        when(stateMachine.isValidTransition(
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE,
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.SUSPENDED)).thenReturn(true);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publishSuspended(anyString());
        doNothing().when(auditService).recordAction(anyString(), anyString(), anyString());

        DeviceLifecycleResponse suspendResponse = service.suspend(deviceId);
        assertNotNull(suspendResponse);

        when(repository.findById(deviceId)).thenReturn(java.util.Optional.of(record));
        when(stateMachine.isValidTransition(
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.SUSPENDED,
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE)).thenReturn(true);

        record.setCurrentState(com.example.telecom.device.lifecycle.model.DeviceLifecycleState.SUSPENDED);

        DeviceLifecycleResponse reactivateResponse = service.activate(deviceId);
        assertNotNull(reactivateResponse);
    }

    @Test
    void testGetLifecycleHistory() {
        String deviceId = "DEV-TEST001";
        when(repository.existsById(deviceId)).thenReturn(true);
        when(auditService.getHistory(deviceId)).thenReturn(List.of(
                new AuditEntry(deviceId, "REGISTER", "system", java.time.LocalDateTime.now(), "Registered"),
                new AuditEntry(deviceId, "ACTIVATE", "admin", java.time.LocalDateTime.now(), "Activated")
        ));

        List<AuditEntry> history = service.getLifecycleHistory(deviceId);

        assertNotNull(history);
        assertEquals(2, history.size());
        verify(auditService).getHistory(deviceId);
    }

    @Test
    void testRetireDevice() {
        String deviceId = "DEV-TEST005";
        com.example.telecom.device.lifecycle.model.LifecycleRecord record =
                new com.example.telecom.device.lifecycle.model.LifecycleRecord(
                        deviceId, "Test Device", "ROUTER", "Cisco",
                        "ISR4451", "US-EAST", "10.0.0.1",
                        com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE);

        when(repository.findById(deviceId)).thenReturn(java.util.Optional.of(record));
        when(stateMachine.isValidTransition(
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE,
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.RETIRED)).thenReturn(true);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publishRetired(anyString());
        doNothing().when(auditService).recordAction(anyString(), anyString(), anyString());

        DeviceLifecycleResponse response = service.retire(deviceId);
        assertNotNull(response);
        verify(eventPublisher).publishRetired(deviceId);
    }

    @Test
    void testDecommissionDevice() {
        String deviceId = "DEV-TEST006";
        com.example.telecom.device.lifecycle.model.LifecycleRecord record =
                new com.example.telecom.device.lifecycle.model.LifecycleRecord(
                        deviceId, "Test Device", "ROUTER", "Cisco",
                        "ISR4451", "US-EAST", "10.0.0.1",
                        com.example.telecom.device.lifecycle.model.DeviceLifecycleState.RETIRED);

        when(repository.findById(deviceId)).thenReturn(java.util.Optional.of(record));
        when(stateMachine.isValidTransition(
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.RETIRED,
                com.example.telecom.device.lifecycle.model.DeviceLifecycleState.DECOMMISSIONED)).thenReturn(true);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(eventPublisher).publishDecommissioned(anyString());
        doNothing().when(auditService).recordAction(anyString(), anyString(), anyString());

        DeviceLifecycleResponse response = service.decommission(deviceId);
        assertNotNull(response);
        verify(eventPublisher).publishDecommissioned(deviceId);
    }

    @Test
    void testGetStatus() {
        String deviceId = "DEV-TEST007";
        com.example.telecom.device.lifecycle.model.LifecycleRecord record =
                new com.example.telecom.device.lifecycle.model.LifecycleRecord(
                        deviceId, "Test Device", "ROUTER", "Cisco",
                        "ISR4451", "US-EAST", "10.0.0.1",
                        com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE);

        when(repository.findById(deviceId)).thenReturn(java.util.Optional.of(record));

        DeviceLifecycleResponse response = service.getStatus(deviceId);
        assertNotNull(response);
        assertEquals(deviceId, response.getDeviceId());
    }

    @Test
    void testListByStatus() {
        String deviceId = "DEV-TEST008";
        com.example.telecom.device.lifecycle.model.LifecycleRecord record =
                new com.example.telecom.device.lifecycle.model.LifecycleRecord(
                        deviceId, "Test Device", "ROUTER", "Cisco",
                        "ISR4451", "US-EAST", "10.0.0.1",
                        com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE);

        when(repository.findByStatus(com.example.telecom.device.lifecycle.model.DeviceLifecycleState.ACTIVE))
                .thenReturn(List.of(record));

        List<DeviceLifecycleResponse> results = service.listByStatus(DeviceLifecycleStatus.ACTIVE);
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void testRegisterDuplicateDevice() {
        DeviceLifecycleRequest request = new DeviceLifecycleRequest(
                "DEV-TEST009", "Duplicate Device", "SWITCH",
                "HP", "2920", "US-WEST", "10.0.0.9");

        when(repository.existsById("DEV-TEST009")).thenReturn(true);

        assertThrows(DomainException.class, () -> service.register(request));
    }

    @Test
    void testGetStatusForNonExistentDevice() {
        when(repository.findById("DEV-NONEXIST")).thenReturn(java.util.Optional.empty());

        assertThrows(DomainException.class, () -> service.getStatus("DEV-NONEXIST"));
    }

    @Test
    void testIsDeviceRegistered() {
        when(repository.existsById("DEV-TEST010")).thenReturn(true);
        when(repository.existsById("DEV-UNKNOWN")).thenReturn(false);

        assertTrue(service.isDeviceRegistered("DEV-TEST010"));
        assertFalse(service.isDeviceRegistered("DEV-UNKNOWN"));
    }
}
