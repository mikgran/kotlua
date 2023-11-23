package mg.pp

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParserTest {

    @Test
    fun testParseCapture1() {

        val capture = "{ -> }"
        val expected = listOf("{", "->", "}")
        val parser = Parser()
        parser["fun"] = capture

        assertEquals(expected, parser["fun"]?.toList())
    }

    @Test
    fun testParseCapture2() {

        val capture = "( ( Int, Int ) -> Int )"
        val expected = listOf("(", "(", "Int", "Int", ")", "->", "Int", ")")
        val parser = Parser()
        parser["fun"] = capture

        assertEquals(expected, parser["fun"]?.toList())
    }
}