package com.crm.repository;

import com.crm.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Customer Repository
 *
 * @author CRM Team
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    List<Customer> findByStatus(String status);

    Page<Customer> findByStatus(String status, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.deleted = false AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Customer> searchCustomers(String keyword, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.assignedUserId = :userId AND c.deleted = false")
    Page<Customer> findByAssignedUserId(Long userId, Pageable pageable);
}
