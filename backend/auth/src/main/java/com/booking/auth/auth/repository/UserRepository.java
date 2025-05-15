package com.booking.auth.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.booking.auth.auth.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{email:'?0'}")

    User findByEmail(String email);

    Boolean existsByEmail(String email);
}
