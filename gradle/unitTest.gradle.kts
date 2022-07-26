val springVersion = "2.7.2"
val h2Version = "2.1.214"
val mockitKotlinVersion = "1.6.0"

dependencies {
    "testImplementation"(kotlin("test"))

    "testImplementation"("com.nhaarman:mockito-kotlin:$mockitKotlinVersion")    // provides helper functions needed for mockito to work in Kotlin
    "testImplementation"("org.springframework.boot:spring-boot-starter-test:$springVersion")
    "testRuntimeOnly"("com.h2database:h2:$h2Version")
}


tasks.withType<Test> {
    useJUnitPlatform()

    minHeapSize = "256m"
    maxHeapSize = "896m"
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 ?: 1
}