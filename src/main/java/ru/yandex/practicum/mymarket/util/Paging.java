package ru.yandex.practicum.mymarket.util;


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