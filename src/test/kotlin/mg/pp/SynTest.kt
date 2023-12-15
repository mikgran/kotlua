package mg.pp

import kastree.ast.Node
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SynTest {

    @Test
    fun test1() {

        val file: Node.File = Syn.parse()

        file.decls.forEach {
            println(it)
        }

        assertTrue(true)
    }
}