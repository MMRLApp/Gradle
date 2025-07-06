package com.dexcompiler.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DexCompilerPluginTest {
    
    @Test
    fun `plugin can be applied`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.dexcompiler.gradle")

        // Verify the plugin was applied
        assertTrue(project.plugins.hasPlugin("com.dexcompiler.gradle"))
    }
    
    @Test
    fun `plugin registers extension`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.dexcompiler.gradle")
        
        val extension = project.extensions.findByName("dexCompiler")
        assertNotNull(extension)
    }
    
    @Test
    fun `plugin registers task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.dexcompiler.gradle")
        
        val task = project.tasks.findByName("compileDex")
        assertNotNull(task)
    }
}
