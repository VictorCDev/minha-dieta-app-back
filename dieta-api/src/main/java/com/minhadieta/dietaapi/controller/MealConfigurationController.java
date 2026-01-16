package com.minhadieta.dietaapi.controller;

import com.minhadieta.dietaapi.dto.MealConfigRequest;
import com.minhadieta.dietaapi.dto.MealConfigResponse;
import com.minhadieta.dietaapi.model.AppUser;
import com.minhadieta.dietaapi.model.MealConfiguration;
import com.minhadieta.dietaapi.repository.MealConfigurationRepository;
import com.minhadieta.dietaapi.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meal-configurations")
public class MealConfigurationController {

    private final MealConfigurationRepository mealConfigRepository;
    private final UserRepository userRepository;

    public MealConfigurationController(MealConfigurationRepository mealConfigRepository, UserRepository userRepository) {
        this.mealConfigRepository = mealConfigRepository;
        this.userRepository = userRepository;
    }

    // LISTAR todas as configurações do usuário logado
    @GetMapping
    public ResponseEntity<List<MealConfigResponse>> getMyMeals() {
        AppUser user = getAuthenticatedUser();
        List<MealConfiguration> configs = mealConfigRepository.findByUser(user);

        List<MealConfigResponse> response = configs.stream()
                .map(m -> new MealConfigResponse(m.getId(), m.getName(), m.getTime(), m.getTargetCalories()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // CRIAR nova configuração
    @PostMapping
    public ResponseEntity<MealConfigResponse> createMeal(@RequestBody MealConfigRequest request) {
        AppUser user = getAuthenticatedUser();

        MealConfiguration newConfig = new MealConfiguration();
        newConfig.setName(request.getName());
        newConfig.setTime(request.getTime());
        newConfig.setTargetCalories(request.getTargetCalories());
        newConfig.setUser(user); // Vincula ao usuário logado!

        MealConfiguration saved = mealConfigRepository.save(newConfig);

        return ResponseEntity.ok(new MealConfigResponse(saved.getId(), saved.getName(), saved.getTime(), saved.getTargetCalories()));
    }

    // DELETAR configuração
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        AppUser user = getAuthenticatedUser();

        // Verifica se a refeição existe e pertence ao usuário logado (Segurança!)
        mealConfigRepository.findById(id).ifPresent(config -> {
            if (config.getUser().getId().equals(user.getId())) {
                mealConfigRepository.delete(config);
            }
        });

        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para pegar o usuário do Token JWT
    private AppUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // O "username" no nosso caso é o email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}