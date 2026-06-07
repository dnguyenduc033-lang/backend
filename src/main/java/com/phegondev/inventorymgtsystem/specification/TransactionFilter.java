package com.phegondev.inventorymgtsystem.specification;

import com.phegondev.inventorymgtsystem.models.Transaction;
import com.phegondev.inventorymgtsystem.models.User;
import com.phegondev.inventorymgtsystem.models.Supplier;
import com.phegondev.inventorymgtsystem.models.Product;
import com.phegondev.inventorymgtsystem.models.Category;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionFilter {

    public static Specification<Transaction> byFilter(String searchValue) {
        return (root, query, criteriaBuilder) -> {
            if (searchValue == null || searchValue.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchPattern = "%" + searchValue.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // 1. Kiểm tra các trường của bảng Transaction
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern));
            if (root.get("note") != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("note")), searchPattern));
            }
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("status").as(String.class)), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("transactionType").as(String.class)), searchPattern));

            // 2. Tạo một đối tượng Join duy nhất cho User và tái sử dụng nó
            jakarta.persistence.criteria.Join<Transaction, com.phegondev.inventorymgtsystem.models.User> userJoin = root.join("user", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("name")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("email")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("phoneNumber")), searchPattern));

            // 3. Tạo một đối tượng Join duy nhất cho Supplier và tái sử dụng nó
            jakarta.persistence.criteria.Join<Transaction, com.phegondev.inventorymgtsystem.models.Supplier> supplierJoin = root.join("supplier", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(supplierJoin.get("name")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(supplierJoin.get("contactInfo")), searchPattern));

            // 4. Tạo một đối tượng Join duy nhất cho Product và tái sử dụng nó
            jakarta.persistence.criteria.Join<Transaction, com.phegondev.inventorymgtsystem.models.Product> productJoin = root.join("product", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("name")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("sku")), searchPattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("description")), searchPattern));

            // 5. Join sâu từ Product sang Category bằng đối tượng productJoin đã có
            jakarta.persistence.criteria.Join<com.phegondev.inventorymgtsystem.models.Product, com.phegondev.inventorymgtsystem.models.Category> categoryJoin = productJoin.join("category", JoinType.LEFT);
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("name")), searchPattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Transaction> byMonthAndYear(int month, int year) {
        return (root, query, criteriaBuilder) -> {
            Expression<Integer> monthExpression = criteriaBuilder.function("month", Integer.class, root.get("createdAt"));
            Expression<Integer> yearExpression = criteriaBuilder.function("year", Integer.class, root.get("createdAt"));

            Predicate monthPredicate = criteriaBuilder.equal(monthExpression, month);
            Predicate yearPredicate = criteriaBuilder.equal(yearExpression, year);

            return criteriaBuilder.and(monthPredicate, yearPredicate);
        };
    }
}