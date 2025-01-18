package rs.raf.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.raf.demo.model.*;
import rs.raf.demo.services.OrderService;
import rs.raf.demo.services.UserService;
import rs.raf.demo.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('can_create_orders')")
    @PostMapping
    public Order placeOrder(@RequestBody CreateOrderRequest order) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return orderService.placeOrder(order, userService.findByEmail(username));
        } catch (Exception e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('can_schedule_orders')")
    @PostMapping("/schedule")
    public Order scheduleOrder(@RequestBody CreateOrderRequest order) {
        try {
            return orderService.scheduleOrder(order);
        } catch (Exception e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('can_read_orders')")
    @GetMapping
    public List<Order> findAllOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDateTime dateFrom,
            @RequestParam(required = false) LocalDateTime dateTo,
            @RequestParam(required = false) Long userId
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);
        System.out.println("!!!!!!!!!!!!! ROLE: " + user.getRole());
        return orderService.findAllForCurrentUser(token.substring(7), status, dateFrom, dateTo, userId, user.getRole() == UserTypes.ADMIN);
    }

    @PreAuthorize("hasAuthority('can_read_orders')")
    @GetMapping("/{id}")
    public Order findOrderById(@PathVariable Long id) {
        return orderService.findById(id).orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Order not found"));
    }

    @PreAuthorize("hasAuthority('can_cancel_orders')")
    @DeleteMapping("/{id}")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @PreAuthorize("hasAuthority('can_track_orders')")
    @GetMapping("/track/{id}")
    public Order trackOrder(@PathVariable Long id) {
        return orderService.trackOrder(id);
    }

    @GetMapping("/menu")
    public List<Dish> getMenu() {
        return orderService.getMenu();
    }

    @PutMapping("/accept/{id}")
    public Order acceptOrder(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);
        if (user.getRole() != UserTypes.ADMIN) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Only admins can accept orders");
        }

        return orderService.acceptOrder(id);
    }

    @PutMapping("/reject/{id}")
    public Order rejectOrder(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);
        if (user.getRole() != UserTypes.ADMIN) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Only admins can reject orders");
        }

        return orderService.rejectOrder(id);
    }
}

