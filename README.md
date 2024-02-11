<img src="/images/logo.png" width="72" height="72">

# DocLens

A simple but robust documents scanner.

| Feed                               | Corners Detection                                    | Document Preview                                     |
|------------------------------------|------------------------------------------------------|------------------------------------------------------|
| ![Feed](./images/doclens_feed.png) | ![Corners Detection](./images/doclens_cropper_2.png) | ![Example](./images/doclens_document_preview_2.jpg)  |


## Technologies

The application runs on **Android Lollipop and above**.

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

#### Gallery

| Basic Flow Demo #1                                                                                                 | Basic Flow Demo #2                                                                                                 |
|--------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| [![Basic Flow Demo #1](https://img.youtube.com/vi/Iv4XK7NAkAg/0.jpg)](https://www.youtube.com/watch?v=Iv4XK7NAkAg) | [![Basic Flow Demo #2](https://img.youtube.com/vi/54KG_SmAA2Y/0.jpg)](https://www.youtube.com/watch?v=54KG_SmAA2Y) |

| Documents Picker                                      | Documents Cropper                                           | Documents Editor                                          |
|-------------------------------------------------------|-------------------------------------------------------------|-----------------------------------------------------------|
| ![Documents Picker](./images/doclens_stitching_1.png) | ![Documents Cropper](./images/doclens_document_cropper.png) | ![Documents Editor](./images/doclens_document_editor.png) |

#### Camera

| Camera                                                                                                 |
|--------------------------------------------------------------------------------------------------------|
| [![Camera](https://img.youtube.com/vi/EQrTKNid59k/0.jpg)](https://www.youtube.com/watch?v=EQrTKNid59k) |

#### Landscape Scanning

| Landscape Scanning                                                                                                 |
|--------------------------------------------------------------------------------------------------------------------|
| [![Landscape Scanning](https://img.youtube.com/vi/_X7B6LMBlEE/0.jpg)](https://www.youtube.com/watch?v=_X7B6LMBlEE) |

### Scanning Video

| Straightforward video file                                                                                                 | Video with noisy frames                                                                                                 |
|----------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| [![Straightforward video file](https://img.youtube.com/vi/oSQlyA8RAWQ/0.jpg)](https://www.youtube.com/watch?v=oSQlyA8RAWQ) | [![Video with noisy frames](https://img.youtube.com/vi/9pOl3OzDWY4/0.jpg)](https://www.youtube.com/watch?v=9pOl3OzDWY4) |

### Image Stitching

| Stitching Documents                                                                                                 | Stitching Newspaper                                                                                                 |
|---------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| [![Stitching Documents](https://img.youtube.com/vi/sPIgiG93lWM/0.jpg)](https://www.youtube.com/watch?v=sPIgiG93lWM) | [![Stitching Newspaper](https://img.youtube.com/vi/FpQYinaYS9g/0.jpg)](https://www.youtube.com/watch?v=FpQYinaYS9g) |

## Features

### Editor

| Editor                                                                                                 |
|--------------------------------------------------------------------------------------------------------|
| [![Editor](https://img.youtube.com/vi/F-SkYZSlXCA/0.jpg)](https://www.youtube.com/watch?v=F-SkYZSlXCA) |

### Custom Algorithms

There are multiple algorithms implemented that helps to handle documents

#### Char Threshold

| Char Threshold option in the Editor                                                |
|------------------------------------------------------------------------------------|
| ![Char Threshold option in the Editor](./images/doclens_char_thershold_editor.png) |


Char Threshold examples

| Document 1                                                  | Document 2                                                  | Document 3                                                  |
|-------------------------------------------------------------|-------------------------------------------------------------|-------------------------------------------------------------|
| ![Document 1](./images/doclens_char_threshold_sample_1.jpg) | ![Document 2](./images/doclens_char_threshold_sample_2.jpg) | ![Document 3](./images/doclens_char_threshold_sample_3.jpg) |

#### Convex Hull Test

| Convex Hull Check                                                                                                 |
|-------------------------------------------------------------------------------------------------------------------|
| [![Convex Hull Check](https://img.youtube.com/vi/X9YnKRJSHio/0.jpg)](https://www.youtube.com/watch?v=X9YnKRJSHio) |


## Misc

### DocLens Youtube Playlist

Plyalist with all demo videos is available here: https://www.youtube.com/playlist?list=PLucKuGqiOAE_hX1PhURkPTRjVaTCUKh3_

Videos from the playlist are:

- **Basic Flow Demo #1**: https://youtube.com/shorts/Iv4XK7NAkAg
- **Basic Flow Demo #2**: https://youtube.com/shorts/54KG_SmAA2Y
- **Stitching newspaper**: https://youtu.be/FpQYinaYS9g
- **Stitching document**: https://youtu.be/sPIgiG93lWM
- **Short video**: https://youtube.com/shorts/oSQlyA8RAWQ
- **Sophisticated video with noise**: https://youtu.be/9pOl3OzDWY4
- **Images from camera**: https://youtube.com/shorts/EQrTKNid59k
- **Convex hull check**: https://youtube.com/shorts/X9YnKRJSHio
- **Landscape document**: https://youtube.com/shorts/_X7B6LMBlEE
- **Editor features**: https://youtube.com/shorts/F-SkYZSlXCA

### Evaluation Criteria List

> 0-5: Loading photos from a gallery and saving the results of transformation are supported



0-5: Loading videos is supported

0-9: Choice of the best video frame from a sequence of frames is supported

0-5: Manual selection of four corners for geometric transformation of scanned page is supported

0-10: Automatic selection of four corners for geometric transformation of scanned page is supported

0-15: At least 3 different algorithms are implemented for at least one of the following operations: binarization, image filtering, noise removal, contrast enhancement

0-5: Stitching of several photos of very-large document is implemented

0-16: Implementation of algorithms outside of OpenCV that take into account specifics of scanned documents, for example, development of the char_threshold segmentation, contrast enhancement for two-mode histogram, etc. The usage of such algorithms should be clearly stated in README

### More document examples

| Document 1                                    | Document 2                                    | Document 3                                    |
|-----------------------------------------------|-----------------------------------------------|-----------------------------------------------|
| ![Document 1](./images/document_example1.jpg) | ![Document 2](./images/document_example2.jpg) | ![Document 3](./images/document_example3.jpg) |

