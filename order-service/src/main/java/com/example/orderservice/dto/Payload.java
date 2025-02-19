package com.example.orderservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Payload {
    private String order_id;
    private String user_id;
    private String product_id;
    private int quantity;
    private int unit_price;
    private int total_price;

    @Builder
    public Payload(String order_id, String user_id, String product_id, int quantity, int unit_price, int total_price) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.total_price = total_price;
    }
}
