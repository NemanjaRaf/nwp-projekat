package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.ErrorMessage;

import java.util.List;

@Repository
public interface ErrorRepository extends JpaRepository<ErrorMessage, Long> {
    List<ErrorMessage> findByOrderCreatedById(Long userId);
    List<ErrorMessage> findByOrderCreatedByEmail(String email);
}
