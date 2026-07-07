package com.lab.inventory.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record Item(
        Long id,
        @NotBlank String name,
        @Min(0) int quantity
) {
}
