package backend

import kotlinx.serialization.Serializable
import util.File

@Serializable
data class Workspace(val name: String, val file: File)
