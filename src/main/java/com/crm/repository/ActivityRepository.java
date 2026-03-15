package com.crm.repository;

import com.crm.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Activity Repository
 *
 * @author CRM Team
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByCustomerId(Long customerId);

    List<Activity> findByContactId(Long contactId);

    List<Activity> findByDealId(Long dealId);

    List<Activity> findByUserId(Long userId);

    Page<Activity> findByType(String type, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.activityDate BETWEEN :startDate AND :endDate")
    List<Activity> findByActivityDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM Activity a WHERE a.status = 'SCHEDULED' AND a.activityDate > :now ORDER BY a.activityDate ASC")
    List<Activity> findUpcomingActivities(LocalDateTime now);

    @Query("SELECT a FROM Activity a WHERE a.deleted = false AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Activity> searchActivities(String keyword, Pageable pageable);
}
