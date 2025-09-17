#!/usr/bin/env sh
# Simplified Gradle wrapper script that defers to the system Gradle installation.
# This project ships without the standard Gradle wrapper distribution because the
environment running the build already provides Gradle. The script keeps the
# developer workflow consistent by exposing the same entry point as the typical
# ./gradlew wrapper.

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
else
  echo "Gradle is required to build this project" >&2
  exit 1
fi
