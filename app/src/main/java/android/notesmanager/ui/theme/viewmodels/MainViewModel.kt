package android.notesmanager.ui.theme.viewmodels

import android.notesmanager.ui.theme.dataclass.GetAllNotes
import android.notesmanager.ui.theme.dataclass.GetAllNotesItem
import android.notesmanager.ui.theme.dataclass.PostNewNotes
import android.notesmanager.ui.theme.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _allNotes = MutableStateFlow(GetAllNotes())
    val allNotes: StateFlow<GetAllNotes> get() = _allNotes

    private val _noteCreationState = MutableStateFlow<GetAllNotesItem?>(null)
    val noteCreationState: StateFlow<GetAllNotesItem?> get() = _noteCreationState

    private val _getSingleNotes = MutableStateFlow<GetAllNotesItem?>(null)
    val getSingleNotes: StateFlow<GetAllNotesItem?> get() = _getSingleNotes

    private val _noteUpdateState = MutableStateFlow<GetAllNotesItem?>(null)
    val noteUpdateState: StateFlow<GetAllNotesItem?> get() = _noteUpdateState

    private val _deleteNotesState = MutableStateFlow<Response<ResponseBody>?>(null)
    val deleteNotes: StateFlow<Response<ResponseBody>?> get() = _deleteNotesState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    fun resetSaveNotesState() {
        _noteCreationState.value = null
        _noteUpdateState.value = null
    }

    fun fetchAllNotes() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _allNotes.value = authRepository.fetchAllNotes()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun getSingleNotes(id: Int) {
        viewModelScope.launch {
            try {
                _getSingleNotes.value = authRepository.getNotesById(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createNewNotes(newNotes: PostNewNotes) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = authRepository.createNewNotes(newNotes)
                _noteCreationState.value = result
                fetchAllNotes()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteNotes(id: Int) {
        viewModelScope.launch {
            try {
                val response = authRepository.deleteNotes(id)
                if (response.isSuccessful) {
                    _deleteNotesState.value = authRepository.deleteNotes(id)
                    fetchAllNotes()
                } else {
                    _deleteNotesState.value = Response.error(response.code(), response.errorBody())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _deleteNotesState.value =
                    Response.error(500, ResponseBody.create(null, "Delete failed"))
            }
        }
    }

    fun updateNotes(id: Int, updatedNotes: PostNewNotes) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _noteUpdateState.value = authRepository.updateNotes(id, updatedNotes)
                fetchAllNotes()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearDeleteNotesState() {
        _deleteNotesState.value = null
    }
}