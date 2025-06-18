package com.mygitgor.restaurant.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity{

    @ManyToOne
    private Food food;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private int quantity;
    private Long totalPrice;

    private List<String> ingredients;
}
