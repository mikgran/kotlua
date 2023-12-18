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

    fun take(numberOfElements: Int): ListOpt<T> = when {
        some() -> value().take(numberOfElements).toListOpt()
        else -> ListOpt()
    }

    fun contains(element: T): Boolean = when {
        some() -> value().contains(element)
        else -> false
    }

    fun containsAll(elements: List<T>): Boolean = when {
        some() -> value().containsAll(elements)
        else -> false
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
        if (some()) {
            value().forEach(block)
        }
        return this
    }

    fun filter(predicate: (T) -> Boolean): ListOpt<T> = ListOpt(value?.filter(predicate) ?: emptyList())

    fun <R : Any> map(mapper: (T) -> R): ListOpt<R> {
        val mapped: List<R> = value().map(mapper)
        return ListOpt(mapped)
    }

    fun <R : Any> flatMap(mapper: (T) -> Iterable<R>): ListOpt<R> = ListOpt(value?.flatMap(mapper) ?: emptyList())

    fun <V : Any> match(predicate: (T) -> Boolean, mapper: (T) -> V): ListOpt<V> {
        return when {
            some() -> value
                    ?.filter { predicate(it) }
                    ?.map(mapper)
                    ?.toListOpt() ?: ListOpt()

            else -> ListOpt()
        }
    }

    inline fun <reified V : Any, R : Any> map(type: Iterator<*>, mapper: (V) -> R): ListOpt<R> {
        val list = mutableListOf<R>()
        for (element in type) if (element is V) list += mapper(element)
        return ListOpt(list)
    }
}

fun <T : Any> List<T>.toListOpt(): ListOpt<T> = ListOpt(this)
fun <T : Any> T.toListOpt(): ListOpt<T> = ListOpt(listOf(this))



