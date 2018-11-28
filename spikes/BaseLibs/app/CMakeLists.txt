# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

set(CPP_PATH ${PROJECT_SOURCE_DIR}/src/main/cpp)
set(EIGEN ${CPP_PATH}/Eigen)
set(OPENCV ${CPP_PATH}/OpenCV-sdk)
set(G2O ${CPP_PATH}/Thirdparty/g2o)
set(DBoW2 ${CPP_PATH}/Thirdparty/DBoW2)

cmake_minimum_required(VERSION 3.4.1)


set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(${CPP_PATH})
include_directories(${OPENCV}/sdk/native/jni/include)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp
        )

add_library(
        lib_g2o SHARED
        #types
        ${G2O}/g2o/types/types_sba.h
        ${G2O}/g2o/types/types_six_dof_expmap.h
        ${G2O}/g2o/types/types_sba.cpp
        ${G2O}/g2o/types/types_six_dof_expmap.cpp
        ${G2O}/g2o/types/types_seven_dof_expmap.cpp
        ${G2O}/g2o/types/types_seven_dof_expmap.h
        ${G2O}/g2o/types/se3quat.h
        ${G2O}/g2o/types/se3_ops.h
        ${G2O}/g2o/types/se3_ops.hpp
        #core
        ${G2O}/g2o/core/base_edge.h
        ${G2O}/g2o/core/base_binary_edge.h
        ${G2O}/g2o/core/hyper_graph_action.cpp
        ${G2O}/g2o/core/base_binary_edge.hpp
        ${G2O}/g2o/core/hyper_graph_action.h
        ${G2O}/g2o/core/base_multi_edge.h
        ${G2O}/g2o/core/hyper_graph.cpp
        ${G2O}/g2o/core/base_multi_edge.hpp
        ${G2O}/g2o/core/hyper_graph.h
        ${G2O}/g2o/core/base_unary_edge.h
        ${G2O}/g2o/core/linear_solver.h
        ${G2O}/g2o/core/base_unary_edge.hpp
        ${G2O}/g2o/core/marginal_covariance_cholesky.cpp
        ${G2O}/g2o/core/base_vertex.h
        ${G2O}/g2o/core/marginal_covariance_cholesky.h
        ${G2O}/g2o/core/base_vertex.hpp
        ${G2O}/g2o/core/matrix_structure.cpp
        ${G2O}/g2o/core/batch_stats.cpp
        ${G2O}/g2o/core/matrix_structure.h
        ${G2O}/g2o/core/batch_stats.h
        ${G2O}/g2o/core/openmp_mutex.h
        ${G2O}/g2o/core/block_solver.h
        ${G2O}/g2o/core/block_solver.hpp
        ${G2O}/g2o/core/parameter.cpp
        ${G2O}/g2o/core/parameter.h
        ${G2O}/g2o/core/cache.cpp
        ${G2O}/g2o/core/cache.h
        ${G2O}/g2o/core/optimizable_graph.cpp
        ${G2O}/g2o/core/optimizable_graph.h
        ${G2O}/g2o/core/solver.cpp
        ${G2O}/g2o/core/solver.h
        ${G2O}/g2o/core/creators.h
        ${G2O}/g2o/core/optimization_algorithm_factory.cpp
        ${G2O}/g2o/core/estimate_propagator.cpp
        ${G2O}/g2o/core/optimization_algorithm_factory.h
        ${G2O}/g2o/core/estimate_propagator.h
        ${G2O}/g2o/core/factory.cpp
        ${G2O}/g2o/core/optimization_algorithm_property.h
        ${G2O}/g2o/core/factory.h
        ${G2O}/g2o/core/sparse_block_matrix.h
        ${G2O}/g2o/core/sparse_optimizer.cpp
        ${G2O}/g2o/core/sparse_block_matrix.hpp
        ${G2O}/g2o/core/sparse_optimizer.h
        ${G2O}/g2o/core/hyper_dijkstra.cpp
        ${G2O}/g2o/core/hyper_dijkstra.h
        ${G2O}/g2o/core/parameter_container.cpp
        ${G2O}/g2o/core/parameter_container.h
        ${G2O}/g2o/core/optimization_algorithm.cpp
        ${G2O}/g2o/core/optimization_algorithm.h
        ${G2O}/g2o/core/optimization_algorithm_with_hessian.cpp
        ${G2O}/g2o/core/optimization_algorithm_with_hessian.h
        ${G2O}/g2o/core/optimization_algorithm_levenberg.cpp
        ${G2O}/g2o/core/optimization_algorithm_levenberg.h
        ${G2O}/g2o/core/jacobian_workspace.cpp
        ${G2O}/g2o/core/jacobian_workspace.h
        ${G2O}/g2o/core/robust_kernel.cpp
        ${G2O}/g2o/core/robust_kernel.h
        ${G2O}/g2o/core/robust_kernel_factory.cpp
        ${G2O}/g2o/core/robust_kernel_factory.h
        ${G2O}/g2o/core/robust_kernel_impl.cpp
        ${G2O}/g2o/core/robust_kernel_impl.h
        #stuff
        ${G2O}/g2o/stuff/string_tools.h
        ${G2O}/g2o/stuff/color_macros.h
        ${G2O}/g2o/stuff/macros.h
        ${G2O}/g2o/stuff/timeutil.cpp
        ${G2O}/g2o/stuff/misc.h
        ${G2O}/g2o/stuff/timeutil.h
        ${G2O}/g2o/stuff/os_specific.c
        ${G2O}/g2o/stuff/os_specific.h
        ${G2O}/g2o/stuff/string_tools.cpp
        ${G2O}/g2o/stuff/property.cpp
        ${G2O}/g2o/stuff/property.h
)

add_library( lib_DBoW2 SHARED
        ${DBoW2}/DBoW2/BowVector.h
        ${DBoW2}/DBoW2/FORB.h
        ${DBoW2}/DBoW2/FClass.h
        ${DBoW2}/DBoW2/FeatureVector.h
        ${DBoW2}/DBoW2/ScoringObject.h
        ${DBoW2}/DBoW2/TemplatedVocabulary.h
        ${DBoW2}/DBoW2/BowVector.cpp
        ${DBoW2}/DBoW2/FORB.cpp
        ${DBoW2}/DBoW2/FeatureVector.cpp
        ${DBoW2}/DBoW2/ScoringObject.cpp
        )

add_library( lib_opencv SHARED IMPORTED )

set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

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

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries(lib_g2o lib_opencv)
target_link_libraries(lib_DBoW2 lib_opencv)

target_link_libraries( native-lib
        ${log-lib}
        lib_g2o
        lib_DBoW2
        lib_opencv)