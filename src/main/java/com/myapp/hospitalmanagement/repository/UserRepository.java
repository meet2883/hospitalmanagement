package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(String name);
}
