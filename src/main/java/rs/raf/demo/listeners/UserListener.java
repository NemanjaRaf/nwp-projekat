package rs.raf.demo.listeners;

import rs.raf.demo.model.User;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

public class UserListener {
    @PostLoad
    private void setFullName(User user) {
        user.setFullName(user.getFirstName() + " " + user.getLastName());
    }

    @PrePersist
    private void prePersist(User user)
    {
        System.out.println("User@prePersist");
        this.setFullName(user);
    }

    @PostPersist
    private void postPersist(User user)
    {
        System.out.println("User@postPersist");
    }
}
