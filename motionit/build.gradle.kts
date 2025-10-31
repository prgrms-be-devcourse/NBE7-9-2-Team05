plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    checkstyle
    jacoco
}


group = "com.back"
version = "0.0.1-SNAPSHOT"
description = "motionit"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-messaging")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    testImplementation("net.datafaker:datafaker:2.3.1")
    testImplementation("com.jayway.jsonpath:json-path")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0")

    // AWS
    implementation("software.amazon.awssdk:s3:2.27.21")
    implementation("software.amazon.awssdk:auth:2.27.21")
    implementation("software.amazon.awssdk:regions:2.27.21")
    implementation("software.amazon.awssdk:cloudfront:2.27.21")
    implementation("com.amazonaws:aws-java-sdk-cloudfront:1.12.782")

    // OpenAI (GPT)
    implementation("com.theokanning.openai-gpt3-java:service:0.18.2")
}


tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<org.gradle.api.plugins.quality.Checkstyle>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

checkstyle {
    toolVersion = "8.24"
    configFile = rootProject.file("config/checkstyle/naver-checkstyle-rules.xml")
    configProperties = mapOf(
        "suppressionFile" to rootProject
            .file("config/checkstyle/naver-checkstyle-suppressions.xml")
            .absolutePath
    )
}

jacoco {
    toolVersion = "0.8.12" // Java 21 호환
}

// ---------- Test 공통 설정: 모든 Test 태스크에 공통 로깅/리스너 ----------
tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
    }

    // 실패 케이스 수집 + 요약 + 파일로 저장
    val failed = mutableListOf<Triple<String, String, String?>>() // class, method, msg
    addTestListener(object : org.gradle.api.tasks.testing.TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(desc: TestDescriptor, result: TestResult) {
            if (result.resultType == TestResult.ResultType.FAILURE) {
                val clazz = desc.className ?: "(unknown-class)"
                val method = desc.name
                val msg = result.exception?.message?.lineSequence()?.firstOrNull()
                failed += Triple(clazz, method, msg)
            }
        }

        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                println(
                    """
                    ------------------------
                    ✅ TEST RESULT SUMMARY
                    Total tests : ${result.testCount}
                    Passed      : ${result.successfulTestCount}
                    Failed      : ${result.failedTestCount}
                    Skipped     : ${result.skippedTestCount}
                    ------------------------
                    """.trimIndent()
                )
                if (failed.isNotEmpty()) {
                    val RED = "\u001B[31m"
                    val RESET = "\u001B[0m"
                    println("❌ FAILED TESTS (${failed.size})")
                    failed.forEachIndexed { i, (c, m, msg) ->
                        println("${RED}${i + 1}. $c#$m${if (msg != null) "  —  $msg" else ""}${RESET}")
                    }
                    val out = layout.buildDirectory.file("reports/tests/failed-tests.txt").get().asFile
                    out.parentFile.mkdirs()
                    out.printWriter().use { pw ->
                        pw.println("FAILED TESTS (${failed.size})")
                        failed.forEach { (c, m, msg) ->
                            pw.println("$c#$m${if (msg != null) " — $msg" else ""}")
                        }
                        pw.println()
                        pw.println("Patterns for --tests:")
                        failed.forEach { (c, m, _) -> pw.println("--tests \"$c.$m\"") }
                    }
                    println("📄 Saved failed list -> ${out.absolutePath}")
                } else {
                    layout.buildDirectory.file("reports/tests/failed-tests.txt").get().asFile.apply {
                        parentFile.mkdirs(); writeText("No failures 🎉")
                    }
                }
            }
        }
    })
}

// ---------- 기본 test 태스크만: 태그 토글 + JaCoCo 리포트 연결 ----------
tasks.named<Test>("test") {
    // gradlew test → integration 제외 / -PincludeIntegration=true → 포함
    if (project.findProperty("includeIntegration") == "true") {
        systemProperty("junit.platform.tags.includes", "integration,unit")
    } else {
        systemProperty("junit.platform.tags.excludes", "integration")
    }
    finalizedBy(tasks.jacocoTestReport) // 기본 리포트만 연결
}

// ---------- fullTest: 통합+단위 전부 ----------
tasks.register<Test>("fullTest") {
    description = "Run unit + integration tests"
    group = "verification"

    val testSourceSet = sourceSets.named("test").get()
    testClassesDirs = testSourceSet.output.classesDirs
    classpath = testSourceSet.runtimeClasspath

    useJUnitPlatform()
    // fullTest는 항상 둘 다 포함 (전역 configureEach가 건드리지 않도록 위 `test`만 분기)
    systemProperty("junit.platform.tags.includes", "integration,unit")

    shouldRunAfter(tasks.named("test"))
    finalizedBy(tasks.named("jacocoFullTestReport"))
}

// ---------- JaCoCo 리포트들 ----------
jacoco {
    toolVersion = "0.8.12"
}

// 기본 test 리포트
tasks.jacocoTestReport {
    dependsOn(tasks.named("test"))
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
    }
    // (필요 시 exclude 규칙 유지)
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/*Application*",
                        "**/config/**",
                        "**/dto/**",
                        "**/exception/**",
                        "**/vo/**",
                        "**/Q*.*",
                        "**/*\$*Companion*.*"
                    )
                }
            }
        )
    )
}

// fullTest 리포트
tasks.register<JacocoReport>("jacocoFullTestReport") {
    dependsOn(tasks.named("fullTest"))

    // fullTest jacoco exec 파일 수집(안전한 방식)
    val fullExec = layout.buildDirectory.file("jacoco/fullTest.exec")
    executionData.setFrom(files(fullExec).asFileTree.matching { include("**/*.exec") })

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacocoFull/html"))
    }

    val main = sourceSets.named("main").get()
    sourceDirectories.setFrom(main.allSource.srcDirs)
    classDirectories.setFrom(
        files(
            main.output.classesDirs.files.map {
                fileTree(it) {
                    exclude(
                        "**/*Application*",
                        "**/config/**",
                        "**/dto/**",
                        "**/exception/**",
                        "**/vo/**",
                        "**/Q*.*",
                        "**/*\$*Companion*.*"
                    )
                }
            }
        )
    )
}
