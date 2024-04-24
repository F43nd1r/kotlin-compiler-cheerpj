package com.faendir

import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

object KotlinCompiler {
    private val scriptCompilationConfiguration = ScriptCompilationConfiguration {
        compilerOptions("-Xuse-fast-jar-file-system=false")
    }

    private val scriptEvaluationConfiguration = ScriptEvaluationConfiguration()

    fun eval(eval: String): Any {
        val host = BasicJvmScriptingHost()
        return when (val result = host.eval(eval.toScriptSource(), scriptCompilationConfiguration, scriptEvaluationConfiguration)) {
            is ResultWithDiagnostics.Success -> result.value
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