<%@ tag body-content="empty" %>
<%@ attribute name="sortField" required="true" %>
<%@ attribute name="sortOrder" required="true" %>
<%@ attribute name="label" required="true" %>

<c:set var="isActive" value="${sortField eq param.sortField and sortOrder eq param.sortOrder}" />

<a href="?page=0&size=${param.size}&sortField=${sortField}&sortOrder=${sortOrder}&search=${param.search}"
   class="text-dark text-decoration-none fs-5 px-2 py-1 ${isActive ? 'fw-bold border-bottom border-dark' : ''}">
    ${label}
</a>
