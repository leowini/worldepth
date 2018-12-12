# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

set(SLAM-SRC ${PROJECT_SOURCE_DIR}/src/main/cpp/SLAM-src)
set(DBoW2 ${PROJECT_SOURCE_DIR}/src/main/cpp/DBoW2)
set(G2O ${PROJECT_SOURCE_DIR}/src/main/cpp/g2o)

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

include_directories(${PROJECT_SOURCE_DIR}/src/main/cpp/opencv-include)
include_directories(${PROJECT_SOURCE_DIR}/src/main/cpp/SLAM-include)
include_directories(${DBoW2})
include_directories(${PROJECT_SOURCE_DIR}/src/main/cpp)
include_directories(${G2O})

add_library(lib_DBoW2
        SHARED

        ${DBoW2}/DBoW2/BowVector.cpp
        ${DBoW2}/DBoW2/FORB.cpp
        ${DBoW2}/DBoW2/FeatureVector.cpp
        ${DBoW2}/DBoW2/ScoringObject.cpp
        ${DBoW2}/DUtils/Random.cpp
        ${DBoW2}/DUtils/Timestamp.cpp)

add_library(lib_g2o
        SHARED

        ${G2O}/types/types_sba.cpp
        ${G2O}/types/types_six_dof_expmap.cpp
        ${G2O}/types/types_seven_dof_expmap.cpp
        #core
        ${G2O}/core/hyper_graph_action.cpp
        ${G2O}/core/hyper_graph.cpp
        ${G2O}/core/marginal_covariance_cholesky.cpp
        ${G2O}/core/matrix_structure.cpp
        ${G2O}/core/batch_stats.cpp
        ${G2O}/core/parameter.cpp
        ${G2O}/core/cache.cpp
        ${G2O}/core/optimizable_graph.cpp
        ${G2O}/core/solver.cpp
        ${G2O}/core/optimization_algorithm_factory.cpp
        ${G2O}/core/estimate_propagator.cpp
        ${G2O}/core/factory.cpp
        ${G2O}/core/sparse_optimizer.cpp
        ${G2O}/core/hyper_dijkstra.cpp
        ${G2O}/core/parameter_container.cpp
        ${G2O}/core/optimization_algorithm.cpp
        ${G2O}/core/optimization_algorithm_with_hessian.cpp
        ${G2O}/core/optimization_algorithm_levenberg.cpp
        ${G2O}/core/jacobian_workspace.cpp
        ${G2O}/core/robust_kernel.cpp
        ${G2O}/core/robust_kernel_factory.cpp
        ${G2O}/core/robust_kernel_impl.cpp
        #stuff
        ${G2O}/stuff/timeutil.cpp
        ${G2O}/stuff/os_specific.c
        ${G2O}/stuff/string_tools.cpp
        ${G2O}/stuff/property.cpp
        )

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp
        #SLAM
        ${SLAM-SRC}/Frame.cpp
        ${SLAM-SRC}/ORBExtractor.cpp
        ${SLAM-SRC}/fast_score.cpp
        ${SLAM-SRC}/TrackingInit.cpp
        ${SLAM-SRC}/Converter.cpp
        ${SLAM-SRC}/KeyFrame.cpp
        ${SLAM-SRC}/MapPoint.cpp
        ${SLAM-SRC}/Map.cpp
        ${SLAM-SRC}/ORBMatcher.cpp
        ${SLAM-SRC}/FrameList.cpp
        ${SLAM-SRC}/randomMap.cpp
        ${SLAM-SRC}/Tracking.cpp
        ${SLAM-SRC}/LocalMapping.cpp
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