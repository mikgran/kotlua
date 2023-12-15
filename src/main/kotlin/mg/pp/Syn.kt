package mg.pp

import kastree.ast.Node
import kastree.ast.psi.Parser
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration

class Syn {

    companion object {

        fun parse(): Node.File {

            val parser = Parser()

            val compilerConfiguration = CompilerConfiguration()
            compilerConfiguration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false))
            val project = KotlinCoreEnvironment.createForProduction(
                    Disposer.newDisposable(),
                    compilerConfiguration,
                    EnvironmentConfigFiles.JVM_CONFIG_FILES
            ).project

            val field = parser::class.java.getDeclaredField("proj\$delegate")
            field.isAccessible = true
            field.set(parser, lazyOf(project))

            return parser.parseFile(codeString)
        }

        val codeString =
"""
    package foo

    fun bar() {
        // Print hello
        println("Hello, World!")
    }
    fun baz() = println("Hello, again!")
""".trimIndent()
    }

}

