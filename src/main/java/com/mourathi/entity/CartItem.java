package com.mourathi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "cart_id",
                                "product_id"
                        }
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name="cart_id",
            nullable=false
    )
    private Cart cart;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name="product_id",
            nullable=false
    )
    private Product product;


    @Column(nullable=false)
    private Integer quantity;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column
    private String updatedBy;
}
