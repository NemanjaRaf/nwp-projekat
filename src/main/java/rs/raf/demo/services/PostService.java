package rs.raf.demo.services;

import org.springframework.stereotype.Service;
import rs.raf.demo.model.Post;
import rs.raf.demo.repositories.PostRepository;

import java.util.List;

@Service
public class PostService {

    private PostRepository postRepository;

    public PostService(PostRepository postRepository)
    {
        this.postRepository = postRepository;
    }

    public Post save(Post post)
    {
        return this.postRepository.save(post);
    }

    public List<Post> findAll()
    {
        return (List<Post>) this.postRepository.findAll();
    }

    public void delete(Integer id)
    {
        this.postRepository.deleteById(id);
    }

}
