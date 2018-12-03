# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

set(SLAM-SRC ${PROJECT_SOURCE_DIR}/src/main/cpp/SLAM-src)
set(DBoW2 ${PROJECT_SOURCE_DIR}/src/main/cpp/DBoW2)

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

include_directories(${PROJECT_SOURCE_DIR}/src/main/cpp/opencv-include)
include_directories(${PROJECT_SOURCE_DIR}/src/main/cpp/SLAM-include)
include_directories(${DBoW2})

add_library(lib_DBoW2
        SHARED

        ${DBoW2}/DBoW2/BowVector.cpp
        ${DBoW2}/DBoW2/FORB.cpp
        ${DBoW2}/DBoW2/FeatureVector.cpp
        ${DBoW2}/DBoW2/ScoringObject.cpp
        ${DBoW2}/DUtils/Random.cpp
        ${DBoW2}/DUtils/Timestamp.cpp)

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp
        ${SLAM-SRC}/Frame.cpp
        ${SLAM-SRC}/ORBExtractor.cpp
        ${SLAM-SRC}/fast_score.cpp
        ${SLAM-SRC}/TrackingInit.cpp
        ${SLAM-SRC}/Converter.cpp
        ${SLAM-SRC}/KeyFrame.cpp
        ${SLAM-SRC}/MapPoint.cpp
        ${SLAM-SRC}/Map.cpp
        ${SLAM-SRC}/ORBMatcher.cpp
        )

add_library( lib_opencv SHARED IMPORTED)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries(lib_DBoW2
        lib_opencv)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        lib_DBoW2
        lib_opencv)