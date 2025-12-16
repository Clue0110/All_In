#!/bin/bash

APP_NAME="all_in_game"  

echo "Stopping old instances of $APP_NAME..."
docker stop $(docker ps -q --filter ancestor=$APP_NAME) 2>/dev/null

echo "Building $APP_NAME..."
if docker build -t $APP_NAME . ; then
    echo "Build Successful!"
else
    echo "Build Failed."
    exit 1
fi

echo "Starting Server..."
docker run -p 8080:8080 $APP_NAME