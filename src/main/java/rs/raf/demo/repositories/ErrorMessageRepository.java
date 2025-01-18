package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.demo.model.ErrorMessage;

public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {
}
