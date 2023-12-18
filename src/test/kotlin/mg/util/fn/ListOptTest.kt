package mg.util.fn

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ListOptTest {

    @Test
    fun testUseList() {
        val a = listOf(4, 3, 2, 1)
        val b = listOf(1, 2, 3, 4)
        val expected = listOf(1, 2, 3, 4)

        ListOpt(a)
                .use(b)
                .apply {
                    assertEquals(expected, value())
                }
    }

    @Test
    fun testSublist() {
        val a = listOf(4, 3, 2, 1)
        val expected = listOf(4, 3)

        ListOpt(a)
                .subList(0, 2)
                .apply {
                    assertEquals(expected, value())
                }
    }

    @Test
    fun testSome() {
        val a = listOf(4, 3, 2, 1)
        var candidate = 0

        ListOpt(a)
                .some { candidate = it.sum() }
                .apply { assertEquals(10, candidate) }

        ListOpt(a)
                .apply { assertTrue(some()) }

        ListOpt(emptyList())
                .apply { assertFalse(some()) }
    }

    @Test
    fun testNone() {
        val a = listOf(4, 3, 2, 1)

        ListOpt(a)
                .apply { assertTrue(some()) }
                .use(emptyList())
                .apply { assertTrue(none()) }
    }

    @Test
    fun testFirstAndLast() {

        val a = listOf(4, 3, 2, 1)

        ListOpt(a)
                .apply { assertEquals(4, first().get()) }

        ListOpt(a)
                .apply { assertEquals(1, last().get()) }

        ListOpt<String>()
                .apply { assertNull(first().get()) }

        ListOpt<Int>()
                .apply { assertNull(last().get()) }
    }

    @Test
    fun testToList() {
        val a = listOf(4, 3, 2, 1)

        ListOpt(a)
                .toList()
                ?.reduce { acc, b -> acc + b }
                .apply {
                    assertEquals(10, this)
                }
    }

    @Test
    fun testFoldLeft() {
        val a = listOf(4, 3, 2, 1)
        val b = listOf(4, 3, 2, 1)
        var i1 = 0

        ListOpt(a)
                .foldLeft(0) { acc, v ->
                    assertEquals(b[i1], v)
                    i1 += 1
                    acc + v
                }
                .apply {
                    assertEquals(10, get())
                }
    }

    @Test
    fun testFoldRight() {
        val a = listOf(4, 3, 2, 1)
        val b = listOf(4, 3, 2, 1)
        var i1 = 3

        ListOpt(a)
                .foldRight(0) { v, acc ->
                    assertEquals(b[i1], v)
                    i1 -= 1
                    acc + v
                }
                .apply {
                    assertEquals(10, get())
                }
    }

    @Test
    fun testForEach() {
        val a = listOf("1", "2", "3", "4", "5")
        val expected = "12345"
        var candidate = ""

        ListOpt(a)
                .forEach { candidate += it }

        assertEquals(expected, candidate)
    }

    @Test
    fun testFilter() {
        val a = listOf(1, 2, 3, 4, 5)
        val expected = listOf(2, 4)

        ListOpt(a)
                .filter { it % 2 == 0 }
                .apply {
                    assertEquals(expected, value())
                }
    }

    @Test
    fun testMap() {
        val expected = listOf(2, 4, 6, 8, 10)
        (1..5).toList()
                .toListOpt()
                .map { it * 2 }
                .apply {
                    assertEquals(expected, value())
                }
    }

    @Test
    fun testFlatMap() {
        val str = "1,2,3,4,5"
        val expected = listOf("1", "2", "3", "4", "5")
        Opt.of(str)
                .toListOpt { listOf(it) }
                .flatMap { it.split(",") }
                .apply {
                    assertEquals(expected, value())
                }

        str.toListOpt()
                .flatMap { it.split(",") }
                .apply {
                    assertEquals(expected, value())
                }
    }

    @Test
    fun testTakeWith2() {
        val strings = listOf("1", "2", "3", "4", "5")
        val expected = listOf("1", "2")

        var i = 0
        strings.toListOpt()
                .take(2)
                .forEach {
                    if (i == 2) {
                        fail("expected two elements, got more.")
                    }
                    assertEquals(expected[i], it)
                    i++
                }
    }
}
