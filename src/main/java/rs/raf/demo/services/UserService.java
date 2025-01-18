package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.UpdateUser;
import rs.raf.demo.model.User;
import rs.raf.demo.model.UserTypes;
import rs.raf.demo.repositories.UserRepository;

import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, TaskScheduler taskScheduler) {
        this.passwordEncoder = passwordEncoder;

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User myUser = this.findByEmail(email);
        if (myUser == null) {
            throw new UsernameNotFoundException("User name " + email + " not found");
        }

        return new org.springframework.security.core.userdetails.User(
                myUser.getEmail(),
                myUser.getPassword(),
                myUser.getPermissions().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        ) {
            @Override
            public String getUsername() {
                return super.getUsername();
            }

            public UserTypes getRole() {
                return myUser.getRole();
            }
        };
    }

    public User create(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user = this.userRepository.save(user);
        System.out.println("Service: User created");
        return user;
    }

    public Page<User> paginate(Integer page, Integer size) {
        return this.userRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public User findById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User update(Long id, UpdateUser user) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPermissions(user.getPermissions());
        System.out.println("Service: User updated" + user.getPermissions());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public void delete(Long id) {
        this.userRepository.deleteById(id);
    }
}
