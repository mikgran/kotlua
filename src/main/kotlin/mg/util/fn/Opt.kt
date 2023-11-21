package mg.util.fn

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

open class Opt<T : Any> {

    protected var value: T? = null

    constructor()

    constructor(t: T?) {
        value = t
    }

    open fun some(): Boolean = value != null
    open fun none(): Boolean = value == null
    fun get() = value
    fun value(): T = value!!

    fun <R : Any> map(mapper: (T) -> R?): Opt<R> = when {
        some() -> of(mapper(value!!))
        else -> empty()
    }

    fun <R : Any> emap(extensionMapper: T.() -> R): Opt<R> = when {
        some() -> of(value!!.extensionMapper())
        else -> empty()
    }

    // if supplier.some() -> supply
    fun onSupplier(conditionalSupplier: (() -> T)?): Opt<T> = when {
        of(conditionalSupplier).some() -> Opt(conditionalSupplier?.invoke())
        else -> this
    }

    // if none() -> supply
    fun use(supplier: () -> T): Opt<T> = when {
        none() -> Opt(supplier())
        else -> this
    }

    // if some() -> consume
    fun some(consumer: (T) -> Unit): Opt<T> {
        if (some()) {
            consumer(value!!)
        }
        return this
    }

    // replace with new
    fun replace(supplier: () -> T): Opt<T> = Opt(supplier())

    // run chunk if none()
    fun none(block: () -> Unit): Opt<T> {
        if (none()) {
            block()
        }
        return this
    }

    fun someThrow(exceptionSupplier: () -> Throwable): Opt<T> {
        if (some()) {
            throw exceptionSupplier()
        }
        return this
    }

    fun noneThrow(exceptionSupplier: () -> Throwable): Opt<T> {
        if (none()) {
            throw exceptionSupplier()
        }
        return this
    }

    fun filter(predicate: (T) -> Boolean): Opt<T> = when {
        some() && predicate(value!!) -> this
        else -> empty()
    }

    fun filterNot(predicate: (T) -> Boolean): Opt<T> = when {
        some() && !predicate(value!!) -> this
        else -> empty()
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

    fun getOrElse(default: T): T = when {
        some() -> value!!
        else -> default
    }

    fun getOrElse(defaultProducer: () -> T) = when {
        some() -> value!!
        else -> defaultProducer()
    }

    fun <R : Any> getAndMap(mapper: (T) -> R): R? = when {
        some() -> mapper(value!!)
        else -> null
    }

    fun getOrElseThrow(exceptionProducer: () -> Throwable): T? = when {
        some() -> value
        else -> throw exceptionProducer()
    }

    fun <R : Any> with(r: Opt<R>, consumer: (T, R) -> Unit): Opt<T> {
        if (some() && r.some()) {
            consumer(value!!, r.value!!)
        }
        return this
    }

    override fun toString(): String = value?.toString() ?: ""

    override fun equals(other: Any?): Boolean {

        return when (other) {
            !is Opt<*> -> false
            else -> {
                val otherObj: Opt<*> = other
                value == otherObj.value
            }
        }
    }

    override fun hashCode(): Int {
        return Objects.hashCode(value)
    }

    fun <R : Any, V : Any> mapWith(r: R?, mapper: (T, R) -> V): Opt<V> {
        val rOpt = of(r)
        return when {
            some() && rOpt.some() -> of(mapper(value!!, rOpt.value!!))
            else -> empty()
        }
    }

    // TOIMPROVE: find a better way for arity of type N objects
    fun <R : Any, S : Any, V : Any> mapWith(r: R?, s: S?, mapper: (T, R, S) -> V): Opt<V> {
        val rOpt = of(r)
        val sOpt = of(s)
        return when {
            some() && rOpt.some() && sOpt.some() -> of(mapper(value!!, rOpt.value!!, sOpt.value!!))
            else -> empty()
        }
    }

    fun <V : Any> mapTo(toType: KClass<V>): Opt<V> = when {
        some() -> toType.safeCast(value!!).toOpt()
        else -> empty()
    }


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

    fun <V : Any> onTrue(conditionalMapper: (T) -> V): Opt<V> {
        return when {
            some() && true == (value as? Boolean) -> conditionalMapper(value!!).toOpt()
            else -> empty()
        }
    }

    fun <V : Any> onFalse(conditionalMapper: (T) -> V): Opt<V> {
        return when {
            some() && false == (value as? Boolean) -> conditionalMapper(value!!).toOpt()
            else -> empty()
        }
    }

    fun <V : Any> some(predicate: Boolean, conditionalMapper: (T) -> V): Opt<V> {
        return when {
            some() && predicate -> conditionalMapper(value!!).toOpt()
            else -> empty()
        }
    }

    inline fun <reified V : Any> mapWhen(predicate: Boolean, conditionalMapper: (T) -> V): Opt<V> {
        return when {
            some() && predicate -> conditionalMapper(value()).toOpt()
            some() && value() is V -> (get() as? V).toOpt()
            else -> empty()
        }
    }

    inline fun <reified V : Any> mapWhen(predicateFunction: (T) -> Boolean, conditionalMapper: ((T) -> V)): Opt<V> {
        return when {
            some() && predicateFunction(value()) -> conditionalMapper(value()).toOpt()
            some() && value() is V -> (get() as? V).toOpt()
            else -> empty()
        }
    }

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

    companion object Factory {

        @JvmStatic
        fun <T : Any> of(t: T?): Opt<T> = when (t) {
            null -> empty()
            else -> Opt(t)
        }

        @JvmStatic
        fun <T : Any> of(t: Opt<T>): Opt<T> = when {
            t.some() -> Opt(t.value!!)
            else -> empty()
        }

        @JvmStatic
        fun <T : Any> empty(): Opt<T> = Opt()
    }
}

fun <T : Any> T?.toOpt(): Opt<T> = Opt.of(this)

fun <T : Any> Boolean?.onTrue(conditionalMapper: Boolean.() -> T): Opt<T> =
        this.toOpt().onTrue(conditionalMapper)

fun <T : Any> Boolean?.onFalse(conditionalMapper: Boolean.() -> T): Opt<T> =
        this.toOpt().onFalse(conditionalMapper)

class Lopt<T : Any>(o: List<T>) : Opt<List<T>>(o) {

    fun forEach() {}

    fun filter() {}

    fun map() {}

    fun flatMap() {}

    fun first() {
        val list = listOf(1, 2, 3, 4)
    }

    fun last() {}

    fun foldLeft() {}

    fun foldRight() {}

    fun sublist() {}

    fun contains() {}

    fun containsAll() {}

    override fun some(): Boolean = false

    override fun none(): Boolean = false
}
