package com.minhadieta.dietaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meal_configuration")
public class MealConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usamos 'name' e não 'mealName' para simplificar
    @Column(nullable = false)
    private String name;

    private String time; // Novo campo: Horário (Ex: "12:00")

    private Double targetCalories; // Novo campo: Meta calórica

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private AppUser user; // Ligação direta com o Usuário (Sem DietProfile por enquanto)

    @OneToMany(mappedBy = "mealConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealItem> mealItems;
}