package com.example.telecom.common.api;

import java.util.List;

public class PagedResult<T> {
    private List<T> items;
    private int page;
    private int pageSize;
    private long total;
    private int totalPages;

    public PagedResult() {}

    public PagedResult(List<T> items, int page, int pageSize, long total) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    public List<T> getItems() { return items; }
    public void setItems(List<T> items) { this.items = items; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
