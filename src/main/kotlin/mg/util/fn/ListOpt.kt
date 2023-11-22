package mg.util.fn

class ListOpt<T : Any> {

    private var value: List<T>? = null

    constructor() {
        value = emptyList()
    }

    constructor(o: List<T>) {
        value = o
    }

    fun first(): Opt<T> = when {
        some() -> Opt.of(value?.first())
        else -> Opt.empty()
    }

    fun last(): Opt<T> = when {
        some() -> Opt.of(value?.last())
        else -> Opt.empty()
    }

    fun size() = value?.size ?: 0
    fun some(): Boolean = value?.isNotEmpty() ?: false
    fun none(): Boolean = value?.isEmpty() ?: true
    fun toList(): List<T>? = value
    fun get(index: Int): T? = value?.get(index)
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

    fun subList(fromIndex: Int, toIndex: Int): ListOpt<T> = ListOpt(value().subList(fromIndex, toIndex))

    fun use(o: List<T>): ListOpt<T> {
        value = o
        return this
    }

    fun forEach(block: (T) -> Unit): ListOpt<T> {
        value().forEach(block)
        return this
    }

    fun filter(predicate: (T) -> Boolean): ListOpt<T> = ListOpt(value?.filter(predicate) ?: emptyList())

    fun <R : Any> map(mapper: (T) -> R): ListOpt<R> {
        val mapped: List<R> = value().map(mapper)
        return ListOpt(mapped)
    }

    fun <R : Any> flatMap(mapper: (T) -> Iterable<R>): ListOpt<R> = ListOpt(value?.flatMap(mapper) ?: emptyList())
}

inline fun <reified T : Any> List<T>.toListOpt(): ListOpt<T> = ListOpt(this)
fun <T : Any> T.toListOpt(): ListOpt<T> = ListOpt(listOf(this))


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

//    private fun <R : Any> isValueClassSameAsRefClass(ref: R): Boolean =
//            value?.let { it::class == ref::class } ?: false

//    inline fun <reified V : Any, R : Any> map(type: Iterator<*>, mapper: (V) -> R): Opt<List<R>> {
//        val list = mutableListOf<R>()
//        for (element in type) if (element is V) list += mapper(element)
//        return of(list)
//    }



