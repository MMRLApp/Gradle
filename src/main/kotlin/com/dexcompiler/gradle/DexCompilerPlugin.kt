package com.dexcompiler.gradle

import com.dexcompiler.gradle.extension.DexCompilerExtension
import com.dexcompiler.gradle.task.CompileDexTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * Main plugin class for the DEX Compiler Gradle plugin.
 * 
 * This plugin provides functionality to compile Java/Kotlin class files
 * to Android DEX format using the Android build tools.
 */
class DexCompilerPlugin : Plugin<Project> {
    
    companion object {
        const val EXTENSION_NAME = "dexCompiler"
        const val TASK_NAME = "compileDex"
        const val TASK_GROUP = "dex compilation"
    }
    
    override fun apply(project: Project) {
        // Create the extension
        val extension = project.extensions.create(EXTENSION_NAME, DexCompilerExtension::class.java)
        
        // Register the compile DEX task
        project.tasks.register(TASK_NAME, CompileDexTask::class.java) { task ->
            task.group = TASK_GROUP
            task.description = "Compiles Java/Kotlin class files to Android DEX format"
            
            // Configure task properties from extension
            task.minSdkVersion.set(extension.minSdkVersion)
            task.debuggable.set(extension.debuggable)
            
            // Configure input files
            task.input.from(project.provider {
                extension.inputDirs.get().map { dir ->
                    project.fileTree(dir) {
                        it.include("**/*.class")
                    }
                }
            })
            
            // Configure output files
            task.outputFile.set(project.layout.projectDirectory.file(extension.outputFile))
            task.pluginClassFile.set(project.layout.projectDirectory.file(extension.pluginClassFile))
            
            // Make sure output directories exist
            task.doFirst {
                val outputDir = File(extension.outputFile.get()).parentFile
                outputDir.mkdirs()
                
                val pluginClassDir = File(extension.pluginClassFile.get()).parentFile
                pluginClassDir.mkdirs()
            }
        }
        
        // Add task dependencies
        project.afterEvaluate {
            val compileDexTask = project.tasks.named(TASK_NAME)
            
            // Make compileDex depend on compilation tasks
            project.tasks.findByName("compileKotlin")?.let { compileKotlin ->
                compileDexTask.configure { it.dependsOn(compileKotlin) }
            }
            
            project.tasks.findByName("compileJava")?.let { compileJava ->
                compileDexTask.configure { it.dependsOn(compileJava) }
            }
            
            project.tasks.findByName("classes")?.let { classes ->
                compileDexTask.configure { it.dependsOn(classes) }
            }
        }
        
        project.logger.info("Applied DEX Compiler plugin to project: ${project.name}")
    }
}
