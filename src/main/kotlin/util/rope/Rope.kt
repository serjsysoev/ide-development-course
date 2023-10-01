package util.rope


class Rope<Metrics> {
    internal val base: RopeNode<Metrics>

    private constructor(base: RopeNode<Metrics>) {
        this.base = base
    }

    constructor(sequence: CharSequence, metricsCalculator: MetricsCalculator<Metrics>) {
        base = if (sequence.length < SPLIT_LENGTH) {
            LeafNode(sequence, metricsCalculator)
        } else {
            sequence.chunked(SPLIT_LENGTH).map { LeafNode(it, metricsCalculator) }.let { merge(it) }
        }
    }

    /**
     * This method should only be used with lines of size 200
     */
    constructor(lineFlow: List<String>, metricsCalculator: MetricsCalculator<Metrics>) {
        base = lineFlow.map { LeafNode(it, metricsCalculator) }.let { merge(it) }
    }

    val length: Int // TODO: migrate to long
            get() = base.length

    operator fun get(index: Int): Char = base[index]

    fun slice(startIndex: Int, endIndex: Int): Rope<Metrics> {
        return Rope(base.slice(startIndex, endIndex))
    }

    override fun toString(): String {
        return base.joinToString().toString()
    }

    fun insert(index: Int, sequence: CharSequence): Rope<Metrics> {
        if (index == 0) {
            return prepend(sequence)
        }
        if (index == length) {
            return append(sequence)
        }
        val (left, right) = base.split(index)
        return Rope(
            concat(
                concat(left, LeafNode(sequence, base.metricsCalculator)),
                right
            )
        )
    }

    fun insert(index: Int, char: Char): Rope<Metrics> = insert(index, char.toString())

    fun prepend(sequence: CharSequence): Rope<Metrics> {
        val newNode = concat(LeafNode(sequence, base.metricsCalculator), base)
        return Rope(newNode)
    }

    fun append(sequence: CharSequence): Rope<Metrics> {
        val newNode = concat(base, LeafNode(sequence, base.metricsCalculator))
        return Rope(newNode)
    }

    fun delete(startIndex: Int, endIndex: Int): Rope<Metrics> {
        return Rope(base.delete(startIndex, endIndex))
    }

    companion object {
        const val SPLIT_LENGTH = 200
    }
}