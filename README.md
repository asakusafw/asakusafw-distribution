# Asakusa Framework Distributions

This project provides a one-stop access of [Asakusa Framework](https://github.com/asakusafw/asakusafw) facilities which includes artifacts of the following repositories:

* [Asakusa Framework](https://github.com/asakusafw/asakusafw)
* [Asakusa on MapReduce](https://github.com/asakusafw/asakusafw-mapreduce)
* [Asakusa on Spark](https://github.com/asakusafw/asakusafw-spark)
* [Asakusa on M3BP](https://github.com/asakusafw/asakusafw-m3bp)

This includes *Asakusa Distribution Gradle Plug-in*. It is a facade of all Asakusa Gradle plug-ins that enables your Gradle build script to access easily to individual platform dependent plug-ins (e.g. Asakusa on Spark Gradle plug-in).

## How to build

### Gradle plug-ins

```sh
cd gradle
./gradlew clean [build] install
```

## How to use

### Gradle plug-ins

```gradle
# build.gradle on your project
buildscript {
    repositories {
        maven { url 'http://asakusafw.s3.amazonaws.com/maven/releases' }
    }
    dependencies {
        classpath group: 'com.asakusafw.gradle', name: 'asakusa-distribution', version: '<x.y.z>'
    }
}

apply plugin: 'asakusafw-sdk'
apply plugin: 'asakusafw-organizer'
apply plugin: 'asakusafw-m3bp'
apply plugin: 'asakusafw-spark'
apply plugin: 'eclipse'

...
```

## License
* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
