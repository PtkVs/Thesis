# Specify the minimum version.
cmake_minimum_required(VERSION 3.9)
# Specify the project info.
project(crypto VERSION 1.0.0 DESCRIPTION "Proxy Re-Encryption library")

option( BUILD_STATIC "Set to ON to include static versions of the library" OFF)

find_package(OpenFHE)

set( CMAKE_CXX_FLAGS ${OpenFHE_CXX_FLAGS} )

include_directories( ${OPENMP_INCLUDES} )
include_directories( ${OpenFHE_INCLUDE} )
include_directories( ${OpenFHE_INCLUDE}/third-party/include )
include_directories( ${OpenFHE_INCLUDE}/core )
include_directories( ${OpenFHE_INCLUDE}/pke )
### add directories for other OpenFHE modules as needed for your project

link_directories( ${OpenFHE_LIBDIR} )
link_directories( ${OPENMP_LIBRARIES} )
set( CMAKE_EXE_LINKER_FLAGS ${OpenFHE_EXE_LINKER_FLAGS} )
link_libraries( ${OpenFHE_SHARED_LIBRARIES} )


# Declare the library target.
file(GLOB_RECURSE SOURCES "src/include/*.hpp")
file(GLOB_RECURSE CPPSOURCES "src/lib/*.cpp")

add_library(${PROJECT_NAME} SHARED
    ${SOURCES}
    ${CPPSOURCES}
)
# Configure the directories to search for header files.
target_include_directories(${PROJECT_NAME} PRIVATE src/lib)
target_include_directories(${PROJECT_NAME} PRIVATE src/include)
# Set the version property.
set_target_properties(${PROJECT_NAME} PROPERTIES VERSION ${PROJECT_VERSION})
# Set the shared object version property to the project's major version.
set_target_properties(${PROJECT_NAME} PROPERTIES SOVERSION ${PROJECT_VERSION_MAJOR})
# Set the public header property to the one with the actual API.
set_target_properties(${PROJECT_NAME} PROPERTIES PUBLIC_HEADER src/include/crypto.hpp)

# For access to standard installation directory variables (CMAKE_INSTALL_xDIR).
include(GNUInstallDirs)
# Set library shared object and API header file to install.
install(TARGETS ${PROJECT_NAME}
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}
)
# Create the pkg-config file from the template.
configure_file(src/${PROJECT_NAME}.pc.in ${PROJECT_NAME}.pc @ONLY)
# Set pkg-config file to install.
install(FILES ${CMAKE_BINARY_DIR}/${PROJECT_NAME}.pc
    DESTINATION ${CMAKE_INSTALL_LIBDIR}/pkgconfig
)