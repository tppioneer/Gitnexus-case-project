package com.example.telecom.sla;

import com.example.telecom.common.exception.DomainException;
import com.example.telecom.common.exception.ValidationException;
import com.example.telecom.common.vendor.VendorSlaStatus;
import com.example.telecom.sla.mapper.SlaMapper;
import com.example.telecom.sla.repository.SlaContractRepository;
import com.example.telecom.sla.service.SlaContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlaContractServiceTest {

    @Mock
    private SlaContractRepository contractRepository;

    private SlaMapper mapper;
    private SlaContractService contractService;

    @BeforeEach
    void setUp() {
        mapper = new SlaMapper();
        contractService = new SlaContractService(contractRepository, mapper);
    }

    @Test
    void createContract_shouldSucceed_whenRequestIsValid() {
        SlaContractRequest request = new SlaContractRequest(
                "Test Contract", "vendor-001", "US-EAST",
                120, 360, 99.9,
                LocalDate.now(), LocalDate.now().plusMonths(6)
        );

        when(contractRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        SlaContractResponse response = contractService.createContract(request);

        assertNotNull(response);
        assertNotNull(response.getContractId());
        assertEquals("Test Contract", response.getContractName());
        assertEquals(VendorSlaStatus.PENDING, response.getStatus());
    }

    @Test
    void createContract_shouldThrow_whenContractNameIsBlank() {
        SlaContractRequest request = new SlaContractRequest(
                "", "vendor-001", "US-EAST",
                120, 360, 99.9,
                LocalDate.now(), LocalDate.now().plusMonths(6)
        );

        assertThrows(ValidationException.class, () -> contractService.createContract(request));
    }

    @Test
    void getContract_shouldThrow_whenNotFound() {
        assertThrows(DomainException.class, () -> contractService.getContract("nonexistent-id"));
    }

    @Test
    void listContracts_shouldReturnEmptyList_whenNoContractsExist() {
        var result = contractService.listContracts(null, null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
