package com.example.Expense.tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    private String title;
    private String description;
    private Double price;
    private LocalDate date;
    private LocalTime time;
    @ManyToOne
    @JoinColumn(name="fk_user_id")
    private User user;
}
