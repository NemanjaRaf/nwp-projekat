package rs.raf.demo.repositories;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import rs.raf.demo.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("postRepository")
public class RestPostRepository implements PostRepository {

    private final WebClient webClient;

    public RestPostRepository(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
    }

    @Override
    public <S extends Post> S save(S post) {
        System.out.println(post.getId());
        if (post.getId() == null) {
            return this.webClient
                    .post()
                    .uri("/posts")
                    .body(BodyInserters.fromValue(post))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<S>() {})
                    .block()
                    .getBody();
        }
        else {
            // update
            return this.webClient
                    .put()
                    .uri("/posts/{id}", post.getId())
                    .body(BodyInserters.fromValue(post))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<S>() {})
                    .block()
                    .getBody();
        }
    }

    @Override
    public <S extends Post> Iterable<S> saveAll(Iterable<S> posts) {
        List<S> savedPosts = new ArrayList<>();
        for (S post : posts) {
            savedPosts.add(this.save(post));
        }
        return savedPosts;
    }

    @Override
    public Optional<Post> findById(Integer id) {
        ResponseEntity<Post> response = this.webClient
                .get()
                .uri("/posts/{id}", id)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Post>() {})
                .block();
        if (response == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }

    @Override
    public boolean existsById(Integer id) {
        return this.findById(id).isPresent();
    }

    @Override
    public List<Post> findAll() {
        ResponseEntity<List<Post>> response = this.webClient
                .get()
                .uri("/posts")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Post>>() {})
                .block();
        if (response == null) {
            return null;
        }

        return response.getBody();
    }

    @Override
    public Iterable<Post> findAllById(Iterable<Integer> ids) {
        List<Post> posts = new ArrayList<>();
        for (Integer id : ids) {
            Optional<Post> postOptional = this.findById(id);
            if (postOptional.isPresent()) {
                posts.add(postOptional.get());
            }
        }

        return posts;
    }

    @Override
    public long count() {
        return findAll().size();
    }

    @Override
    public void deleteById(Integer id) {
        this.webClient
                .delete()
                .uri("/posts/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public void delete(Post post) {
        this.deleteById(post.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Post> posts) {
        for (Post post : posts) {
            this.delete(post);
        }
    }

    @Override
    public void deleteAll() {
        for (Post post: this.findAll()) {
            this.delete(post);
        }
    }
}
