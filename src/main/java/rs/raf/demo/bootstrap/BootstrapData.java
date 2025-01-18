package rs.raf.demo.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.raf.demo.model.*;
import rs.raf.demo.repositories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final DishRepository dishRepository;

    @Autowired
    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder, DishRepository dishRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dishRepository = dishRepository;
    }

    private void loadDishes() {
        Dish dish1 = new Dish();
        dish1.setName("Pasta");
        dish1.setPrice(299.99);
        dish1.setWeightInGrams(300);

        Dish dish2 = new Dish();
        dish2.setName("Pizza");
        dish2.setPrice(499.99);
        dish2.setWeightInGrams(500);

        Dish dish3 = new Dish();
        dish3.setName("Burger");
        dish3.setPrice(199.99);
        dish3.setWeightInGrams(200);


        this.dishRepository.save(dish1);
        this.dishRepository.save(dish2);
        this.dishRepository.save(dish3);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

        this.loadDishes();

        User user1 = new User();
        user1.setUsername("user1");
        user1.setRole(UserTypes.ADMIN);
        user1.setPassword(this.passwordEncoder.encode("user1"));
        user1.setFirstName("Djordje");
        user1.setLastName("Cvarkov");
        user1.setEmail("test@test.com");
        Set<String> permissions = new java.util.HashSet<>();
        permissions.add("can_create_users");
        permissions.add("can_read_users");
        permissions.add("can_update_users");
        permissions.add("can_delete_users");
        permissions.add("can_create_orders");
        permissions.add("can_read_orders");
        permissions.add("can_update_orders");
        permissions.add("can_cancel_orders");
        permissions.add("can_schedule_orders");
        permissions.add("can_track_orders");
        user1.setPermissions(permissions);
        this.userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(this.passwordEncoder.encode("user2"));
        user2.setFirstName("Dragan");
        user2.setLastName("Torbica");
        user2.setEmail("test2@test.com");
        this.userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("user3");
        user3.setPassword(this.passwordEncoder.encode("user3"));
        user3.setFirstName("");
        user3.setLastName("Boskic");
        user3.setEmail("test3@test.com");
        this.userRepository.save(user3);

        System.out.println("Data loaded!");
    }
}
