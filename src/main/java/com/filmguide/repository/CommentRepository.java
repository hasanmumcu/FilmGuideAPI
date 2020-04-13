package com.filmguide.repository;

import java.util.List;

import com.filmguide.model.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "comments", collectionResourceRel = "comments", itemResourceRel = "comment")
public interface CommentRepository extends JpaRepository<Comment, Long>{
    
    public List<Comment> findByMovieId(String movieId);
}