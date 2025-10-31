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

/** -----------------------------
 *  JaCoCo (한 곳에서만 선언)
 *  ----------------------------- */
jacoco {
    toolVersion = "0.8.12" // Java 21 호환
}

/** 공통 커버리지 제외 패턴 */
val coverageExcludes = listOf(
    "**/*Application*",
    "**/config/**",
    "**/dto/**",
    "**/exception/**",
    "**/vo/**",
    "**/Q*.*",                 // Querydsl 생성물
    "**/*\$*Companion*.*"      // Kotlin 내부 생성물
)

/** -----------------------------
 *  Test 공통 설정 (로깅/요약/실패수집)
 *  ----------------------------- */
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
                val out = layout.buildDirectory.file("reports/tests/failed-tests.txt").get().asFile
                out.parentFile.mkdirs()

                if (failed.isNotEmpty()) {
                    val RED = "\u001B[31m"
                    val RESET = "\u001B[0m"
                    println("❌ FAILED TESTS (${failed.size})")
                    failed.forEachIndexed { i, (c, m, msg) ->
                        println("${RED}${i + 1}. $c#$m${if (msg != null) "  —  $msg" else ""}${RESET}")
                    }
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
                    out.writeText("No failures 🎉")
                }
            }
        }
    })
}

/** -----------------------------
 *  기본 test 태스크 (unit 기본, -PincludeIntegration=true 시 통합 포함)
 *  ----------------------------- */
tasks.named<Test>("test") {
    if (project.findProperty("includeIntegration") == "true") {
        systemProperty("junit.platform.tags.includes", "integration,unit")
    } else {
        systemProperty("junit.platform.tags.excludes", "integration")
    }
    finalizedBy(tasks.jacocoTestReport)
}

/** -----------------------------
 *  fullTest: 단위+통합 모두 실행 (CI에서 사용)
 *  ----------------------------- */
tasks.register<Test>("fullTest") {
    description = "Run unit + integration tests"
    group = "verification"

    val testSourceSet = sourceSets.named("test").get()
    testClassesDirs = testSourceSet.output.classesDirs
    classpath = testSourceSet.runtimeClasspath

    useJUnitPlatform()
    systemProperty("junit.platform.tags.includes", "integration,unit")

    shouldRunAfter(tasks.named("test"))
    finalizedBy(tasks.named("jacocoFullTestReport"))
}

/** -----------------------------
 *  JaCoCo 리포트 (test)
 *  ----------------------------- */
tasks.jacocoTestReport {
    dependsOn(tasks.named("test"))
    reports {
        xml.required.set(true)
        // PR 코멘트 액션에서 사용하기 쉬운 고정 경로
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/xml/jacocoTestReport.xml"))
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
    }
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) { exclude(coverageExcludes) }
            }
        )
    )
}

/** -----------------------------
 *  JaCoCo 리포트 (fullTest)
 *  ----------------------------- */
tasks.register<JacocoReport>("jacocoFullTestReport") {
    dependsOn(tasks.named("fullTest"))

    // fullTest의 실행 결과(Exec)를 태스크 참조로 안전하게 수집
    executionData(tasks.named("fullTest"))

    reports {
        xml.required.set(true)
        // PR 코멘트 액션에서 사용하기 쉬운 고정 경로
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacocoFull/xml/jacocoFullTestReport.xml"))
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacocoFull/html"))
    }

    val main = sourceSets.named("main").get()
    sourceDirectories.setFrom(main.allSource.srcDirs)
    classDirectories.setFrom(
        files(
            main.output.classesDirs.files.map {
                fileTree(it) { exclude(coverageExcludes) }
            }
        )
    )
}