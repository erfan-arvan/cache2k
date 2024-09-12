# iterate over src and dst and copy any file existing in src but not in dst to dst in the same directory
# usage: python move.py

import os
import shutil

def move_files():
    src = "src"
    dst = "srcI"
    index = len("/Users/nima/Developer/annotator-run/src/main/java/com/")
    for root, dirs, files in os.walk(src):
        for file in files:
            if file.endswith("package-info.java") or file.endswith(".DS_Store"):
                continue
            fromm = os.path.join(root, file)[index:]
            if not os.path.exists(dst + fromm):
                print("copying file: ", file)
                print("from: ", os.path.join(root, file))
                print("to: ", dst + fromm)
                # move file from src to dst
                os.system("cp " + os.path.join(root, file) + " " + dst + fromm)

move_files()

