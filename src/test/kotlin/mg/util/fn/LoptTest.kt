package mg.util.fn

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.streams.toList

class LoptTest {

    @Test
    fun useList() {


    }

    @Test
    fun forEach() {
    }

    @Test
    fun filter() {
    }

    @Test
    fun map() {
        val apply = Opt.of("1,2,3,4,5")
                .tol { listOf(it) }
                .flatMap { it.split(",") }
                .onEach { println("value: $it") }

        assertEquals(true, true)
    }

    @Test
    fun flatMap() {
    }

    @Test
    fun first() {
    }

    @Test
    fun last() {
    }

    @Test
    fun foldLeft() {
    }

    @Test
    fun foldRight() {
    }

    @Test
    fun sublist() {
    }

    @Test
    fun contains() {
    }

    @Test
    fun containsAll() {
    }

    @Test
    fun some() {
    }

    @Test
    fun none() {
    }
}