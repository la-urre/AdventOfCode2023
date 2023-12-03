fun solveDay2() {
    val bag = Bag(12, 13, 14)
    inputFile("day2_input").useLines { lines ->
        val games = lines.map { line -> parseGame(line) }.toList()
        val idSum = games.filter { game -> game.isPossibleWith(bag) }.sumOf { it.id }
        println(idSum)

        val powerSum = games.sumOf { it.minimalBag().power }
        println(powerSum)
    }
}

fun parseGame(line: String): Game {
    return with(line.split(":")) {
        Game(parseId(this.first()), parseSets(this[1].trim()))
    }
}

fun parseId(gameFragment: String) = gameFragment.substringAfter("Game ").toInt()

fun parseSets(setsString: String) = setsString.split("; ").map { parseSet(it) }

fun parseSet(setString: String) = Set(setString.split(", ").map { parseSubset(it) })

fun parseSubset(subsetString: String) = with(subsetString.split(" ")) {
    Subset(this.first().toInt(), Color.valueOf(this[1].uppercase()))
}

data class Game(val id: Int, val sets: List<Set>) {
    fun isPossibleWith(bag: Bag) = sets.all { it.isPossibleWith(bag) }

    fun minimalBag() = Bag(sets.maxOf { it.nbCubes(Color.RED) },
        sets.maxOf { it.nbCubes(Color.GREEN) },
        sets.maxOf { it.nbCubes(Color.BLUE) })
}

data class Set(val subsets: List<Subset>) {
    private val byColor = subsets.groupBy { it.color }.mapValues { it.value.sumOf { subset -> subset.count } }

    fun nbCubes(color: Color) = byColor[color] ?: 0

    fun isPossibleWith(bag: Bag) =
        nbCubes(Color.RED) <= bag.nbRed && nbCubes(Color.GREEN) <= bag.nbGreen && nbCubes(Color.BLUE) <= bag.nbBlue

}

data class Subset(val count: Int, val color: Color)

enum class Color {
    RED, GREEN, BLUE
}

data class Bag(val nbRed: Int, val nbGreen: Int, val nbBlue: Int) {
    val power = nbRed * nbGreen * nbBlue
}