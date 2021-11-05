import java.io.File

fun main(args: Array<String>) {
    solveAll()
}

fun solveLevel1(filename: String): String {
    var result = ""
    val lines = File(filename).readLines()
    val allArgs = lines.drop(1)
        .flatMap { it.split(" ") }

    var index = 0
    var returned = false

    while (!returned && index < allArgs.size) {
        val statement = allArgs[index]
        when(statement) {
            "print" -> {
                result += allArgs[index+1]
                index += 2
            }
            "start" -> index++
            "end" -> index++
            "return" -> returned = true
            "if" -> {
                val isTrue = allArgs[index + 1] == "true"
                index += 2
                if (!isTrue) {
                    while (allArgs[index] != "else") {
                        index++
                    }
                    index++
                }
            }
            "else" -> {
                while (allArgs[index] != "end") {
                    index++
                }
                index++
            }

        }
    }

    return result
}


fun solveAll() {
    val level = "level2"
    val levelDir = "C:\\Users\\pia\\Downloads\\untitled\\src\\main\\resources\\$level"

    File(levelDir).walk().filter { it.isFile && it.absolutePath.endsWith(".in") }.forEach {
        val outPath = it.absolutePath.replace(".in", ".out")
        File(outPath).writeText(solveLevel1(it.absolutePath))
    }
}