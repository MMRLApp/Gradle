plugins {
    kotlin("jvm") version "1.9.21"
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.dergoogler.mmrl.wx.plugin"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    
    // Android Gradle Plugin dependencies
    implementation("com.android.tools.build:gradle:8.2.0")
    implementation("com.android.tools.build:gradle-api:8.2.0")
    
    // D8/R8 DEX compilation dependencies
    implementation("com.android.tools:r8:8.2.42")
    implementation("com.android.tools.build:builder:8.2.0")
    
    // Additional Android build dependencies
    implementation("com.android.tools:common:31.2.0")
    implementation("com.android.tools:sdk-common:31.2.0")
    
    // ASM for bytecode analysis
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-tree:9.6")
    
    // Guava for utilities
    implementation("com.google.guava:guava:32.1.3-jre")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("junit:junit:4.13.2")
}

gradlePlugin {
    plugins {
        create("dexCompilerPlugin") {
            id = "com.dergoogler.mmrl.wx.plugin"
            implementationClass = "com.dergoogler.mmrl.wx.plugin.DexCompilerPlugin"
            displayName = "DEX Compiler Plugin"
            description = "A Gradle plugin for compiling Java/Kotlin classes to Android DEX format"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            pom {
                name.set("DEX Compiler Gradle Plugin")
                description.set("A Gradle plugin for compiling Java/Kotlin classes to Android DEX format")
                url.set("https://github.com/your-username/dex-gradle")
                
                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.html")
                    }
                }
                
                developers {
                    developer {
                        id.set("developer")
                        name.set("Developer Name")
                        email.set("developer@example.com")
                    }
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
