package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import rs.raf.demo.model.*;
import rs.raf.demo.repositories.DishRepository;
import rs.raf.demo.repositories.ErrorMessageRepository;
import rs.raf.demo.repositories.OrderRepository;
import rs.raf.demo.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;
    private final DishRepository dishRepository;
    private final UserService userService;
    private final ErrorMessageRepository errorMessageRepository;

    @Value("${app.maxConcurrentOrders}")
    private int maxConcurrentOrders;

    @Autowired
    public OrderService(OrderRepository orderRepository, JwtUtil jwtUtil, DishRepository dishRepository, UserService userService, ErrorMessageRepository errorMessageRepository) {
        this.orderRepository = orderRepository;
        this.jwtUtil = jwtUtil;
        this.dishRepository = dishRepository;
        this.userService = userService;
        this.errorMessageRepository = errorMessageRepository;
    }

    public List<Dish> getMenu() {
        return dishRepository.findAll();
    }

    public Order scheduleOrder(CreateOrderRequest request) {

        Order order = new Order();
        order.setStatus(OrderStatus.SCHEDULED);
        order.setActive(true);
        order.setScheduledTime(request.getScheduledTime());
        order.setCreatedAt(LocalDateTime.now());
        order.setCreatedBy(userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));
        List<Dish> dishes = dishRepository.findAllById(request.getDishes());
        order.setDishes(dishes);

        if (order.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Vreme zakazivanja mora biti u budućnosti.");
        }

        return orderRepository.save(order);
    }

    public Order placeOrder(CreateOrderRequest req, User user) {
        long concurrentOrders = orderRepository.countByStatusIn(Arrays.asList(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY, OrderStatus.ORDERED));


        Order newOrder = new Order();
        newOrder.setStatus(OrderStatus.ORDERED);
        newOrder.setActive(true);
        newOrder.setScheduledTime(null);
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setCreatedBy(user);
        System.out.println(user);
        List<Dish> dishes = dishRepository.findAllById(req.getDishes());
        newOrder.setDishes(dishes);

        Order createdOrder = orderRepository.save(newOrder);

        if (concurrentOrders >= maxConcurrentOrders) {
            createdOrder.setStatus(OrderStatus.CANCELED);
            orderRepository.save(createdOrder);

            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setErrorTime(LocalDateTime.now());
            errorMessage.setOrder(createdOrder);
            errorMessage.setOperation("CREATE");
            errorMessage.setMessage("Prekoračen maksimalni broj istovremenih porudžbina.");
            errorMessageRepository.save(errorMessage);
            throw new IllegalStateException("Prekoračen maksimalni broj istovremenih porudžbina.");
        }

        return createdOrder;
    }

    public void processScheduledOrder(Order order) {
        long concurrentOrders = orderRepository.countByStatusIn(Arrays.asList(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY, OrderStatus.ORDERED));

        if (concurrentOrders >= maxConcurrentOrders) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setErrorTime(LocalDateTime.now());
            errorMessage.setOrder(order);
            errorMessage.setOperation("SCHEDULE");
            errorMessage.setMessage("Prekoračen maksimalni broj istovremenih porudžbina.");

            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
            errorMessageRepository.save(errorMessage);
        } else {
            order.setStatus(OrderStatus.ORDERED);
            orderRepository.save(order);
        }
    }

    public List<Order> getScheduledOrders() {
        LocalDateTime now = LocalDateTime.now();
        return orderRepository.findByStatusAndScheduledTimeBefore(OrderStatus.SCHEDULED, now);
    }

    public List<Order> findAllForCurrentUser(String token, OrderStatus status, LocalDateTime dateFrom, LocalDateTime dateTo, Long userId, Boolean isAdmin) {
        Specification<Order> spec;

        if (isAdmin) {
            System.out.println("************************ ADMIN ************************");
            spec = Specification.where(OrderSpecification.hasStatus(status))
                    .and(OrderSpecification.hasScheduledTimeBetween(dateFrom, dateTo))
                    .and(OrderSpecification.hasCreatedById(userId));
        } else {
            System.out.println("************************ USER ************************");
            String email = jwtUtil.extractUsername(token);
            spec = Specification.where(OrderSpecification.hasStatus(status))
                    .and(OrderSpecification.hasScheduledTimeBetween(dateFrom, dateTo))
                    .and(OrderSpecification.hasCreatedByEmail(email));
        }

        return orderRepository.findAll(spec);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.ORDERED) {
            throw new IllegalStateException("Only orders in ORDERED status can be canceled");
        }
        order.setStatus(OrderStatus.CANCELED);
        order.setActive(false);
        orderRepository.save(order);
    }

    public Order acceptOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.ORDERED) {
            throw new IllegalStateException("Only orders in ORDERED status can be accepted");
        }
        order.setStatus(OrderStatus.PREPARING);
        orderRepository.save(order);

        return order;
    }

    public Order rejectOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.ORDERED) {
            throw new IllegalStateException("Only orders in ORDERED status can be rejected");
        }
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return order;
    }

    public Order trackOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Scheduled(fixedDelay = 10000)
    public void updateOrderStatuses() {
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            if (order.getScheduledTime() != null && order.getScheduledTime().isBefore(LocalDateTime.now())) {
                order.setScheduledTime(null);
            }

            switch (order.getStatus()) {
//                case ORDERED:
//                    order.setStatus(OrderStatus.PREPARING);
//                    break;
                case PREPARING:
                    order.setStatus(OrderStatus.IN_DELIVERY);
                    break;
                case IN_DELIVERY:
                    order.setStatus(OrderStatus.DELIVERED);
                    break;
                default:
                    break;
            }
            orderRepository.save(order);
        }
    }
}

