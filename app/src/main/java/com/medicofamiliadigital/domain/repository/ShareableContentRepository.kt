package com.medicofamiliadigital.domain.repository

import com.medicofamiliadigital.data.model.ShareableContent

interface ShareableContentRepository {
    suspend fun createShareableLink(shareableContent: ShareableContent): Result<String>
    suspend fun getShareableContent(shareId: String): Result<ShareableContent>
    suspend fun deleteShareableLink(shareId: String): Result<Unit>
}

