package com.crm.repository;

import com.crm.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Contact Repository
 *
 * @author CRM Team
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByCustomerId(Long customerId);

    List<Contact> findByStatus(String status);

    Page<Contact> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.deleted = false AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Contact> searchContacts(String keyword, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.customerId = :customerId AND c.isPrimary = true AND c.deleted = false")
    List<Contact> findPrimaryContactsByCustomerId(Long customerId);
}
