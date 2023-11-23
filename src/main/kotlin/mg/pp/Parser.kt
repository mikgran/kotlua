package mg.pp

import mg.util.fn.toListOpt

class Parser {

    private var delimeter: String

    constructor() {
        // default delimeter is empty space " ".
        delimeter = " "
    }

    constructor(delim: String) {
        delimeter =
                when {
                    delim.isEmpty() -> " "
                    else -> delim
                }
    }

    private var dictionary = mutableMapOf<String, Sequence>()

    private fun parse(string: String): Sequence {
        val sequence = Sequence()
        string.toListOpt()
                .map { it.replace(",", "") }
                .flatMap { it.split(delimeter) }
                .forEach { sequence + it }
        return sequence
    }

    fun put(key: String, capture: String) {
        dictionary[key] = parse(capture)
    }

    fun get(key: String): Sequence? {
        return dictionary[key]
    }
}

operator fun Parser.set(key: String, capture: String) = put(key, capture)
operator fun Parser.get(key: String): Sequence? = get(key)

