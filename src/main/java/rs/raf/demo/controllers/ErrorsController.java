package rs.raf.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.User;
import rs.raf.demo.model.UserTypes;
import rs.raf.demo.services.ErrorService;
import rs.raf.demo.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/errors")
@CrossOrigin
public class ErrorsController {

    @Autowired
    private ErrorService errorService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<ErrorMessage> getErrors(@RequestParam(required = false) Long userId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);

        boolean isAdmin = user.getRole() == UserTypes.ADMIN;

        return errorService.getErrors(userId, isAdmin, username);
    }
}
