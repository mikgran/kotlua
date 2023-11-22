package mg.pp

class Sequence {

    private val elements = mutableListOf<String>()

    fun add(element: String) = elements.add(element)

    fun get(index: Int) = elements[index]

    operator fun iterator() = elements.iterator()
}

operator fun Sequence.plus(element: String): Sequence {
    this.add(element)
    return this
}

operator fun Sequence.plusAssign(element: String) {
    add(element)
}

operator fun Sequence.get(index: Int): String = get(index)



