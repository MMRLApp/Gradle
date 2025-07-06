package com.dergoogler.mmrl.wx.plugin.extension

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

/**
 * Extension for configuring the DEX compiler plugin.
 */
abstract class DexCompilerExtension {
    
    /**
     * Whether to detect plugin classes with annotations.
     * Default: false
     */
    abstract val detectPluginClasses: Property<Boolean>
    
    /**
     * The detected plugin class name (read-only, set by the task).
     */
    abstract val pluginClassName: Property<String>
    
    /**
     * Minimum SDK version for DEX compilation.
     * Default: 26
     */
    abstract val minSdkVersion: Property<Int>
    
    /**
     * Whether to enable debugging in DEX compilation.
     * Default: true
     */
    abstract val debuggable: Property<Boolean>
    
    /**
     * Input directories or files containing class files to compile.
     */
    abstract val inputDirs: SetProperty<String>
    
    /**
     * Output file for the compiled DEX.
     * Default: build/outputs/dex/classes.dex
     */
    abstract val outputFile: Property<String>
    
    /**
     * Output file for writing the detected plugin class name.
     * Default: build/outputs/dex/plugin-class.txt
     */
    abstract val pluginClassFile: Property<String>
    
    init {
        // Set default values
        detectPluginClasses.convention(false)
        minSdkVersion.convention(26)
        debuggable.convention(true)
        inputDirs.convention(setOf("build/classes"))
        outputFile.convention("build/outputs/dex/classes.dex")
        pluginClassFile.convention("build/outputs/dex/plugin-class.txt")
    }
}
