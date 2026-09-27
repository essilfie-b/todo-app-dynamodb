#!/bin/bash

echo "Cleaning previous builds..."
rm -rf layer/java/lib/*
rm -rf target/*

echo "Building Lambda Layer..."
cd layer
mvn clean package
cd ..

echo "Building main application..."
mvn clean package

echo "Build complete!"
