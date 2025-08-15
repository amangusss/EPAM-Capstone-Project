package com.github.amanguss.shopping_list_application.entity;

import com.github.amanguss.shopping_list_application.entity.enums.Permission;

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
@Table(name = "list_shares")
public class ListShare {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "share_id")
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type")
    Permission permission;

    @Column(name = "shared_date", nullable = false)
    LocalDateTime sharedDate;

    @Column(name = "expiration_date")
    LocalDateTime expirationDate;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_LIST_SHARES_LIST"))
    @ToString.Exclude
    ShoppingList shoppingList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by_user_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_LIST_SHARES_SHARED_BY"))
    @ToString.Exclude
    User sharedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_user_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_LIST_SHARES_SHARED_WITH"))
    @ToString.Exclude
    User sharedTo;

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
        ListShare listShare = (ListShare) o;
        return getId() != null && Objects.equals(getId(), listShare.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}