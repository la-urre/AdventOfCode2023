fun solveDay3() {
    inputFile("day3_input").useLines { lines ->
        val engine = parseEngine(lines)
        println(engine.sumOfParts)

        val gearRatioSum = engine.parts
            .groupBy { it.linkedSymbol }
            .filterKeys { it.isGear }
            .filterValues { it.size == 2 }
            .values
            .sumOf { gearAdjacentParts ->
                gearAdjacentParts.map { part -> part.number }
                    .reduce { gearRatio, partNumber -> gearRatio * partNumber }
            }
        println(gearRatioSum)
    }
}

fun parseEngine(lines: Sequence<String>): Engine {
    val engineGrid = lines.map { it.toCharArray() }.toList().toTypedArray()
    val engineParts = mutableSetOf<PotentialPart>()

    val adjacentSymbols = adjacentSymbols(engineGrid)
    var currentPotentialPart: PotentialPart? = null
    for ((rowNumber, row) in engineGrid.withIndex()) {
        for ((columnNumber, character) in row.withIndex()) {
            when {
                character.isDigit() -> {
                    if (currentPotentialPart == null) {
                        currentPotentialPart = PotentialPart()
                    }
                    currentPotentialPart.addDigit(character.digitToInt())
                    val symbol = adjacentSymbols[Position(rowNumber, columnNumber)]
                    if (symbol != null) {
                        currentPotentialPart.linkedSymbol = symbol
                        engineParts.add(currentPotentialPart)
                    }
                }

                else -> {
                    currentPotentialPart = null
                }
            }
        }
        currentPotentialPart = null
    }
    return Engine(engineParts.map { Part(it.number, it.linkedSymbol!!) })
}

private fun adjacentSymbols(engineGrid: Array<CharArray>): Map<Position, Symbol> {
    val potentialPartPositions = mutableMapOf<Position, Symbol>()
    engineGrid.forEachIndexed { rowNumber, row ->
        row.forEachIndexed { columnNumber, character ->
            if (isSymbol(character)) {
                val symbol = Symbol(character)
                potentialPartPositions[Position(rowNumber - 1, columnNumber - 1)] = symbol
                potentialPartPositions[Position(rowNumber - 1, columnNumber)] = symbol
                potentialPartPositions[Position(rowNumber - 1, columnNumber + 1)] = symbol
                potentialPartPositions[Position(rowNumber, columnNumber - 1)] = symbol
                potentialPartPositions[Position(rowNumber, columnNumber + 1)] = symbol
                potentialPartPositions[Position(rowNumber + 1, columnNumber - 1)] = symbol
                potentialPartPositions[Position(rowNumber + 1, columnNumber)] = symbol
                potentialPartPositions[Position(rowNumber + 1, columnNumber + 1)] = symbol
            }
        }
    }
    return potentialPartPositions
}

data class Position(val rowNumber: Int, val columnNumber: Int)

class Symbol(char: Char) {
    val isGear = char == '*'
}

fun isSymbol(char: Char) = !char.isDigit() && char != '.'

data class Engine(val parts: List<Part>) {
    val sumOfParts = parts.sumOf { it.number }
}

data class Part(val number: Int, val linkedSymbol: Symbol)

class PotentialPart {
    var linkedSymbol: Symbol? = null
    var number = 0
    fun addDigit(digit: Int) {
        number = number * 10 + digit
    }
}