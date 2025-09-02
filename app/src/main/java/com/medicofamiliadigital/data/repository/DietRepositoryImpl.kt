package com.medicofamiliadigital.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medicofamiliadigital.data.model.DietPlan
import com.medicofamiliadigital.data.model.FoodItem
import com.medicofamiliadigital.data.model.Meal
import com.medicofamiliadigital.data.model.Profile
import com.medicofamiliadigital.domain.repository.DietRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DietRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DietRepository {

    private val dietPlansCollection = firestore.collection("diet_plans")

    override suspend fun generateDietPlan(profile: Profile): Result<DietPlan> {
        return try {
            // This is a simplified dummy implementation. In a real app, this would involve
            // calling an external AI service or a more complex local algorithm
            val dummyMeals = listOf(
                Meal(
                    name = "Café da Manhã",
                    time = "08:00",
                    foodItems = listOf(
                        FoodItem("Ovos mexidos", "2 unidades", 150),
                        FoodItem("Pão integral", "1 fatia", 80),
                        FoodItem("Café sem açúcar", "1 xícara", 5)
                    )
                ),
                Meal(
                    name = "Almoço",
                    time = "13:00",
                    foodItems = listOf(
                        FoodItem("Frango grelhado", "150g", 250),
                        FoodItem("Arroz integral", "100g", 130),
                        FoodItem("Salada mista", "à vontade", 50)
                    )
                ),
                Meal(
                    name = "Jantar",
                    time = "19:00",
                    foodItems = listOf(
                        FoodItem("Peixe assado", "120g", 180),
                        FoodItem("Batata doce", "100g", 90),
                        FoodItem("Brócolis cozido", "100g", 30)
                    )
                )
            )

            val totalCalories = dummyMeals.sumOf { meal -> meal.foodItems.sumOf { it.calories } }

            val newDietPlan = DietPlan(
                profileId = profile.id,
                dailyCalories = totalCalories,
                meals = dummyMeals,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            dietPlansCollection.document(profile.id).set(newDietPlan).await()
            Result.success(newDietPlan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDietPlan(profileId: String): Result<DietPlan> {
        return try {
            val document = dietPlansCollection.document(profileId).get().await()
            val dietPlan = document.toObject(DietPlan::class.java)?.copy(id = document.id)

            dietPlan?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Plano alimentar não encontrado para este perfil."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

