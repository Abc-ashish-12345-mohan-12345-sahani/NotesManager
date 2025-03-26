package android.notesmanager.ui.theme.repository

import android.notesmanager.ui.theme.apiServices.ApiService
import android.notesmanager.ui.theme.dataclass.GetAllNotes
import android.notesmanager.ui.theme.dataclass.GetAllNotesItem
import android.notesmanager.ui.theme.dataclass.PostNewNotes
import okhttp3.ResponseBody
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun fetchAllNotes(): GetAllNotes {
        val notesList = apiService.fetchAllNotes()
        return GetAllNotes().apply { addAll(notesList) }
    }

    suspend fun getNotesById(id: Int): GetAllNotesItem {
        return apiService.getNotesById(id)
    }

    suspend fun createNewNotes(newNotes: PostNewNotes): GetAllNotesItem {
        return apiService.createNewNotes(newNotes)
    }

    suspend fun deleteNotes(id: Int): Response<ResponseBody> {
        return apiService.deleteNotes(id)
    }

    suspend fun updateNotes(id: Int, updatedNotes: PostNewNotes): GetAllNotesItem {
        return apiService.updateNotes(id, updatedNotes)
    }
}