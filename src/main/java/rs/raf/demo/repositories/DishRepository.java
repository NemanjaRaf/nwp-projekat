package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.Dish;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    Optional<Dish> findById(Long id);
    List<Dish> findAll();
    @Query("SELECT d FROM Dish d WHERE d.id IN :ids")
    List<Dish> findDishesByIds(@Param("ids") List<Long> ids);
}