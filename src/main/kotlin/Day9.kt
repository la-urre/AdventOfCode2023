fun solveDay9() {
    inputFile("day9_input").useLines { lines ->
        val sensorReadings = SensorReadings(lines.map { parseValueHistory(it) }.toList())
        println(sensorReadings.valuesHistory.sumOf { it.extrapolatedNextValue })
        println(sensorReadings.valuesHistory.sumOf { it.extrapolatedPreviousValue })
    }
}

private fun parseValueHistory(line: String): ValueHistory {
    return ValueHistory(line.split(" ").map { it.toInt() }.toList())
}

private data class SensorReadings(val valuesHistory: List<ValueHistory>) {
}

private data class ValueHistory(val values: List<Int>) {
    private val diffSequences = run {
        var currentSequence = values
        val allDiffSequences = mutableListOf<List<Int>>()
        while (!currentSequence.all { it == 0 }) {
            allDiffSequences.add(currentSequence)
            currentSequence = diffSequence(currentSequence)
        }
        allDiffSequences.toList()
    }
    val extrapolatedNextValue = diffSequences.reversed().fold(0) { acc, sequence -> acc + sequence.last() }
    val extrapolatedPreviousValue = diffSequences.reversed().fold(0) { acc, sequence -> sequence.first() - acc }

    private fun diffSequence(sequence: List<Int>) = sequence.zipWithNext().map { it.second - it.first }
}