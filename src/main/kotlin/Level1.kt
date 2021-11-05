import java.io.File

fun main(args: Array<String>) {
    solveAll()
}

fun solveLevel1(filename: String): String {
    var result = ""
    val lines = File(filename).readLines()
    val allArgs = lines.drop(1)
        .flatMap { it.split(" ") }

    var print = false

    for (arg in allArgs) {
        if (!print) {
            if (arg == "print") {
                print = true;
            }
        } else {
            result += arg
            print = false
        }
    }
    return result
}

fun solveAll() {
    val level = "level1"
    val levelDir = "C:\\Users\\pia\\Downloads\\untitled\\src\\main\\resources\\$level"

    File(levelDir).walk().filter { it.isFile && it.absolutePath.endsWith(".in") }.forEach {
        val outPath = it.absolutePath.replace(".in", ".out")
        File(outPath).writeText(solveLevel1(it.absolutePath))
    }
}