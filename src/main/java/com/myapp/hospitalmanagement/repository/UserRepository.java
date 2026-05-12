package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.User;
import com.myapp.hospitalmanagement.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
    User findByName(String name);
    User findByEmail(String email);
}
