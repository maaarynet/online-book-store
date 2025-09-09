package com.example.bookstore.dto.category;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class CreateCategoryRequestDto {
    @NotBlank
    private String name;
    private String description;
}
