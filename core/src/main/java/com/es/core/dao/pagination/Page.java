package com.es.core.dao.pagination;

import java.util.List;

public record Page<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements
) {

    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / pageSize);
    }

}
