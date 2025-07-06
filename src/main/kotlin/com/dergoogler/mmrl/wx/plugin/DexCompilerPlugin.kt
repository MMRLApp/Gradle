package com.dergoogler.mmrl.wx.plugin

import com.dergoogler.mmrl.wx.plugin.extension.DexCompilerExtension
import com.dergoogler.mmrl.wx.plugin.task.CompileDexTask
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
                val inputDirs = extension.inputDirs.get()
                if (inputDirs.isEmpty()) {
                    // Auto-detect compilation outputs if no explicit input directories are set
                    val autoDetectedDirs = mutableSetOf<String>()
                    
                    // Check for Kotlin compilation output in current project
                    project.tasks.findByName("compileKotlin")?.let {
                        autoDetectedDirs.add("build/classes/kotlin/main")
                    }
                    // Check for Android Kotlin compilation output
                    project.tasks.findByName("compileDebugKotlin")?.let {
                        autoDetectedDirs.add("build/tmp/kotlin-classes/debug")
                    }
                    project.tasks.findByName("compileReleaseKotlin")?.let {
                        autoDetectedDirs.add("build/tmp/kotlin-classes/release")
                    }
                    
                    // Check for Java compilation output in current project
                    project.tasks.findByName("compileJava")?.let {
                        autoDetectedDirs.add("build/classes/java/main")
                    }
                    // Check for Android Java compilation output
                    project.tasks.findByName("compileDebugJavaWithJavac")?.let {
                        autoDetectedDirs.add("build/intermediates/javac/debug/classes")
                    }
                    project.tasks.findByName("compileReleaseJavaWithJavac")?.let {
                        autoDetectedDirs.add("build/intermediates/javac/release/classes")
                    }
                    
                    // If this is a root project, check subprojects for compilation outputs
                    if (project.subprojects.isNotEmpty()) {
                        project.subprojects.forEach { subproject ->
                            // Regular Kotlin/Java projects
                            subproject.tasks.findByName("compileKotlin")?.let {
                                autoDetectedDirs.add("${subproject.name}/build/classes/kotlin/main")
                            }
                            subproject.tasks.findByName("compileJava")?.let {
                                autoDetectedDirs.add("${subproject.name}/build/classes/java/main")
                            }
                            
                            // Android projects
                            subproject.tasks.findByName("compileDebugKotlin")?.let {
                                autoDetectedDirs.add("${subproject.name}/build/tmp/kotlin-classes/debug")
                            }
                            subproject.tasks.findByName("compileReleaseKotlin")?.let {
                                autoDetectedDirs.add("${subproject.name}/build/tmp/kotlin-classes/release")
                            }
                            subproject.tasks.findByName("compileDebugJavaWithJavac")?.let {
                                autoDetectedDirs.add("${subproject.name}/build/intermediates/javac/debug/classes")
                            }
                            subproject.tasks.findByName("compileReleaseJavaWithJavac")?.let {
                                autoDetectedDirs.add("${subproject.name}/build/intermediates/javac/release/classes")
                            }
                        }
                    }
                    
                    val finalDirs = autoDetectedDirs.ifEmpty { setOf("build/classes") }
                    project.logger.lifecycle("CompileDex: Auto-detected input directories: $finalDirs")
                    finalDirs
                } else {
                    project.logger.lifecycle("CompileDex: Using configured input directories: $inputDirs")
                    inputDirs
                }.map { dir ->
                    val fileTree = project.fileTree(dir) {
                        it.include("**/*.class")
                    }
                    project.logger.lifecycle("CompileDex: Checking directory: $dir (${fileTree.files.size} .class files found)")
                    fileTree
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
            
            // Make compileDex depend on compilation tasks in main project
            project.tasks.findByName("compileKotlin")?.let { compileKotlin ->
                compileDexTask.configure { it.dependsOn(compileKotlin) }
            }
            project.tasks.findByName("compileDebugKotlin")?.let { compileDebugKotlin ->
                compileDexTask.configure { it.dependsOn(compileDebugKotlin) }
            }
            
            project.tasks.findByName("compileJava")?.let { compileJava ->
                compileDexTask.configure { it.dependsOn(compileJava) }
            }
            project.tasks.findByName("compileDebugJavaWithJavac")?.let { compileDebugJava ->
                compileDexTask.configure { it.dependsOn(compileDebugJava) }
            }
            
            project.tasks.findByName("classes")?.let { classes ->
                compileDexTask.configure { it.dependsOn(classes) }
            }
            
            // Also depend on compilation tasks in subprojects
            project.subprojects.forEach { subproject ->
                // Regular projects
                subproject.tasks.findByName("compileKotlin")?.let { compileKotlin ->
                    compileDexTask.configure { it.dependsOn(compileKotlin) }
                }
                subproject.tasks.findByName("compileJava")?.let { compileJava ->
                    compileDexTask.configure { it.dependsOn(compileJava) }
                }
                subproject.tasks.findByName("classes")?.let { classes ->
                    compileDexTask.configure { it.dependsOn(classes) }
                }
                
                // Android projects
                subproject.tasks.findByName("compileDebugKotlin")?.let { compileDebugKotlin ->
                    compileDexTask.configure { it.dependsOn(compileDebugKotlin) }
                }
                subproject.tasks.findByName("compileDebugJavaWithJavac")?.let { compileDebugJava ->
                    compileDexTask.configure { it.dependsOn(compileDebugJava) }
                }
            }
        }
        
        project.logger.info("Applied DEX Compiler plugin to project: ${project.name}")
    }
}
