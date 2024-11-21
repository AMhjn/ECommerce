package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.id.IntegralDataTypeHolder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "Product Name must atlest contain 3 characters")
    private String productName;
    private String image;
    @NotBlank
    @Size(min = 3, message = "Product Description must atlest contain 3 characters")
    private  String description;
    private Integer quantity;
    private double price; // 100
    private double specialPrice; // 25
    private double discount;// 75

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
