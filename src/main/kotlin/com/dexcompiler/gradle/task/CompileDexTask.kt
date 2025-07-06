package com.dexcompiler.gradle.task

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.errors.MessageReceiverImpl
import com.android.build.gradle.options.SyncOptions.ErrorFormatMode
import com.android.builder.dexing.ClassFileInputs
import com.android.builder.dexing.DexArchiveBuilder
import com.android.builder.dexing.DexParameters
import com.android.builder.dexing.r8.ClassFileProviderFactory
import com.dexcompiler.gradle.extension.DexCompilerExtension
import com.google.common.io.Closer
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors

abstract class CompileDexTask : DefaultTask() {

    @get:InputFiles
    @get:SkipWhenEmpty
    @get:IgnoreEmptyDirectories
    abstract val input: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:OutputFile
    abstract val pluginClassFile: RegularFileProperty

    @get:Input
    abstract val minSdkVersion: Property<Int>

    @get:Input
    abstract val debuggable: Property<Boolean>

    init {
        description = "Compiles Java/Kotlin class files to Android DEX format"
        group = "dex compilation"
        
        // Default values
        minSdkVersion.convention(24)
        debuggable.convention(true)
    }

    @Suppress("UnstableApiUsage")
    @TaskAction
    fun compileDex() {
        val android = project.extensions.findByName("android") as? BaseExtension
        val extension = project.extensions.getByType(DexCompilerExtension::class.java)
        
        val dexOutputDir = outputFile.get().asFile.parentFile
        
        // Ensure output directory exists
        dexOutputDir.mkdirs()

        Closer.create().use { closer ->
            val bootClasspath = android?.bootClasspath?.map(File::toPath) ?: emptyList()
            
            val dexBuilder = DexArchiveBuilder.createD8DexBuilder(
                DexParameters(
                    minSdkVersion = minSdkVersion.get(),
                    debuggable = debuggable.get(),
                    dexPerClass = false,
                    withDesugaring = true,
                    desugarBootclasspath = ClassFileProviderFactory(bootClasspath)
                        .also { closer.register(it) },
                    desugarClasspath = ClassFileProviderFactory(listOf<Path>()).also { closer.register(it) },
                    coreLibDesugarConfig = null,
                    coreLibDesugarOutputKeepRuleFile = null,
                    messageReceiver = MessageReceiverImpl(
                        ErrorFormatMode.HUMAN_READABLE,
                        LoggerFactory.getLogger(CompileDexTask::class.java)
                    )
                )
            )

            val fileStreams = input.map { inputFile -> 
                ClassFileInputs.fromPath(inputFile.toPath()).use { 
                    it.entries { _, _ -> true } 
                } 
            }.toTypedArray()

            Arrays.stream(fileStreams).flatMap { it }
                .use { classesInput ->
                    val files = classesInput.collect(Collectors.toList())

                    dexBuilder.convert(
                        files.stream(),
                        dexOutputDir.toPath()
                    )

                    // Process annotations if plugin detection is enabled
                    if (extension.detectPluginClasses.get()) {
                        processPluginAnnotations(files, extension)
                    }
                }
        }

        logger.lifecycle("Compiled ${input.files.size} class file(s) to DEX format: ${outputFile.get()}")
    }
}
