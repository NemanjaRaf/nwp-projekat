package rs.raf.demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import rs.raf.demo.model.Order;
import rs.raf.demo.services.OrderService;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final OrderService orderService;

    public SchedulerConfig(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 5000)
    public void processScheduledOrders() {
        System.out.println("999999999999999999999999999");
        List<Order> scheduledOrders = orderService.getScheduledOrders();

        for (Order order : scheduledOrders) {
            System.out.println("Scheduled order: " + order.getId());
            if (order.getScheduledTime().isBefore(LocalDateTime.now())) {
                System.out.println("Processing scheduled order: " + order.getId());
                orderService.processScheduledOrder(order);
            }
        }
    }
}