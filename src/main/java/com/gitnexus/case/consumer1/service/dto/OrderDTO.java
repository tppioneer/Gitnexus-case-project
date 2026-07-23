package com.gitnexus.case.consumer1.service.dto;

public class OrderDTO {
    private String id;
    private String name;
    private String status;
    private Double amount;

    public OrderDTO() {}

    public OrderDTO(String id, String name, String status, Double amount) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.amount = amount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
