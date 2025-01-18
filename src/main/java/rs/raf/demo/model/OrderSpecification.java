package rs.raf.demo.model;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {
    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Order> hasScheduledTimeBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return (root, query, criteriaBuilder) ->
                (dateFrom == null || dateTo == null) ? null : criteriaBuilder.between(root.get("scheduledTime"), dateFrom, dateTo);
    }

    public static Specification<Order> hasCreatedById(Long userId) {
        return (root, query, criteriaBuilder) -> userId == null ? null : criteriaBuilder.equal(root.get("createdBy").get("id"), userId);
    }

    public static Specification<Order> hasCreatedByEmail(String email) {
        return (root, query, criteriaBuilder) -> email == null ? null : criteriaBuilder.equal(root.get("createdBy").get("email"), email);
    }
}
