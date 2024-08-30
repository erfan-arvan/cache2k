#!/bin/bash


CLASSPATH="/Users/erfanarvan/.m2/repository/javax/cache/cache-api/1.1.0/cache-api-1.1.0.jar:/Users/erfanarvan/.m2/repository/org/cache2k/cache2k-testing/2.0-SNAPSHOT/cache2k-testing-2.0-SNAPSHOT.jar:/Users/erfanarvan/.m2/repository/junit/junit/4.13.1/junit-4.13.1.jar:/Users/erfanarvan/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/Users/erfanarvan/.m2/repository/org/cache2k/cache2k-pinpoint/2.0-SNAPSHOT/cache2k-pinpoint-2.0-SNAPSHOT.jar
"

# Define the target directory as the current directory
TARGET_DIR="."

# Split the classpath into an array
IFS=':' read -r -a paths <<< "$CLASSPATH"

# Create a temporary directory for extracting AAR files
TEMP_DIR=$(mktemp -d)

# Function to extract JAR files from AAR files and copy to the target directory
extract_jar_from_aar() {
    aar_file=$1
    aar_name=$(basename "$aar_file" .aar)
    dest_dir="$TARGET_DIR"

    unzip -o "$aar_file" -d "$TEMP_DIR"
    
    if [ -f "$TEMP_DIR/classes.jar" ]; then
        echo "Copying classes.jar from $aar_file to $dest_dir/$aar_name.jar"
        mv "$TEMP_DIR/classes.jar" "$dest_dir/$aar_name.jar"
    else
        echo "classes.jar not found in $aar_file"
    fi
    
    if [ -d "$TEMP_DIR/libs" ]; then
        for jar in "$TEMP_DIR/libs/"*.jar; do
            if [ -f "$jar" ]; then
                jar_name=$(basename "$jar")
                echo "Copying $jar from $aar_file to $dest_dir/$aar_name-$jar_name"
                mv "$jar" "$dest_dir/$aar_name-$jar_name"
            else
                echo "No JAR files found in libs directory of $aar_file"
            fi
        done
    else
        echo "libs directory not found in $aar_file"
    fi
    
    rm -rf "$TEMP_DIR/*"
}

# Copy each item in the classpath to the target directory
for path in "${paths[@]}"; do
    if [[ "$path" == *.aar ]]; then
        echo "Extracting JAR from $path"
        extract_jar_from_aar "$path"
    elif [[ "$path" == *.jar ]]; then
        jar_name=$(basename "$path")
        echo "Copying JAR from $path to $TARGET_DIR"
        cp -rf "$path" "$TARGET_DIR/$jar_name"
    fi
done

# Clean up
rm -rf "$TEMP_DIR"

echo "Classpath files have been copied to $TARGET_DIR"
