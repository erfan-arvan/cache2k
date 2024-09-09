#!/bin/bash

# Function to count and calculate the difference for a given file
function count_nullness_diff {
    file=$1

    # Count occurrences of [nullness:
    count_nullness=$(grep -c "\[nullness:" "$file")

    # Count occurrences of [nullness:type.anno.before.modifier
    count_modifier=$(grep -c "\[nullness:type\.anno\.before\.modifier" "$file")

    # Calculate the difference
    difference=$((count_nullness - count_modifier))

    # Print results
    echo "File: $file"
    echo "Count of [nullness: = $count_nullness"
    echo "Count of [nullness:type.anno.before.modifier = $count_modifier"
    echo "Difference (nullness - modifier) = $difference"
    echo "------------------------------------"
}

# Count for preCheck.out
count_nullness_diff "preCheck.out"

# Count for postCheck.out
count_nullness_diff "postCheck.out"

