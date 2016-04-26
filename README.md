# MapView

This a indoor map view named MapView for `Android`. It also offer some layers. If you are doing a indoor map application and try to do it.

## Layers

* MapLayer
    * rotate
    * scale
    * slide
* LocationLayer
    * Sensor
* BitmapLayer
* MarkLayer
* RouteLayer
    * ShortestPath By [FloydAlgorithm](https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm)
    * BestPath By [GeneticAlgorithm](https://en.wikipedia.org/wiki/Genetic_algorithm)， and you also look [here](https://github.com/onlylemi/GeneticTSP).

## Demo

I offer every layer demo and you can look the [demo](https://github.com/onlylemi/MapView/tree/master/demo) floder. And the following is a screenshot of demo.

![](https://raw.githubusercontent.com/onlylemi/notes/master/images/android_mapview_1.gif)
![](https://raw.githubusercontent.com/onlylemi/notes/master/images/android_mapview_2.gif)
![](https://raw.githubusercontent.com/onlylemi/notes/master/images/android_mapview_3.gif)

## Usage

### Gradle

```groovy
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
	
dependencies {
    compile 'com.github.onlylemi:mapview:v1.0'
}
```

### Maven

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.onlylemi</groupId>
    <artifactId>mapview</artifactId>
    <version>v1.0</version>
</dependency>
```

## About me

Welcome to pull [requests](https://github.com/onlylemi/GeneticTSP/pulls).  

If you have any new idea about this project, feel free to [contact me](mailto:onlylemi.com@gmail.com). :smiley: