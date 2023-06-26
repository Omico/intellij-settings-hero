import org.gradle.internal.impldep.com.google.common.io.Files.isFile

file("src/main/resources/messages").walk()
    .maxDepth(1)
    .filter(File::isFile)
    .filter { it.extension == "properties" }
    .forEach {
        it.readLines()
            .map(String::trim)
            .sorted()
            .joinToString(separator = "\n", postfix = "\n")
            .let(it::writeText)
    }
