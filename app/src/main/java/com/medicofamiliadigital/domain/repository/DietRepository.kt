package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.DietPlan
import com.medicofamiliadigital.data.model.Profile

interface DietRepository {
    suspend fun generateDietPlan(profile: Profile): Result<DietPlan>
    suspend fun getDietPlan(profileId: String): Result<DietPlan>
}

