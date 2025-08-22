package com.example.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OrderRequestDto {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9\\s,.-]+$")
    private String shippingAddress;
}
