#!/bin/bash

# Path to the JAR file to copy
JAR_PATH="/Users/erfanarvan/Desktop/comparisonPaper/wpi-njr-debug/jsr305-3.0.2.jar"

# Iterate over all subdirectories of the current directory
for dir in */; do
    # Navigate into the subdirectory
    if [ -d "$dir" ]; then
        echo "Processing directory: $dir"

        # Step 1: Find and remove import statements for Nullable
        find "$dir" -type f -path "*/src/*.java" -exec sed -i '' '/import .*Nullable;/d' {} +

        # Step 2: Add import statement for javax.annotation.Nullable
        find "$dir" -type f -path "*/src/*.java" -exec sed -i '' '/^package/a\'$'\n''import javax.annotation.Nullable;' {} +

        # Step 3: Navigate into the lib directory and copy the jar file
        if [ -d "$dir/lib" ]; then
            echo "Copying jsr305-1.3.9.jar to $dir/lib"
            cp "$JAR_PATH" "$dir/lib"
        else
            echo "lib directory not found in $dir"
        fi
    fi
done

echo "Processing complete."

