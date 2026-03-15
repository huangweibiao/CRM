package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Deal Entity
 * Represents business deals/opportunities
 *
 * @author CRM Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "deals")
public class Deal extends BaseEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "stage", length = 50)
    private String stage = "PROSPECT";

    @Column(name = "probability")
    private Integer probability = 0;

    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;

    @Column(name = "actual_close_date")
    private LocalDate actualCloseDate;

    @Column(name = "status", length = 20)
    private String status = "OPEN";

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "priority", length = 20)
    private String priority = "MEDIUM";
}
