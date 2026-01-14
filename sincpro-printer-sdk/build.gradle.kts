plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.sincpro.printer"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
}

dependencies {
    // Bixolon SDK - embebido en el AAR para que el consumidor solo necesite el AAR
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    
    // Coroutines - api para que el consumidor las tenga transitivamente
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.core:core-ktx:1.12.0")
}

tasks.register("buildSdk") {
    dependsOn("assembleRelease")
    doLast {
        val aar = file("build/outputs/aar/sincpro-printer-sdk-release.aar")
        val out = file("build/sdk")
        out.mkdirs()
        if (aar.exists()) {
            aar.copyTo(file("$out/sincpro-printer-sdk.aar"), overwrite = true)
            println("✅ SDK: ${out.absolutePath}/sincpro-printer-sdk.aar")
        }
    }
}

tasks.register("distributeSdk") {
    dependsOn("buildSdk")
    doLast {
        val sdk = file("build/sdk/sincpro-printer-sdk.aar")
        listOf("../android/libs", "../example-app/app/libs").forEach { target ->
            val dir = file(target)
            dir.mkdirs()
            dir.listFiles()?.filter { it.name.startsWith("sincpro-printer") }?.forEach { it.delete() }
            sdk.copyTo(file("$target/sincpro-printer-sdk.aar"), overwrite = true)
            println("✅ Copied: $target/sincpro-printer-sdk.aar")
        }
    }
}
