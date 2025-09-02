package com.medicofamiliadigital.data.repository

import android.graphics.Bitmap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.medicofamiliadigital.domain.repository.DocumentAnalysisRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentAnalysisRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DocumentAnalysisRepository {

    private val documentAnalysisCollection = firestore.collection("document_analysis")
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun analyzeDocument(documentId: String, imageBitmap: Bitmap): Result<String> {
        return try {
            val image = InputImage.fromBitmap(imageBitmap, 0)
            val result = textRecognizer.process(image).await()
            val extractedText = result.text

            // Save extracted text to Firestore
            documentAnalysisCollection.document(documentId).set(mapOf(
                "extractedText" to extractedText,
                "analysisDate" to com.google.firebase.Timestamp.now()
            )).await()

            // TODO: Integrate with a more advanced AI model for actual analysis summary
            val dummyAnalysis = "Análise preliminar do documento: \n\n" + 
                                "Texto extraído: \n\n" + 
                                extractedText.take(500) + "...\n\n" + 
                                "Esta é uma análise simulada. Para uma análise completa, integre com um modelo de IA mais avançado."

            Result.success(dummyAnalysis)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAnalysisResult(documentId: String): Result<String> {
        return try {
            val document = documentAnalysisCollection.document(documentId).get().await()
            val extractedText = document.getString("extractedText")
            
            extractedText?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Análise não encontrada para este documento."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

