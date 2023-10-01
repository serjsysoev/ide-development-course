package util

import util.rope.LineMetrics
import util.rope.LineMetricsCalculator
import util.rope.Rope

val HomeFolder: File get() = java.io.File(System.getProperty("user.home")).toProjectFile()

interface File {
    val name: String
    val isDirectory: Boolean
    val children: List<File>
    val hasChildren: Boolean

    fun read(): Rope<LineMetrics>
}

fun java.io.File.toProjectFile(): File = object : File {
    override val name: String
        get() = this@toProjectFile.name

    override val isDirectory: Boolean
        get() = this@toProjectFile.isDirectory

    override val children: List<File>
        get() = this@toProjectFile
            .listFiles { _, name -> !name.startsWith(".") }
            .orEmpty()
            .map { it.toProjectFile() }

    private val numberOfFiles
        get() = listFiles()?.size ?: 0

    override val hasChildren: Boolean
        get() = isDirectory && numberOfFiles > 0


    override fun read(): Rope<LineMetrics> {
        val stringList = buildList {
            val charArray = CharArray(Rope.SPLIT_LENGTH)
            val reader = this@toProjectFile.bufferedReader()

            while (reader.read(charArray).coerceAtLeast(0) != 0) {
                add(String(charArray))
            }
        }
        return Rope(stringList, LineMetricsCalculator())
    }
}
