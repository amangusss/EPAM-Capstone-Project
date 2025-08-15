package com.github.amanguss.shopping_list_application.entity;

import org.hibernate.proxy.HibernateProxy;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "session_id")
    Integer id;

    @Column(name = "session_token", unique = true, nullable = false)
    String sessionToken;

    @Column(name = "login_timestamp")
    LocalDateTime loginTime;

    @Column(name = "logout_timestamp")
    LocalDateTime logoutTime;

    @Column(name = "last_activity_timestamp")
    LocalDateTime lastActivityTime;

    @Column(name = "last_modified_date")
    LocalDateTime lastModifiedDate;

    @Column(name = "ip_address")
    String ipAddress;

    @Column(name = "user_agent")
    String userAgent;

    @Column(name = "is_active")
    Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "user_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_USER_SESSIONS_USER"))
    User user;

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
        UserSession that = (UserSession) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
