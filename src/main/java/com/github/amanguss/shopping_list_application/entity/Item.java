package com.github.amanguss.shopping_list_application.entity;

import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

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
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "item_id")
    Integer id;

    @Column(name = "item_name", nullable = false)
    String name;

    @Column(name = "item_description")
    String description;

    @Column(name = "quantity")
    Double quantity;

    @Column(name = "unit_of_measure")
    String unitOfMeasure;

    @Column(name = "estimated_price")
    Double estimatedPrice;

    @Column(name = "actual_price")
    Double actualPrice;

    @Column(name = "is_purchased", nullable = false)
    Boolean isPurchased;

    @Column(name = "purchase_date")
    LocalDateTime purchasedDate;

    @Column(name = "added_date", nullable = false)
    LocalDateTime addedDate;

    @Column(name = "priority_level")
    @Enumerated(EnumType.STRING)
    PriorityLevel priority;

    @Column(name = "notes")
    String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_ITEMS_LIST"))
    @ToString.Exclude
    ShoppingList shoppingList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_ITEMS_CATEGORY"))
    @ToString.Exclude
    Category category;

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
        Item item = (Item) o;
        return getId() != null && Objects.equals(getId(), item.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
