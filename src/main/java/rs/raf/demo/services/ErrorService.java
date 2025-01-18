package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.repositories.ErrorRepository;

import java.util.List;

@Service
public class ErrorService {

    @Autowired
    private ErrorRepository errorRepository;

    public List<ErrorMessage> getErrors(Long userId, boolean isAdmin, String userEmail) {
        if (isAdmin) {
            if (userId != null) {
                return errorRepository.findByOrderCreatedById(userId);
            }
            return errorRepository.findAll();
        } else {
            return errorRepository.findByOrderCreatedByEmail(userEmail);
        }
    }
}
