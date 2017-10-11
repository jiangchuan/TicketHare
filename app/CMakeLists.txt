# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.
include_directories(src/main/jni/include)
include_directories(src/main/jni)
aux_source_directory(src/main/jni SOURCE_FILES)
aux_source_directory(src/main/jni/src/core SOURCE_FILES_CORE)
aux_source_directory(src/main/jni/src/train SOURCE_FILES_TRAIN)
aux_source_directory(src/main/jni/src/util SOURCE_FILES_UTIL)
aux_source_directory(src/main/jni/thirdparty/LBP SOURCE_FILES_LBP)
aux_source_directory(src/main/jni/thirdparty/mser SOURCE_FILES_MSER)
aux_source_directory(src/main/jni/thirdparty/svm SOURCE_FILES_SVM)
aux_source_directory(src/main/jni/thirdparty/textDetect SOURCE_FILES_TEXTDETECT)
aux_source_directory(src/main/jni/thirdparty/xmlParser SOURCE_FILES_XML)
list(APPEND SOURCE_FILES ${SOURCE_FILES_CORE})
list(APPEND SOURCE_FILES ${SOURCE_FILES_SOURCE_FILES_SVM})
list(APPEND SOURCE_FILES ${SOURCE_FILES_CORE})
list(APPEND SOURCE_FILES ${SOURCE_FILES_TRAIN})
list(APPEND SOURCE_FILES ${SOURCE_FILES_UTIL})
list(APPEND SOURCE_FILES ${SOURCE_FILES_LBP})
list(APPEND SOURCE_FILES ${SOURCE_FILES_MSER})
list(APPEND SOURCE_FILES ${SOURCE_FILES_TEXTDETECT})
list(APPEND SOURCE_FILES ${SOURCE_FILES_XML})
set(OpenCV_DIR ../../OpenCV-android-sdk/sdk/native/jni)
#set(libs_DIR ../libs)
find_package(OpenCV REQUIRED)

add_library( # Sets the name of the library.
             mrcar
             # Sets the library as a shared library.
             SHARED
             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
              ${SOURCE_FILES})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       mrcar
                       ${OpenCV_LIBS}
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
