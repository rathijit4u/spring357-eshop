package com.mourathi.dto;

import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;

public class PageResponse<T> {

    private final boolean success;
    private final List<T> data;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final LocalDateTime timestamp;

    public PageResponse(boolean success, Page<T> page) {
        this.success = success;
        this.data = page.stream().toList();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public List<T> getData() {
        return data;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
