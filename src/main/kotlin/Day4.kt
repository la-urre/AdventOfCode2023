import kotlin.collections.Set
import kotlin.math.pow

fun solveDay4() {
    inputFile("day4_input").useLines { lines ->
        val game = ScratchCardGame(parseScratchCards(lines))
        println(game.points)
        println(game.totalNumberOfCopies)
    }
}

fun parseScratchCards(lines: Sequence<String>): List<ScratchCard> {
    return lines.map { parseScratchCard(it) }.toList()
}

fun parseScratchCard(scratchCardString: String): ScratchCard {
    val cardSplit = scratchCardString.split(": ")
    val cardId = cardSplit.first().split(" ").filter { it.isNotEmpty() }[1].toInt()
    val cardNumbers = cardSplit[1]
    val numbersSplit = cardNumbers.split(" | ")
    val winningNumbers = numbersSplit.first().split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
    val numbersHad = numbersSplit[1].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
    return ScratchCard(cardId, winningNumbers, numbersHad)
}

data class ScratchCardGame(val scratchCards: List<ScratchCard>) {
    val points = scratchCards.sumOf { card -> card.points }
    private val numberOfCopiesById = mutableMapOf<Int, Int>()

    init {
        scratchCards.forEach { card ->
            val numberOfCopies = numberOfCopiesById.increment(card.id)
            val wonCardIds = (card.id + 1..card.id + card.matchingNumbers.size)
            wonCardIds.forEach { id ->
                numberOfCopiesById.add(id, numberOfCopies)
            }
        }
    }

    val totalNumberOfCopies = numberOfCopiesById.values.sum()
}

data class ScratchCard(val id: Int, val winningNumbers: Set<Int>, val numbersHad: Set<Int>) {
    val matchingNumbers = numbersHad.intersect(winningNumbers)

    val points = with(matchingNumbers) {
        if (isEmpty()) 0 else 2f.pow(size - 1).toInt()
    }
}

fun <T> MutableMap<T, Int>.increment(key: T): Int {
    return this.merge(key, 1) { copies, _ -> copies + 1 }!!
}

fun <T> MutableMap<T, Int>.add(key: T, value: Int) {
    this[key] = this.getOrDefault(key, 0) + value
}