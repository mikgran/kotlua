package mg.pp

import mg.util.fn.toListOpt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SequenceTest {

    @Test
    fun testStructure1() {

        val sequence = Sequence()

        val capture = "{ -> }"
        val expected = listOf("{", "->", "}")

        capture.toListOpt()
                .flatMap { it.split(" ") }
                .forEach { sequence + it }
                .apply {
                    assertEquals(expected, value())
                }
    }

}