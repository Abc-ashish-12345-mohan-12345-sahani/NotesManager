package android.notesmanager.ui.theme.dataclass

class GetAllNotes : ArrayList<GetAllNotesItem>()

data class GetAllNotesItem(
    val id: Int,
    val name: String,
    val description: String,
    val createdTimeORDate: String,
)