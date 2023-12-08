fun solveDay8() {
    inputFile("day8_input").useLines { linesSeq ->
        val map = parseMap(linesSeq)
        println(map.numberOfSteps(from = { node -> node.name == "AAA" }, to = { node -> node.name == "ZZZ" }))
        println(map.numberOfSteps(from = { node -> node.name.endsWith('A') }, to = { node -> node.name.endsWith('Z') }))
    }
}

private fun parseMap(mapLines: Sequence<String>): Map {
    val lines = mapLines.iterator()
    val instructions = lines.next().map { Direction.values().find { direction -> direction.letter == it }!! }
    lines.next()
    val network = parseNetwork(lines)
    return Map(instructions, network)
}

private fun parseNetwork(networkLines: Iterator<String>): Network {
    val unconnectedNodes = networkLines.asSequence().map { line -> parseNode(line) }.toList()
    val connectedNodes = unconnectedNodes.map { ConnectedNode(it.name) }
    val connectedNodesByName = connectedNodes.associateBy { it.name }
    unconnectedNodes.forEach {
        val connectedNode = connectedNodesByName[it.name]!!
        connectedNode.nextNodeLeft = connectedNodesByName[it.nextNodeLeft]
        connectedNode.nextNodeRight = connectedNodesByName[it.nextNodeRight]
    }
    return Network(connectedNodes)
}

private fun parseNode(nodeString: String): UnconnectedNode {
    val nodeSplit = nodeString.split(" = ")
    val name = nodeSplit[0]
    val nextNodes = nodeSplit[1].substring(1, nodeSplit[1].length - 1).split(", ")
    return UnconnectedNode(name, nextNodes[0], nextNodes[1])
}

private data class Map(val instructions: List<Direction>, val network: Network) {
    fun numberOfSteps(from: (ConnectedNode) -> Boolean, to: (ConnectedNode) -> Boolean) =
        network.navigate(instructions, from, to)
}

private data class Network(val nodes: List<ConnectedNode>) {
    fun navigate(
        instructions: List<Direction>,
        startingPointPredicate: (ConnectedNode) -> Boolean,
        endingPointPredicate: (ConnectedNode) -> Boolean
    ): Long {
        val directions = instructions.loopingIterator()
        val currentNodes = nodes.filter(startingPointPredicate)
        return currentNodes.map {
            var nbSteps = 0L
            var currentNode = it
            while (!endingPointPredicate.invoke(currentNode)) {
                val nextDirection = directions.next()
                currentNode =
                    if (nextDirection == Direction.Left) currentNode.nextNodeLeft!! else currentNode.nextNodeRight!!
                nbSteps++
            }
            nbSteps
        }.reduce { acc, steps -> findLCM(acc, steps) }
    }
}

fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

private data class ConnectedNode(
    val name: String,
    var nextNodeLeft: ConnectedNode? = null,
    var nextNodeRight: ConnectedNode? = null
) {
    override fun toString(): String {
        return "'$name' = (${nextNodeLeft!!.name}, ${nextNodeRight!!.name})"
    }
}

private data class UnconnectedNode(val name: String, val nextNodeLeft: String, val nextNodeRight: String)

private enum class Direction(val letter: Char) {
    Left('L'), Right('R')
}

fun <T> List<T>.loopingIterator(): Iterator<T> = LoopingIterator(this)

class LoopingIterator<T>(private val list: List<T>) : Iterator<T> {
    var iterator = list.iterator()
    override fun hasNext() = true
    override fun next(): T {
        val result = iterator.next()
        if (!iterator.hasNext()) {
            iterator = list.iterator()
        }
        return result
    }
}