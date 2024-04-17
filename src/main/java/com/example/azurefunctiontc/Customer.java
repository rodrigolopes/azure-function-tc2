package com.example.azurefunctiontc;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
public final class Customer {
    @Id
    private Long id;
    private String name;
}
