package com.minhadieta.dietaapi.service;

import com.minhadieta.dietaapi.dto.MealItemRequest;
import com.minhadieta.dietaapi.dto.MealItemResponse;
import com.minhadieta.dietaapi.model.AppUser;
import com.minhadieta.dietaapi.model.Ingredient;
import com.minhadieta.dietaapi.model.MealConfiguration;
import com.minhadieta.dietaapi.model.MealItem;
import com.minhadieta.dietaapi.repository.IngredientRepository;
import com.minhadieta.dietaapi.repository.MealConfigurationRepository;
import com.minhadieta.dietaapi.repository.MealItemRepository;
import com.minhadieta.dietaapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealItemService {

    @Autowired
    private MealItemRepository mealItemRepository;

    @Autowired
    private MealConfigurationRepository mealConfigurationRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MealItemResponse create(Long userId, Long dietProfileId, Long mealId, MealItemRequest request) {
        // Ignoramos dietProfileId por enquanto no MVP
        MealConfiguration mealConfig = findMealConfigForUser(userId, mealId);

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente com ID " + request.getIngredientId() + " não encontrado."));

        MealItem newMealItem = new MealItem();
        newMealItem.setMealConfiguration(mealConfig);
        newMealItem.setIngredient(ingredient);
        newMealItem.setQuantity(request.getQuantity());
        newMealItem.setUnit(request.getUnit());

        MealItem savedItem = mealItemRepository.save(newMealItem);
        return convertToResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public List<MealItemResponse> findAllByMeal(Long userId, Long dietProfileId, Long mealId) {
        MealConfiguration mealConfig = findMealConfigForUser(userId, mealId);
        return mealConfig.getMealItems().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MealItemResponse update(Long userId, Long dietProfileId, Long mealId, Long itemId, MealItemRequest request) {
        findMealConfigForUser(userId, mealId); // Validação de segurança

        MealItem mealItem = mealItemRepository.findByIdAndMealConfiguration_Id(itemId, mealId)
                .orElseThrow(() -> new IllegalArgumentException("Item de refeição não encontrado ou não pertence a esta refeição."));

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente com ID " + request.getIngredientId() + " não encontrado."));

        mealItem.setIngredient(ingredient);
        mealItem.setQuantity(request.getQuantity());
        mealItem.setUnit(request.getUnit());

        MealItem updatedItem = mealItemRepository.save(mealItem);
        return convertToResponse(updatedItem);
    }

    @Transactional
    public void delete(Long userId, Long dietProfileId, Long mealId, Long itemId) {
        findMealConfigForUser(userId, mealId); // Validação de segurança

        MealItem mealItem = mealItemRepository.findByIdAndMealConfiguration_Id(itemId, mealId)
                .orElseThrow(() -> new IllegalArgumentException("Item de refeição não encontrado ou não pertence a esta refeição."));

        mealItemRepository.delete(mealItem);
    }

    /**
     * Método auxiliar corrigido: Busca a refeição e garante que pertence ao Usuário.
     * Removemos a dependência do DietProfile.
     */
    private MealConfiguration findMealConfigForUser(Long userId, Long mealId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        return mealConfigurationRepository.findByIdAndUser(mealId, user)
                .orElseThrow(() -> new IllegalArgumentException("Refeição não encontrada ou não pertence ao usuário especificado."));
    }

    private MealItemResponse convertToResponse(MealItem mealItem) {
        return new MealItemResponse(
                mealItem.getId(),
                mealItem.getIngredient().getId(),
                mealItem.getIngredient().getName(),
                mealItem.getQuantity(),
                mealItem.getUnit()
        );
    }
}