# Responsive Training

This is an Android app for mobile and XR device that displays a list of videos and allows the user to play them. The application is built with Jetpack Compose using MVVM architecture.

## Features

*   Fetches a list of videos from a remote server.
*   Displays the videos in a list.
*   Allows the user to play a video by clicking on it.
*   Includes a 2D and a 3D UI.
*   Supports XR mode for an immersive experience.
*   Uses a 3D model of a football field in the XR mode.

## UI Modes

The application supports two different UI modes:

*   **Mobile:** A mobile experience with usual 2D UI.
*   **HomeSpace:** A mixed-reality experience where the 2D UI of Mobile mode is displayed as a floating panel within the user's physical environment.
*   **FullSpace:** A fully immersive virtual reality experience where the 3D UI is displayed, replacing the user's surroundings.

## Project Structure

The project follows a standard MVVM project structure. The main application code is located in the `app` module.

```
app
└── src
    └── main
        ├── java
        │   └── com
        │       └── example
        │           └── testproject
        │               ├── MainActivity.kt
        │               ├── model
        │               │   └── Video.kt
        │               ├── network
        │               │   └── APIService.kt
        │               │   └── VideoRepository.kt
        │               ├── ui
        │               │   └── theme
        │               │       ├── Color.kt
        │               │       ├── Theme.kt
        │               │       └── Type.kt
        │               ├── view
        │               │   ├── VideoListView.kt
        │               │   ├── NavGraph.kt
        │               │   └── VideoPlayerView.kt
        │               └── viewModel
        │                   └── VideoListViewModel.kt
        └── assets
            └── models
                └── football_field.glb
```
## Key Files

*   `MainActivity.kt`: The entry point of the application. It sets up the Jetpack Compose content and the navigation controller.
*   `NavGraph.kt`: This file defines the navigation graph of the application. It uses the `NavHost` composable to define the different screens and the navigation between them. It also handles the logic for switching between the 2D and 3D UI based on whether the XR mode is enabled.
*   `VideoListView.kt`: This file contains the composables for displaying the list of videos. It has two main composables: `VideoListView2D` for the 2D UI and `VideoListView3D` for the 3D UI. It also includes the `FootballField` composable that displays a 3D model of a football field in the 3D UI.
*   `VideoPlayerView.kt`: This file contains the composables for playing a video. It has two main composables: `VideoPlayer2D` for the 2D UI and `VideoPlayer3D` for the 3D UI. It uses a `VideoView` to play the video and provides custom controls for the 3D UI.
*   `VideoListViewModel.kt`: This is the ViewModel for the video list and player screens. It is responsible for fetching the video data from the `VideoRepository`, managing the state of the video player, and handling user interactions. It also contains the logic for switching between the different XR modes.

## Dependencies

The project uses the following dependencies:

*   **Jetpack Compose:** For building the UI.
*   **Coil:** For loading images.
*   **Retrofit:** For network requests.
*   **Gson:** For parsing JSON.
*   **Coroutines:** For asynchronous programming.
*   **OpenXR, ARCore, Filament, gltfio:** For the XR experience.
*   **Compose Navigation:** For navigating between screens.

## How to Build and Run

1.  Open the project in Android Studio.
2.  Edit local.propoerties file or create it at the root of the project and specify your Android SDK path
3.  Build the project.
4.  Run the `app` module on an Android device or emulator.

If you want to try the XR experience you will need to set up an XR Virtual Device


