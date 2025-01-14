val scalaLibraryVersion = "2.13.16"
val gatlingVersion = "3.13.1"


configurations {
    "perfTestImplementation" {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }
}


dependencies {
    "perfTestImplementation"("io.gatling.highcharts:gatling-charts-highcharts:$gatlingVersion")
    "perfTestImplementation"("io.gatling:gatling-core:$gatlingVersion")

    "perfTestCompileOnly"("org.scala-lang:scala-library:$scalaLibraryVersion")
}