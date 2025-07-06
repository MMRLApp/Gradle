package com.dexcompiler.gradle.task

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CompileDexTaskTest {
    
    @Test
    fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.dexcompiler.gradle")

        // Verify the task was registered
        assertTrue(project.tasks.findByName("compileDex") != null)
    }
    
    @Test
    fun `task has correct type`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.dexcompiler.gradle")
        
        val task = project.tasks.findByName("compileDex")
        assertNotNull(task)
        assertTrue(task is CompileDexTask)
    }
    
    @Test
    fun `extension is registered`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.dexcompiler.gradle")
        
        val extension = project.extensions.findByName("dexCompiler")
        assertNotNull(extension)
    }
}
