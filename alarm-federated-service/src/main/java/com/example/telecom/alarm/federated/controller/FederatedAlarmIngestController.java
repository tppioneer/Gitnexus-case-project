package com.example.telecom.alarm.federated.controller;

import com.example.telecom.alarm.federated.*;
import com.example.telecom.alarm.federated.mapper.FederatedAlarmMapper;
import com.example.telecom.alarm.federated.repository.FederatedAlarmSourceRepository;
import com.example.telecom.alarm.federated.service.AlarmIngestionService;
import com.example.telecom.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/federated/alarms/ingest")
public class FederatedAlarmIngestController {

    private final AlarmIngestionService ingestionService;
    private final FederatedAlarmSourceRepository sourceRepository;
    private final FederatedAlarmMapper mapper;

    public FederatedAlarmIngestController(AlarmIngestionService ingestionService,
                                           FederatedAlarmSourceRepository sourceRepository,
                                           FederatedAlarmMapper mapper) {
        this.ingestionService = ingestionService;
        this.sourceRepository = sourceRepository;
        this.mapper = mapper;
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ingestBatch(
            @Valid @RequestBody List<FederatedAlarmRequest> requests) {
        List<FederatedAlarmRecord> records = ingestionService.ingestBatch(requests);
        List<FederatedAlarmResponse> responses = records.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        Map<String, Object> result = Map.of(
                "totalReceived", requests.size(),
                "totalIngested", records.size(),
                "alarms", responses
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<FederatedAlarmResponse>> ingestSingle(
            @Valid @RequestBody FederatedAlarmRequest request) {
        FederatedAlarmRecord record = ingestionService.ingest(request);
        FederatedAlarmResponse response = mapper.toResponse(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PostMapping("/register-source")
    public ResponseEntity<ApiResponse<FederatedAlarmSource>> registerSource(
            @RequestBody Map<String, String> sourceInfo) {
        String sourceId = sourceInfo.getOrDefault("sourceId", "src-" + System.currentTimeMillis());
        String name = sourceInfo.getOrDefault("name", sourceId);
        String regionCode = sourceInfo.getOrDefault("regionCode", "UNKNOWN");
        String endpoint = sourceInfo.getOrDefault("endpoint", "");

        FederatedAlarmSource source = new FederatedAlarmSource(sourceId, name, regionCode, endpoint);
        FederatedAlarmSource saved = sourceRepository.save(source);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(saved));
    }

    @GetMapping("/sources")
    public ResponseEntity<ApiResponse<List<FederatedAlarmSource>>> listSources() {
        List<FederatedAlarmSource> sources = sourceRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(sources));
    }
}
