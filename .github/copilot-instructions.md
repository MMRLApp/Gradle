<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# DEX Compiler Gradle Plugin Development Instructions

This is a Gradle plugin project written in Kotlin that provides functionality to compile Java/Kotlin class files to Android DEX format.

## Project Structure

- `src/main/kotlin/com/dexcompiler/gradle/` - Main plugin source code
  - `DexCompilerPlugin.kt` - Main plugin class
  - `extension/DexCompilerExtension.kt` - Configuration extension
  - `task/CompileDexTask.kt` - Main compilation task
- `build.gradle.kts` - Build configuration with all necessary dependencies
- `README.md` - Comprehensive documentation

## Key Technologies

- **Kotlin**: Primary development language
- **Gradle Plugin Development**: Using `gradle-plugin` plugin
- **Android Build Tools**: R8/D8 for DEX compilation
- **ASM**: Bytecode analysis for annotation detection

## Development Guidelines

1. **Plugin Structure**: Follow Gradle plugin conventions with proper extension and task registration
2. **Android Dependencies**: Use Android Gradle Plugin APIs for DEX compilation
3. **Task Configuration**: Leverage Gradle's lazy configuration APIs
4. **Error Handling**: Provide meaningful error messages for compilation failures
5. **Testing**: Add unit tests for plugin functionality
6. **Documentation**: Keep README.md updated with configuration examples

## Common Tasks

- `./gradlew build` - Build the plugin
- `./gradlew publishToMavenLocal` - Publish to local Maven repository for testing
- `./gradlew test` - Run tests
- `./gradlew check` - Run all checks including tests and code quality

## Dependencies

The plugin requires:
- Android Gradle Plugin for build tools
- R8 for DEX compilation
- ASM for bytecode analysis
- Guava for utilities

When making changes, ensure compatibility with the specified versions in `build.gradle.kts`.
