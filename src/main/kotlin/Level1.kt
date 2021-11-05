import java.io.File
import java.lang.Error
import java.util.*

// TODO
// skipping to end or start might be a problem if reserved words can be used as variable names or strings

fun main(args: Array<String>) {
   solveAll()
   // val res = solveLevel1("C:\\Users\\pia\\Downloads\\untitled\\src\\main\\resources\\level4\\level4_example3.in")
   // println(res)
}

var index = 0
var input = listOf<String>()
var blocks : Deque<Block> = LinkedList()
var executions: Queue<Block> = LinkedList()
val variables = mutableMapOf<String, String>()
const val error = "ERROR"

data class Block(
    val startIndex: Int,
    val statement: String,
    var goTo: Int = -1,
    val executions: Queue<Block> = LinkedList() // start else or if
)

fun solveLevel1(filename: String): List<String> {
    val results = mutableListOf<String>()
    var currentResult = ""
    val lines = File(filename).readLines()
    input = lines.drop(1)
        .flatMap { it.trim().split(" ") }
    index = 0

    while (index < input.size) {
        try {
            val statement = input[index]
            when (statement) {
                "postpone" -> {
                    blocks.peek().executions.add(Block(index, "postpone"))
                    index++
                    skipBlock()
                }
                "var" -> {
                    val name = input[index + 1]
                    if (variables.containsKey(name)) {
                        throw Error("Variable already exists")
                    }
                    val value = parseVariableOrValue(input[index + 2])
                    variables[name] = value
                    index += 3
                }
                "set" -> {
                    val name = input[index + 1]
                    if (!variables.containsKey(name)) {
                        throw Error("Variable does not exist")
                    }
                    val value = parseVariableOrValue(input[index + 2])
                    variables[name] = value
                    index += 3
                }
                "print" -> {
                    currentResult += parseVariableOrValue(input[index + 1])
                    index += 2
                }
                "start" -> {
                    variables.clear()
                    blocks.clear()
                    blocks.push(Block(index, "start"))
                    index++
                }
                "end" -> {
                    val currentBlock = blocks.peek()
                    if (currentBlock.executions.isNotEmpty()) {
                        currentBlock.goTo = index
                        val firstExec = currentBlock.executions.remove()
                        blocks.add(Block(firstExec.startIndex, "postpone"))
                        index = firstExec.startIndex + 1
                    } else {
                        index = if (currentBlock.goTo != -1) { currentBlock.goTo } else { index }
                        blocks.pop()
                        if (currentBlock.statement == "start") {
                            results.add(currentResult)
                            currentResult = ""
                            index++
                        } else if (currentBlock.statement == "if") {
                            index += 2
                            skipBlock()
                        } else {
                            index++
                        }
                    }
                }
                "return" -> {
                    results.add(currentResult)
                    currentResult = ""
                    moveToNextStart()
                }
                "if" -> {
                    val ifIndex = index
                    val isTrue = parseVariableOrBoolean(input[index + 1])
                    index += 2
                    if (!isTrue) {
                        skipBlock()
                    } else {
                        blocks.push(Block(ifIndex, "if"))
                    }
                }
                "else" -> {
                    blocks.push(Block(index, "else"))
                    index++
                }

            }
        } catch (e: Error) {
            currentResult = ""
            results.add("ERROR")
            moveToNextStart()
        }
    }

    return results
}

private fun moveToNextStart() {
    while (index < input.size && input[index] != "start") {
        index++
    }
}

private fun skipBlock() {
    var blockCounter = 1
    while (blockCounter != 0) {
        if (input[index] == "end") {
            blockCounter -= 1
        } else if (input[index] == "start" || input[index] == "if" || input[index] == "else" || input[index] == "postpone") {
            blockCounter += 1
        }
        index++
    }
}

fun parseVariableOrBoolean(argument: String) : Boolean {
    if (argument == "true" || argument == "false") {
        return argument == "true"
    } else {
        val variable = variables[argument]
        if (variable == "true") {
            return true
        } else if (variable == "false") {
            return false
        }
    }
    throw Error("can not parse boolean")
}

fun parseVariableOrValue(argument: String) : String {
    return variables.getOrDefault(argument, argument)
}

fun solveAll() {
    val level = "level4"
    val levelDir = "C:\\Users\\pia\\Downloads\\untitled\\src\\main\\resources\\$level"

    File(levelDir).walk().filter { it.isFile && it.absolutePath.endsWith(".in") }.forEach {
        val outPath = it.absolutePath.replace(".in", ".out")
        File(outPath).writeText(solveLevel1(it.absolutePath).joinToString("\n"))
    }
}