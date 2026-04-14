package me.akshawop.journalApp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import me.akshawop.journalApp.entity.User;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    User findByUsername(String username);

    void deleteByUsername(String username);
}
