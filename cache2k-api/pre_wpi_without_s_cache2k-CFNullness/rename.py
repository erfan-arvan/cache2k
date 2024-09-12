# visit all files in org directory and rename .ajava to .java
# usage: python rename.py

import os



#org/cache2k/CacheException-org.checkerframework.checker.nullness.KeyForSubchecker.java
# walk all files in org directory rcursively and visit child directories
def visit_files():
    for root, dirs, files in os.walk("srcI"):
        for file in files:
            if file.endswith(".java"):
                continue
            if file.endswith("-org.checkerframework.checker.nullness.NullnessChecker.ajava"):
                print("renaming file: ", file)
                # rename -org.checkerframework.checker.nullness.NullnessChecker.ajava in the file name to .java
                os.rename(os.path.join(root, file), os.path.join(root, file.replace("-org.checkerframework.checker.nullness.NullnessChecker.ajava", ".java")))
            else:
                # delete file:
                os.remove(os.path.join(root, file))
                

visit_files()
