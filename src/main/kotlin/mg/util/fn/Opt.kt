package mg.util.fn

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

class Opt<T : Any> {

    private var value: T? = null

    constructor()

    constructor(t: T?) {
        value = t
    }

    fun some(): Boolean = value != null
    fun none(): Boolean = value == null
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

    fun getOrThrow(exceptionProducer: () -> Throwable): T? = when {
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

    fun <R : Any> toListOpt(listMaker: (T) -> List<R>): ListOpt<R> = ListOpt(listMaker(value()))

    inline fun <reified V : Any> toListOpt(): ListOpt<V> {
        when (val value = get()) {
            is List<*> -> value.filterIsInstance<V>()
            is V -> listOf(value)
            else -> emptyList()
        }.apply {
            return ListOpt(this)
        }
    }

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

