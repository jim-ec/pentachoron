cmake_minimum_required(VERSION 3.4.1)

# Compile with C++17:
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17")

# Add fmath include directy.
# Since fmath is header-only, no library is necessary to be built.
set(FMATH_VERSION 1.0.1)
include_directories(src/main/cpp/fmath-${FMATH_VERSION})

# Add Android libraries:
find_library(LOG_LIB log)
find_library(GL_LIB GLESv2)

# Build library:
add_library(native-lib SHARED src/main/cpp/renderer.cpp)
target_link_libraries(native-lib ${GL_LIB} ${LOG_LIB})
