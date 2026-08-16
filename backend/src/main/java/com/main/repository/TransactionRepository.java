package com.main.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.main.entity.Transaction;
import com.main.entity.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    Page<Transaction> findByUserAndType(User user, String type, Pageable pageable);

    Page<Transaction> findByUserAndCategory( User user, String category, Pageable pageable);

    Page<Transaction> findByUserAndTransactionDate( User user, LocalDate transactionDate, Pageable pageable);

    Page<Transaction> findByUserAndTypeAndCategory( User user, String type, String category, Pageable pageable );

    Page<Transaction> findByUserAndTypeAndTransactionDate( User user, String type, LocalDate transactionDate, Pageable pageable );

    Page<Transaction> findByUserAndCategoryAndTransactionDate( User user, String category, LocalDate transactionDate, Pageable pageable );

    Page<Transaction> findByUserAndTypeAndCategoryAndTransactionDate( User user, String type, String category, LocalDate transactionDate, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user = :user
            AND t.type = :type
            """)
    BigDecimal calculateTotalByType( @Param("user") User user, @Param("type") String type
    );
    
    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.user = :user
            AND (:type IS NULL OR t.type = :type)
            AND (:category IS NULL OR t.category = :category)
            AND (:date IS NULL OR t.transactionDate = :date)
            AND (
                :startDate IS NULL
                OR t.transactionDate >= :startDate
            )
            AND (
                :endDate IS NULL
                OR t.transactionDate <= :endDate
            )
            """)
    Page<Transaction> findTransactions(
            @Param("user") User user,
            @Param("type") String type,
            @Param("category") String category,
            @Param("date") LocalDate date,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}