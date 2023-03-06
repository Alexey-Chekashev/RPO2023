cmake_minimum_required(VERSION 3.18.1)

project("fclient")

add_library(fclient SHARED /home/alexey/AndroidStudioProjects/fclient/app/src/main/cpp/native-lib.cpp)
add_library(spdlog STATIC IMPORTED)
add_library(mbedcrypto SHARED IMPORTED)
set_target_properties(spdlog PROPERTIES IMPORTED_LOCATION
        /home/alexey/AndroidStudioProjects/libs/spdlog/build/${ANDROID_ABI}/libspdlog.a)
set_target_properties(mbedcrypto PROPERTIES IMPORTED_LOCATION
        /home/alexey/AndroidStudioProjects/libs/mbedtls/build/${ANDROID_ABI}/library/libmbedcrypto.so)
target_include_directories(fclient PUBLIC
        /home/alexey/AndroidStudioProjects/libs/spdlog/spdlog/include
        /home/alexey/AndroidStudioProjects/libs/mbedtls/mbedtls/include
        )

find_library( # Sets the name of the path variable.
        log-lib log)

target_link_libraries( # Specifies the target library.
        fclient
        spdlog
        mbedcrypto
        ${log-lib})