fun solveDay7() {
    inputFile("day7_input").useLines { lines ->
        val hands = lines.map { line -> parseHand(line) }.toList()
        val game = CamelGame(hands)
        println(game.totalWinnings(Rule.BASIC))
        println(game.totalWinnings(Rule.WITH_JOKER))
    }
}

private fun parseHand(line: String): Hand {
    val lineFragments = line.split(" ")
    val cards = lineFragments.first().map { parseCard(it) }.toList()
    val bid = lineFragments[1].toInt()
    return Hand(cards, bid)
}

private fun parseCard(character: Char): Card {
    return Card.values().find { it.character == character }!!
}

private data class CamelGame(val hands: List<Hand>) {
    fun totalWinnings(rule: Rule) =
        hands.sortedWith(rule.handComparator).mapIndexed { index, hand -> (index + 1) * hand.bid }.sum()
}

private data class Hand(val cards: List<Card>, val bid: Int)

private enum class Card(val character: Char) {
    TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'), TEN('T'), JACK('J'), QUEEN(
        'Q'
    ),
    KING('K'), ACE('A');
}

private enum class Type {
    HighCard, OnePair, TwoPairs, ThreeOfAKind, FullHouse, FourOfAKind, FiveOfAKind
}

private enum class Rule {
    BASIC {
        override fun valueOf(card: Card) = card.ordinal
        override fun sameCardCounts(hand: Hand) = hand.cards.groupingBy { it }.eachCount().values
    },
    WITH_JOKER {
        override fun valueOf(card: Card) = if (card == Card.JACK) 0 else card.ordinal + 1
        override fun sameCardCounts(hand: Hand): List<Int> {
            val nbJokers = hand.cards.count { it == Card.JACK }
            val groups = hand.cards.groupBy { it }.toMutableMap()
            if (nbJokers in 1..4) {
                groups.remove(Card.JACK)
            }
            val counts = groups.values.map { it.size }.sortedDescending().toMutableList()
            if (nbJokers in 1..4) {
                counts[0] += nbJokers
            }
            return counts
        }
    };

    val handComparator: Comparator<Hand> = Comparator { hand, otherHand ->
        val handComparison = typeOf(hand).compareTo(typeOf(otherHand))
        if (handComparison == 0) {
            hand.cards.zip(otherHand.cards).forEach { (card, otherCard) ->
                val cardComparison = valueOf(card).compareTo(valueOf(otherCard))
                if (cardComparison != 0) return@Comparator cardComparison
            }
        }
        return@Comparator handComparison
    }

    abstract fun valueOf(card: Card): Int

    abstract fun sameCardCounts(hand: Hand): Collection<Int>

    fun typeOf(hand: Hand): Type {
        val groups = sameCardCounts(hand)
        return when {
            groups.size <= 1 -> Type.FiveOfAKind
            groups.any { size -> size == 4 } -> Type.FourOfAKind
            groups.any { size -> size == 3 } && groups.any { size -> size == 2 } -> Type.FullHouse
            groups.any { size -> size == 3 } -> Type.ThreeOfAKind
            groups.count { size -> size == 2 } == 2 -> Type.TwoPairs
            groups.any { size -> size == 2 } -> Type.OnePair
            else -> Type.HighCard
        }
    }
}