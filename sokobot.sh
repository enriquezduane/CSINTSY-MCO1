#!/bin/sh

remove_files() {
  for file in "$@"; do
    rm -f "$file"
  done
}

compile_class() {
  javac "$1" -cp src
}

files_to_remove=(
  src/gui/BotThread.class
  src/gui/GameFrame.class
  src/gui/GamePanel.class
  src/main/Driver.class
  src/reader/FileReader.class
  src/reader/MapData.class
  src/solver/SokoBot.class
)

remove_files "${files_to_remove[@]}"

compile_class src/main/Driver.java

java -classpath src main.Driver testlevel bot

