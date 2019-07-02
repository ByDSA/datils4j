# Datils4j: Dani's Core Libraries for Java

[![Latest release](https://img.shields.io/github/release/ByDSA/datils4j.svg)](https://github.com/ByDSA/datils4j/releases/latest)
[![Build Status](https://travis-ci.org/ByDSA/datils4j.svg?branch=master)](https://travis-ci.org/ByDSA/datils4j)

Datils4j is a set of core libraries that includes:

# Features
- Random Picker:
## Targets

La idea del Target es asignarle un valor de superficie  para que luego el PackTarget, compuesto de Target, lance un 'dart' a la superficie formada por todos los Target que contiene. El Target alcanzado por el 'dart' será el escogido aleatoriamente.

### Download
- Gradle:
Add Repository:
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add Dependency:
```gradle
dependencies {
  implementation 'com.github.ByDSA:datils4j:1.1'
}
```

- Maven:
Add Repository:
```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

Add Dependency:
```xml
<dependency>
	<groupId>com.github.ByDSA</groupId>
	<artifactId>datils4j</artifactId>
	<version>1.1</version>
</dependency>
```
