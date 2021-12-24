package rs.raf.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import rs.raf.demo.model.Post;

@NoRepositoryBean()
public interface PostRepository extends CrudRepository<Post, Integer> {
}
