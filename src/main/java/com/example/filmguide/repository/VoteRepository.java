package com.example.filmguide.repository;

import java.util.List;

import com.example.filmguide.model.Vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "votes", collectionResourceRel = "votes", itemResourceRel = "vote")
public interface VoteRepository extends JpaRepository<Vote, Long>{
    
    public List<Vote> findByMovieId(String movieId);
}