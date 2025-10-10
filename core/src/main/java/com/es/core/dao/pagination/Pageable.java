package com.es.core.dao.pagination;

public record Pageable(
        int page,
        int size,
        String sortField,
        String sortOrder
) {
}
