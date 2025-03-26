package android.notesmanager.ui.theme.apiServices

import android.notesmanager.ui.theme.dataclass.GetAllNotesItem
import android.notesmanager.ui.theme.dataclass.PostNewNotes
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("/courses")
    suspend fun fetchAllNotes(): List<GetAllNotesItem>

    @GET("/courses/{id}")
    suspend fun getNotesById(@Path("id") id: Int): GetAllNotesItem

    @POST("/courses")
    suspend fun createNewNotes(@Body newNotes: PostNewNotes): GetAllNotesItem

    @DELETE("courses/delete/{id}")
    suspend fun deleteNotes(@Path("id") id: Int): Response<ResponseBody>

    @PUT("/courses/{id}")
    suspend fun updateNotes(@Path("id") id: Int, @Body updatedNotes: PostNewNotes): GetAllNotesItem
}