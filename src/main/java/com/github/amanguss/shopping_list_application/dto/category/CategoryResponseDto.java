package com.github.amanguss.shopping_list_application.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

    private Integer id;
    private String name;
    private String description;
    private String color;
    private Boolean isSystemCategory;
    private LocalDateTime creationDate;
    private Integer displayOrder;
    private Integer itemCount;
}