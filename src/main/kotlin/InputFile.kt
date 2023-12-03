import java.io.File

fun inputFile(fileName: String): File {
    val url = ClassLoader.getSystemResource(fileName)
    return File(url.toURI())
}