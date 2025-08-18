package com.example.bookstore.dto.category;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class CreateCategoryRequestDto {
    @NotBlank
    private String name;
    private String description;
}
