package com.example.telecom.common;

import com.example.telecom.common.api.PagedResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagedResultTest {

    @Test
    void shouldCalculateTotalPages() {
        PagedResult<String> result = new PagedResult<>(List.of("a", "b", "c"), 1, 10, 25);
        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());
        assertEquals(25, result.getTotal());
        assertEquals(3, result.getItems().size());
    }

    @Test
    void shouldHandleEmptyItems() {
        PagedResult<String> result = new PagedResult<>(List.of(), 1, 20, 0);
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void shouldHandleExactPage() {
        PagedResult<Integer> result = new PagedResult<>(List.of(1, 2), 2, 2, 4);
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void shouldHandleLargeDataset() {
        List<String> items = List.of("1", "2", "3", "4", "5");
        PagedResult<String> result = new PagedResult<>(items, 1, 100, 5);
        assertEquals(1, result.getTotalPages());
        assertEquals(5, result.getItems().size());
    }

    @Test
    void shouldAllowSettingFields() {
        PagedResult<String> result = new PagedResult<>();
        result.setPage(1);
        result.setPageSize(10);
        result.setTotal(100);
        result.setTotalPages(10);
        result.setItems(List.of("a", "b"));

        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());
        assertEquals(100, result.getTotal());
        assertEquals(10, result.getTotalPages());
        assertEquals(2, result.getItems().size());
    }

    @Test
    void shouldHandleZeroPageSize() {
        PagedResult<String> result = new PagedResult<>(List.of(), 0, 0, 100);
        assertEquals(0, result.getTotalPages());
    }
}
