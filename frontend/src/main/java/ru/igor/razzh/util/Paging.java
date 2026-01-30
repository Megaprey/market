package ru.igor.razzh.util;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class Paging {
    private int pageNumber;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public Paging(int currentPage, int pageSize, long totalItems) {
        this.pageNumber = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalItems / pageSize) : 0;
    }

    public static <T> Page<T> getPage(List<T> content, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(content, pageable, content.size());
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }


    // Дополнительные полезные методы
    public boolean hasNext() {
        return pageNumber < totalPages;
    }

    public boolean hasPrevious() {
        return pageNumber > 1;
    }

    public int getNextPage() {
        return pageNumber + 1;
    }

    public int getPreviousPage() {
        return pageNumber - 1;
    }
}