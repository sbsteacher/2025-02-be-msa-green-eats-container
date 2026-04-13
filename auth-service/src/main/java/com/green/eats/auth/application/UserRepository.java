package com.green.eats.auth.application;

import com.green.eats.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //쿼리 메소드, select * from user where email = ?
    User findByEmail(String email);
}
