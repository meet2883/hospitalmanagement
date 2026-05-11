package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityGraph;
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
public class PatientRepositoryImpl implements PatientRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Patient> findAllWithInsurance(Specification<Patient> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
        Root<Patient> root = query.from(Patient.class);

        // Apply specification if present
        Predicate predicate = specification != null ? specification.toPredicate(root, query, cb) : cb.conjunction();
        query.where(predicate);

        // Create typed query
        TypedQuery<Patient> typedQuery = entityManager.createQuery(query);

        // Apply EntityGraph to fetch insurance eagerly
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("Patient.insurance");
        typedQuery.setHint("jakarta.persistence.loadgraph", entityGraph);

        // Apply pagination
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Execute query
        List<Patient> results = typedQuery.getResultList();

        // Get total count for pagination metadata
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Patient> countRoot = countQuery.from(Patient.class);
        Predicate countPredicate = specification != null ? specification.toPredicate(countRoot, countQuery, cb) : cb.conjunction();
        countQuery.select(cb.count(countRoot)).where(countPredicate);

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
