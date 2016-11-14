Cordova VR Player
======

A simple cordova plugin playing a video in a native [Google VR View](https://developers.google.com/vr/concepts/vrview) for Android and iOS. It supports both mono and stereo videos through compatibility with Google Cardboard.

##Usage
-----

####**GoogleVRPlayer.playVideo(videoUrl, fallbackVideoUrl)**

Opens a view and starts playing video available under `videoUrl` parameter. The video is played in full screen mode by default. When user exits the full screen mode, the view automatically closes. Some older devices cannot decode video larger than 1080p (1920x1080). In case the video fails to play the plugin will attempt to play the video available under `fallbackVideoUrl` parameter.