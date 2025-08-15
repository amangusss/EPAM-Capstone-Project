package com.github.amanguss.shopping_list_application.entity;

import com.github.amanguss.shopping_list_application.entity.enums.Period;

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
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "budget_id")
    Integer id;

    @Column(name = "budget_limit", nullable = false)
    Double limit;

    @Column(name = "budget_currency", length = 3)
    String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_period", length = 50)
    private Period period;

    @Column(name = "creation_date", nullable = false)
    LocalDateTime creationDate;

    @Column(name = "is_active")
    Boolean isActive;

    @OneToOne(mappedBy = "budget")
    @ToString.Exclude
    ShoppingList shoppingList;

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
        Budget budget = (Budget) o;
        return getId() != null && Objects.equals(getId(), budget.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}