# DEX Compiler Gradle Plugin

## Features

- Compiles Java/Kotlin class files to Android DEX format using Android build tools
- Supports plugin annotation detection (configurable)
- Configurable minimum SDK version and debugging options
- Automatic dependency on compilation tasks
- Built-in support for desugaring and Android boot classpath

## Usage

### Apply the Plugin from JitPack

Add the plugin to your `build.gradle.kts`:

```kotlin
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.github.MMRLApp:Gradle:TAG")
    }
}

apply(plugin = "com.dexcompiler.gradle")
```

Or if using Maven Central:

```kotlin
plugins {
    id("com.dexcompiler.gradle") version "1.0.0"
}
```

### Configure the Plugin

```kotlin
dexCompiler {
    // Enable plugin annotation detection (default: false)
    detectPluginClasses.set(true)
    
    // Set minimum SDK version (default: 24)
    minSdkVersion.set(24)
    
    // Enable debugging (default: true)
    debuggable.set(true)
    
    // Set input directories (default: ["build/classes"])
    inputDirs.set(setOf("build/classes/kotlin/main", "build/classes/java/main"))
    
    // Set output file (default: "build/outputs/dex/classes.dex")
    outputFile.set("build/outputs/dex/my-classes.dex")
    
    // Set plugin class output file (default: "build/outputs/dex/plugin-class.txt")
    pluginClassFile.set("build/outputs/dex/detected-plugin.txt")
}
```

### Run the Task

```bash
./gradlew compileDex
```

## Requirements

- Java 11 or higher
- Gradle 7.0 or higher
- Android SDK (for DEX compilation tools)

## Configuration Options

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `detectPluginClasses` | `Boolean` | `false` | Enable plugin annotation detection |
| `minSdkVersion` | `Int` | `26` | Minimum Android SDK version |
| `debuggable` | `Boolean` | `true` | Enable debugging in DEX compilation |
| `inputDirs` | `Set<String>` | `["build/classes"]` | Input directories containing class files |
| `outputFile` | `String` | `"build/outputs/dex/classes.dex"` | Output DEX file path |
| `pluginClassFile` | `String` | `"build/outputs/dex/plugin-class.txt"` | Plugin class output file |

## Example Projects

### Basic Java/Kotlin Project (with JitPack)

```kotlin
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.github.MMRLApp:Gradle:TAG")
    }
}

apply(plugin = "com.dexcompiler.gradle")

dexCompiler {
    inputDirs.set(setOf("build/classes/kotlin/main"))
    outputFile.set("build/outputs/dex/classes.dex")
}
```

### Android Plugin Project (with JitPack)

```kotlin
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.github.MMRLApp:Gradle:TAG")
    }
}

apply(plugin = "com.dexcompiler.gradle")

dexCompiler {
    detectPluginClasses.set(true)
    minSdkVersion.set(26)
    inputDirs.set(setOf("build/classes/kotlin/main"))
    outputFile.set("build/outputs/dex/plugin.dex")
    pluginClassFile.set("build/outputs/dex/plugin-class.txt")
}
```

## Task Dependencies

The `compileDex` task automatically depends on:
- `compileKotlin` (if present)
- `compileJava` (if present)  
- `classes` (if present)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
