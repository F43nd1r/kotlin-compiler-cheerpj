package com.faendir

import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

@Suppress("unused")
object KotlinCompiler {
    private val scriptCompilationConfiguration = ScriptCompilationConfiguration {
        compilerOptions("-Xuse-fast-jar-file-system=false")
    }

    private val scriptEvaluationConfiguration = ScriptEvaluationConfiguration()

    fun eval(eval: String): Any? {
        val host = BasicJvmScriptingHost()
        return when (val result = host.eval(eval.toScriptSource(), scriptCompilationConfiguration, scriptEvaluationConfiguration)) {
            is ResultWithDiagnostics.Success -> when(val returnValue = result.value.returnValue)  {
                is ResultValue.Value -> returnValue.value
                is ResultValue.Error -> throw returnValue.error
                ResultValue.NotEvaluated -> throw RuntimeException("Not evaluated")
                is ResultValue.Unit -> Unit
            }
            is ResultWithDiagnostics.Failure -> {
                result.reports.filter { it.severity > ScriptDiagnostic.Severity.DEBUG }.forEach { diagnostic ->
                    println("--- Diagnostic ---")
                    println("Code: ${diagnostic.code}")
                    println("Message: ${diagnostic.message}")
                    println("Severity: ${diagnostic.severity}")
                    println("SourcePath: ${diagnostic.sourcePath}")
                    println("Location: ${diagnostic.location}")
                    println("Exception: ${diagnostic.exception?.stackTraceToString()}")
                }
                throw RuntimeException()
            }
        }
    }
}