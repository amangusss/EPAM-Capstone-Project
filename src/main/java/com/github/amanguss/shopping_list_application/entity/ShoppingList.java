package com.github.amanguss.shopping_list_application.entity;

import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

import org.hibernate.proxy.HibernateProxy;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shopping_lists")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "list_id")
    Integer id;

    @Column(name = "list_name", nullable = false)
    String name;

    @Column(name = "list_description")
    String description;

    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate;

    @Column(name = "last_modified_date")
    LocalDateTime lastModifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "list_status")
    ListStatus status;

    @Column(name = "is_template", nullable = false)
    Boolean isTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level")
    PriorityLevel priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_SHOPPING_LISTS_OWNER"))
    @ToString.Exclude
    User owner;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Item> items;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "budget_id",
                foreignKey = @ForeignKey(name = "FK_SHOPPING_LISTS_BUDGET"))
    @ToString.Exclude
    Budget budget;

    @OneToMany(mappedBy = "shoppingList", fetch = FetchType.LAZY)
    @ToString.Exclude
    List<ListShare> shares;

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
        ShoppingList that = (ShoppingList) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}