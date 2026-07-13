package com.example.telecom.collector.dto;

import java.util.List;
import java.util.Map;

public class DeviceBatchImportRequest {

    public enum ImportType {
        CSV,
        JSON
    }

    private String batchId;
    private ImportType importType;
    private List<String> fileContent;
    private Map<String, String> importOptions;

    public DeviceBatchImportRequest() {}

    public DeviceBatchImportRequest(String batchId, ImportType importType,
                                    List<String> fileContent, Map<String, String> importOptions) {
        this.batchId = batchId;
        this.importType = importType;
        this.fileContent = fileContent;
        this.importOptions = importOptions;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public ImportType getImportType() { return importType; }
    public void setImportType(ImportType importType) { this.importType = importType; }

    public List<String> getFileContent() { return fileContent; }
    public void setFileContent(List<String> fileContent) { this.fileContent = fileContent; }

    public Map<String, String> getImportOptions() { return importOptions; }
    public void setImportOptions(Map<String, String> importOptions) { this.importOptions = importOptions; }
}
