package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rs.raf.demo.model.Order;
import rs.raf.demo.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    List<Order> findByCreatedBy_Email(String email);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusAndScheduledTimeBetweenAndCreatedBy_Id(OrderStatus status, LocalDateTime dateFrom, LocalDateTime dateTo, Long userId);

    List<Order> findByStatusAndCreatedBy_EmailAndScheduledTimeBetween(OrderStatus status, String email, LocalDateTime dateFrom, LocalDateTime dateTo);

    List<Order> findByStatusAndCreatedBy_Email(OrderStatus status, String email);

    List<Order> findAll();

    long countByStatus(OrderStatus status);

    List<Order> findByStatusAndScheduledTimeBefore(OrderStatus status, LocalDateTime now);
    long countByStatusIn(List<OrderStatus> statuses);
}