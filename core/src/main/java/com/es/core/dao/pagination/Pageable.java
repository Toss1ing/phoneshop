package com.es.core.dao.pagination;

public class Pageable {

    private int page;
    private int size;
    private String sortField;
    private String sortOrder;

    public Pageable(int page, int size) {
        this.page = page;
        this.size = size;
        this.sortField = null;
        this.sortOrder = null;
    }

    public Pageable(int page, int size, String sortField, String sortOrder) {
        this.page = page;
        this.size = size;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
