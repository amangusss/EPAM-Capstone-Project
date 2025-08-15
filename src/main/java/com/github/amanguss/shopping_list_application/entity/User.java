package com.github.amanguss.shopping_list_application.entity;

import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.hibernate.proxy.HibernateProxy;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    Integer id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", nullable = false)
    String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", nullable = false)
    String lastName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email_address", unique = true, nullable = false)
    String email;

    @Column(name = "password_hash", nullable = false)
    String password;

    @Column(name = "phone_number")
    String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth")
    Date dateOfBirth;

    @Column(name = "registration_date", nullable = false)
    LocalDateTime registrationDate;

    @Column(name = "last_login_date")
    LocalDateTime lastLoginDate;

    @Column(name = "account_status", nullable = false)
    @Enumerated(EnumType.STRING)
    AccountStatus accountStatus;

    @Column(name = "email_verified")
    Boolean isVerified;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ShoppingList> shoppingLists;

    @OneToMany(mappedBy = "sharedTo", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ListShare> sharesReceived;

    @OneToMany(mappedBy = "sharedBy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ListShare> sharesSent;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<UserSession> sessions;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}