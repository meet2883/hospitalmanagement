package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.User;
import com.myapp.hospitalmanagement.repository.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Page<User> findAllUser(Specification<User> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = specification != null ? specification.toPredicate(root, query, cb) : cb.conjunction();
        query.where(predicate);

        TypedQuery<User> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<User> results = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);

        Predicate countPredicate = specification != null
                                    ? specification.toPredicate(countRoot, countQuery, cb)
                                    : cb.conjunction();

        countQuery.select(cb.count(countRoot)).where(countPredicate);

        Long total = entityManager.createQuery(countQuery).getSingleResult();
        return new PageImpl<>(results, pageable, total);
    }
}
