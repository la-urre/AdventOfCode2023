fun solveDay1() {
    inputFile("day1_input").useLines { lines ->
        println(lines.map { extractNumericalCalibrationValue(it) }.sum())
    }
}

fun solveDay1Part2() {
    inputFile("day1_input").useLines { lines ->
        println(lines.map { extractCalibrationValue(it) }.sum())
    }
}

fun extractNumericalCalibrationValue(line: String): Int {
    return line.first { it.isDigit() }.digitToInt() * 10 + line.last { it.isDigit() }.digitToInt()
}

fun extractCalibrationValue(line: String): Int {
    // oneightwo does not work
    return line.replace("oneight", "oneeight")
        .replace("twone", "twoone")
        .replace("threeight", "threeeight")
        .replace("fiveight", "fiveeight")
        .replace("sevenine", "sevennine")
        .replace("eightwo", "eighttwo")
        .replace("eighthree", "eightthree")
        .replace("nineight", "nineeight")
        .replace("one", "1")
        .replace("two", "2")
        .replace("three", "3")
        .replace("four", "4")
        .replace("five", "5")
        .replace("six", "6")
        .replace("seven", "7")
        .replace("eight", "8")
        .replace("nine", "9")
        .let { extractNumericalCalibrationValue(it) }
}