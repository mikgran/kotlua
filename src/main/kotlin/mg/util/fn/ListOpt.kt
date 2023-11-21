package mg.util.fn

class ListOpt<T : Any> {

    private var value: List<T>? = null

    constructor()

    constructor(o: List<T>) {
        value = o
    }

    fun first(): Opt<T> = Opt.of(value?.first())
    fun last(): Opt<T> = Opt.of(value?.last())
    fun size() = value?.size ?: 0
    fun some(): Boolean = value?.isNotEmpty() ?: false
    fun none(): Boolean = value?.isEmpty() ?: true
    fun get(): List<T>? = value
    fun value(): List<T> = value!!

    fun contains(element: T): Boolean {
        if (some()) {
            return value().contains(element)
        }
        return false
    }

    fun containsAll(elements: List<T>): Boolean {
        if (some()) {
            return value().containsAll(elements)
        }
        return false
    }

    fun some(consumer: (List<T>) -> Unit): ListOpt<T> {
        if (some()) {
            consumer(value!!)
        }
        return this
    }

    fun <R : Any> foldLeft(initial: R, operation: (R, T) -> R): Opt<R> = Opt.of(value?.fold(initial, operation))
    fun <R : Any> foldRight(initial: R, operation: (T, R) -> R): Opt<R> = Opt.of(value?.foldRight(initial, operation))

    fun sublist() {}

    fun use(o: List<T>): ListOpt<T> {
        value = o
        return this
    }

    fun forEach(block: (T) -> Unit) = value().forEach(block)

    fun filter(predicate: (T) -> Boolean): ListOpt<T> = ListOpt(value().filter(predicate))

    fun <R : Any> map(mapper: (T) -> R): ListOpt<R> {
        val mapped: List<R> = value().map(mapper)
        return ListOpt(mapped)
    }

    fun <R : Any> flatMap(mapper: (T) -> Iterable<R>): List<R> = value!!.flatMap(mapper)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> T.toListOpt(): ListOpt<T> {
    return when (this) {
        is Opt<*> -> {
            when (val o = this.get()) {
                null -> ListOpt()
                else -> ListOpt(listOf(o as T))
            }
        }

        else -> ListOpt(listOf(this))
    }
}


//    fun <V : Any> match(predicate: Boolean, mapper: (T) -> V): BiOpt<T, V> =
//            this.match({ predicate }, mapper)

//    fun <V : Any> match(predicate: (T) -> Boolean, mapper: (T) -> V): BiOpt<T, V> {
//        return this.filter { isPresent() && predicate(value!!) }
//                .map(mapper)
//                .map { v -> BiOpt.of(value!!, v) }
//                .getOrElse(getBiOptOfValueAndEmpty())
//    }

//    @Suppress("UNCHECKED_CAST")
//    fun <R : Any, V : Any> match(
//            ref: R,
//            predicate: (R) -> Boolean,
//            mapper: (R) -> V,
//    ): BiOpt<T, V> {
//
//        // maps and filters only non null values of the same class.
//        // returns BiOpt.of(oldValue, newValue/null)
//        return this.filter { isPresent() && isValueClassSameAsRefClass(ref) }
//                .map { it as R }
//                .filter(predicate)
//                .map(mapper)
//                .map { v -> BiOpt.of(value!!, v) }
//                .getOrElse(getBiOptOfValueAndEmpty())
//    }

//    private fun <V : Any> getBiOptOfValueAndEmpty(): BiOpt<T, V> = BiOpt.of(of(value), empty())
//
//    fun <V : Any> case(
//            predicate: (T) -> Boolean,
//            mapper: (T) -> V,
//    ): BiOpt<T, V> {
//
//        return this.filter { isPresent() && predicate(value!!) }
//                .map(mapper)
//                .map { newRight -> BiOpt.of(value!!, newRight) }
//                .getOrElse(getBiOptOfValueAndEmpty())
//    }

//    private fun <R : Any> isValueClassSameAsRefClass(ref: R): Boolean =
//            value?.let { it::class == ref::class } ?: false

//    inline fun <reified V : Any, R : Any> map(type: Iterator<*>, mapper: (V) -> R): Opt<List<R>> {
//        val list = mutableListOf<R>()
//        for (element in type) if (element is V) list += mapper(element)
//        return of(list)
//    }

//    inline fun <reified V : Any, R : Any> lmap(mapper: (V) -> R): Opt<List<R>> {
//        return when (val type = get()) {
//            is List<*> -> map(type.iterator(), mapper)
//            is Iterator<*> -> map(type, mapper)
//            else -> empty()
//        }
//    }


//    /**
//     * A non transforming through-to-list-for-each (lxforEach). Performs side-effect
//     * on the contents, with no modification of the contents.
//     */
//    inline fun <reified V : Any> lxforEach(consumer: (V) -> Unit): Opt<List<V>> {
//        val list = toList<V>()
//        list.forEach(consumer)
//        return list.toOpt()
//    }

//    inline fun <reified V : Any, R : Any> lxmap(mapper: List<V>.() -> List<R>): Opt<List<R>> = of(toList<V>().mapper())
//    inline fun <reified V : Any> lfilter(predicate: (V) -> Boolean): Opt<List<V>> = of(toList<V>().filter(predicate))
//    inline fun <reified V : Any> toList(): List<V> {
//        return when (val value = get()) {
//            is List<*> -> value.filterIsInstance<V>()
//            is V -> listOf(value)
//            else -> emptyList()
//        }
//    }