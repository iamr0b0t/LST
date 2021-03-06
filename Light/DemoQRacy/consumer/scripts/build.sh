#!/bin/bash

BUILD_DEST=../out
[ ! -d $BUILD_DEST ] && mkdir $BUILD_DEST
cd $BUILD_DEST

javac  ../src/Pointer.java -d $BUILD_DEST 
javac  -cp :../libs/core-3.3.0.jar:../libs/javase-3.3.0.jar:../libs/commons-cli-1.4.jar ../src/Screen.java -d $BUILD_DEST 

echo "Compiled destination: $BUILD_DEST"
