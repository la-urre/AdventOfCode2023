import kotlin.Long.Companion.MAX_VALUE

fun solveDay5() {
    inputFile("day5_input").useLines { lines ->
        val almanac = parseAlmanac(lines)
        println(almanac.lowestLocationNumber)
        println(almanac.lowestLocationNumberWithRange)
    }
}

fun parseAlmanac(lines: Sequence<String>): Almanac {
    lateinit var seedNumbers: LinkedHashSet<Long>
    val seedToSoilConversions = mutableListOf<Conversion>()
    val soilToFertilizerConversions = mutableListOf<Conversion>()
    val fertilizerToWaterConversions = mutableListOf<Conversion>()
    val waterToLightConversions = mutableListOf<Conversion>()
    val lightToTemperatureConversions = mutableListOf<Conversion>()
    val temperatureToHumidityConversions = mutableListOf<Conversion>()
    val humidityToLocationConversions = mutableListOf<Conversion>()
    var activeConversionList: MutableList<Conversion> = seedToSoilConversions
    lines.forEach { line ->
        when {
            line.startsWith("seeds: ") -> {
                seedNumbers = LinkedHashSet(line.split(" ").toList().drop(1).map { it.toLong() })
            }

            line.startsWith("seed-to-soil map:") -> {
                activeConversionList = seedToSoilConversions
            }

            line.startsWith("soil-to-fertilizer map:") -> {
                activeConversionList = soilToFertilizerConversions
            }

            line.startsWith("fertilizer-to-water map:") -> {
                activeConversionList = fertilizerToWaterConversions
            }

            line.startsWith("water-to-light map:") -> {
                activeConversionList = waterToLightConversions
            }

            line.startsWith("light-to-temperature map:") -> {
                activeConversionList = lightToTemperatureConversions
            }

            line.startsWith("temperature-to-humidity map:") -> {
                activeConversionList = temperatureToHumidityConversions
            }

            line.startsWith("humidity-to-location map:") -> {
                activeConversionList = humidityToLocationConversions
            }

            line.isNotEmpty() -> {
                val conversionText = line.split(" ")
                activeConversionList.add(
                    Conversion(
                        conversionText[0].toLong(), conversionText[1].toLong(), conversionText[2].toLong()
                    )
                )
            }
        }
    }
    return Almanac(
        seedNumbers,
        ConversionMap(seedToSoilConversions),
        ConversionMap(soilToFertilizerConversions),
        ConversionMap(fertilizerToWaterConversions),
        ConversionMap(waterToLightConversions),
        ConversionMap(lightToTemperatureConversions),
        ConversionMap(temperatureToHumidityConversions),
        ConversionMap(humidityToLocationConversions)
    )
}

class Almanac(
    seedNumbers: LinkedHashSet<Long>,
    private val seedToSoilConversions: ConversionMap,
    private val soilToFertilizerConversions: ConversionMap,
    private val fertilizerToWaterConversions: ConversionMap,
    private val waterToLightConversions: ConversionMap,
    private val lightToTemperatureConversions: ConversionMap,
    private val temperatureToHumidityConversions: ConversionMap,
    private val humidityToLocationConversions: ConversionMap
) {

    val lowestLocationNumber: Long = lowestLocationNumber(seedNumbers.toList())
    private val seedNumbersAsRanges =
        seedNumbers.chunked(2).map { seedNumberPair -> (seedNumberPair[0] until seedNumberPair[0] + seedNumberPair[1]) }
            .sortedBy { it.first }
    val lowestLocationNumberWithRange = run {
        val locationsOrdered = humidityToLocationConversions.sortedByDestination()
        for (locationNumber in 0..locationsOrdered.last().destinationRange.last) {
            val potentialSeedNumber = convertLocationNumberToSeedNumber(locationNumber)
            val seedRange = seedNumbersAsRanges.find { range -> range.contains(potentialSeedNumber) }
            if (seedRange != null) {
                return@run locationNumber
            }
        }
    }

    private fun lowestLocationNumber(seedNumbers: Iterable<Long>): Long {
        var lowest = MAX_VALUE
        seedNumbers.forEach { seedNumber ->
            val locationNumber = convertSeedNumberToLocationNumber(seedNumber)
            if (locationNumber < lowest) {
                lowest = locationNumber
            }
        }
        return lowest
    }

    private fun convertSeedNumberToLocationNumber(seedNumber: Long): Long {
        val soilNumber = seedToSoilConversions.convert(seedNumber)
        val fertilizerNumber = soilToFertilizerConversions.convert(soilNumber)
        val waterNumber = fertilizerToWaterConversions.convert(fertilizerNumber)
        val lightNumber = waterToLightConversions.convert(waterNumber)
        val temperatureNumber = lightToTemperatureConversions.convert(lightNumber)
        val humidityNumber = temperatureToHumidityConversions.convert(temperatureNumber)
        return humidityToLocationConversions.convert(humidityNumber)
    }

    private fun convertLocationNumberToSeedNumber(locationNumber: Long): Long {
        val humidityNumber = humidityToLocationConversions.reverse(locationNumber)
        val temperatureNumber = temperatureToHumidityConversions.reverse(humidityNumber)
        val lightNumber = lightToTemperatureConversions.reverse(temperatureNumber)
        val waterNumber = waterToLightConversions.reverse(lightNumber)
        val fertilizerNumber = fertilizerToWaterConversions.reverse(waterNumber)
        val soilNumber = soilToFertilizerConversions.reverse(fertilizerNumber)
        return seedToSoilConversions.reverse(soilNumber)
    }
}

data class ConversionMap(val conversions: List<Conversion>) {
    fun convert(number: Long) = conversions.firstNotNullOfOrNull { it.convert(number) } ?: number
    fun reverse(number: Long) = conversions.firstNotNullOfOrNull { it.reverse(number) } ?: number
    fun sortedByDestination() = conversions.sortedBy { it.destinationRangeStart }
}

data class Conversion(val destinationRangeStart: Long, val sourceRangeStart: Long, val rangeLength: Long) {
    private val sourceRange = sourceRangeStart until sourceRangeStart + rangeLength
    val destinationRange = destinationRangeStart until destinationRangeStart + rangeLength
    fun convert(number: Long) = if (number in sourceRange) number - sourceRangeStart + destinationRangeStart else null

    fun reverse(number: Long) =
        if (number in destinationRange) number + sourceRangeStart - destinationRangeStart else null
}