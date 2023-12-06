fun solveDay6() {
    inputFile("day6_input").useLines { lines ->
        val input = lines.toList()
        val competition = parseCompetitionAsDifferentRaces(input)
        println(competition.waysToBeatRecordProduct)
        val newCompetition = parseCompetitionAsSingleRace(input)
        println(newCompetition.waysToBeatRecordProduct)
    }
}

private fun parseCompetitionAsDifferentRaces(lines: List<String>): Competition {
    val times = lines.first().split("Time: ")[1].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toList()
    val distances =
        lines.last().split("Distance: ")[1].split(" ").filter { it.isNotEmpty() }.map { it.toLong() }.toList()
    val races = times.zip(distances).map { Race(it.first, it.second) }
    return Competition(races)
}

fun parseCompetitionAsSingleRace(lines: List<String>): Competition {
    val time = lines.first().split("Time: ")[1].split(" ").filter { it.isNotEmpty() }.joinToString("").toInt()
    val distance = lines.last().split("Distance: ")[1].split(" ").filter { it.isNotEmpty() }.joinToString("").toLong()
    return Competition(listOf(Race(time, distance)))
}

data class Competition(val races: List<Race>) {
    val waysToBeatRecordProduct = races.map { race -> race.waysToBeatRecord }.reduce { acc, ways -> acc * ways }
}

data class Race(val time: Int, val recordDistance: Long) {
    val waysToBeatRecord = run {
        var ways : Long = 0
        for (timeHoldingButton in 1 until time) {
            val speed = timeHoldingButton
            val remainingTimeToTravel = time - timeHoldingButton
            val totalDistance = speed.toBigInteger().times(remainingTimeToTravel.toBigInteger())
            if (totalDistance > recordDistance.toBigInteger()) {
                ways++
            }
        }
        return@run ways
    }
}
