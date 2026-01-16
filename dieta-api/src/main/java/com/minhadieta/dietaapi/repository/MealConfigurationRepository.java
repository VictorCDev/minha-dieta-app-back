package com.minhadieta.dietaapi.repository;

import com.minhadieta.dietaapi.model.AppUser;
import com.minhadieta.dietaapi.model.MealConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealConfigurationRepository extends JpaRepository<MealConfiguration, Long> {

    // Busca todas as refeições daquele usuário
    List<MealConfiguration> findByUser(AppUser user);

    // Busca por ID e Usuário (Segurança)
    Optional<MealConfiguration> findByIdAndUser(Long id, AppUser user);
}