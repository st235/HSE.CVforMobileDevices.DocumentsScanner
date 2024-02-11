<img src="/images/logo.png" width="72" height="72">

# DocLens

A simple but robust documents scanner.

| Feed                               | Corners Detection                                    | Document Example                                         |
|------------------------------------|------------------------------------------------------|----------------------------------------------------------|
| ![Feed](./images/doclens_feed.png) | ![Corners Detection](./images/doclens_cropper_2.png) | ![Example](./images/doclens_char_threshold_sample_2.jpg) |


## Technologies

The main technologies used in the project: 

- Kotlin + [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose): a robust technology to build UI
- [Open CV](https://opencv.org/): Computer Vision library

### OpenCV

Compiled OpenCV SDK can be found under [`dependencies/opencv` folder](./dependencies/opencv).

Please, do pay attention, that the compiled SDK is **patched**. 
In order, to make it work smoothly with the project, the source compatibility inside [`sdk/build.gradle`](./dependencies/opencv/sdk/build.gradle).

The changes are similar to the code below:

```groovy
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
```

## Usage

### Basic Flow

| Basic Flow Demo #1                                                                                                 | Basic Flow Demo #2                                                                                                 |
|--------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| [![Basic Flow Demo #1](https://img.youtube.com/vi/Iv4XK7NAkAg/0.jpg)](https://www.youtube.com/watch?v=Iv4XK7NAkAg) | [![Basic Flow Demo #1](https://img.youtube.com/vi/54KG_SmAA2Y/0.jpg)](https://www.youtube.com/watch?v=54KG_SmAA2Y) |


