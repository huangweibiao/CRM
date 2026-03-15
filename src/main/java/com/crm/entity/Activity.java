package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Activity Entity
 * Represents activities and interactions
 *
 * @author CRM Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @Column(name = "activity_date")
    private LocalDateTime activityDate;

    @Column(name = "duration")
    private Integer duration; // in minutes

    @Column(name = "status", length = 20)
    private String status = "SCHEDULED";

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "outcome", columnDefinition = "TEXT")
    private String outcome;
}
