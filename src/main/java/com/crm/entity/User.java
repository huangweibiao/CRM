package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User Entity
 * Represents system users
 *
 * @author CRM Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "provider", length = 20)
    private String provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
