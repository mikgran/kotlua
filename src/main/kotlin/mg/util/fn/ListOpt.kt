package mg.util.fn

class ListOpt<T : Any> {

    var value: List<T>? = null

    constructor()

    constructor(o: List<T>) {
        value = o
    }

    fun use(o: List<T>): ListOpt<T> {
        value = o
        return this
    }

    fun forEach(block: (T) -> Unit) {
        value!!.forEach(block)
    }

    fun filter(predicate: (T) -> Boolean): ListOpt<T> = ListOpt(value!!.filter(predicate))

    fun <R : Any> map(mapper: (T) -> R): ListOpt<R> {

        val mapped: List<R> = value!!.map(mapper)

        return ListOpt(mapped)
    }

    fun <R : Any> flatMap(mapper: (T) -> Iterable<R>): List<R> = value!!.flatMap(mapper)
//
//    fun first(): Opt<T> = of(value().first())
//
//    fun last(): Opt<T> = of(value().last())
//
//    fun <R : Any> foldLeft(initial: R, operation: (R, T) -> R): Opt<R> = of(value().fold(initial, operation))
//
//    fun <R : Any> foldRight(initial: R, operation: (T, R) -> R): Opt<R> = of(value().foldRight(initial, operation))
//
//    fun sublist() {}
//
//    fun contains() {}
//
//    fun containsAll() {}
//
//    override fun some(): Boolean = false
//
//    override fun none(): Boolean = false
}