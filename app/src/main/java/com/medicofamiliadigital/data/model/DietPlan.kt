package com.medicofamiliadigital.data.model

import com.google.firebase.Timestamp

data class DietPlan(
    val id: String = "",
    val profileId: String = "",
    val dailyCalories: Int = 0,
    val meals: List<Meal> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

data class Meal(
    val name: String = "",
    val time: String = "",
    val foodItems: List<FoodItem> = emptyList()
)

data class FoodItem(
    val name: String = "",
    val quantity: String = "",
    val calories: Int = 0
)


