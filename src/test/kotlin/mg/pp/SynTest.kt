package mg.pp

import kastree.ast.Node
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SynTest {

    @Test
    fun testSimpleParsing() {
        val file: Node.File = Syn.parse(codeString)
        val expected = listOf("bar", "baz")

        file.decls.forEach {
            if (it is Node.Decl.Func) {
                assertTrue(expected.contains(it.name))
            } else {
                Assertions.fail("Expecting 'Node.Decl.Func' object.")
            }
        }
    }

    companion object {
        val codeString = """
            package foo
            
            fun bar() {
                // Print hello
                println("Hello, World!")
            }
            fun baz() = println("Hello, again!")
        """.trimIndent()
    }
}
