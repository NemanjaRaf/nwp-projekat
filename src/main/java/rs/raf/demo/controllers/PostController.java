package rs.raf.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Post;
import rs.raf.demo.services.PostService;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return this.postService.save(post);
    }

    @GetMapping
    public List<Post> all() {
        return this.postService.findAll();
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable("id") Integer id, @RequestBody Post post) {
        post.setId(id);
        return this.postService.save(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        this.postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
