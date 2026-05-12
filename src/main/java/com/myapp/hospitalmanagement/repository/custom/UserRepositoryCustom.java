package com.myapp.hospitalmanagement.repository.custom;

import com.myapp.hospitalmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserRepositoryCustom {
    Page<User> findAllUser(Specification<User> specification, Pageable pageable);
}
