# Simple script to build the project without an IDE
javac -d bin src/com/sambalana/Stack/*
cd bin
jar cfe itp-calc.jar com.sambalana.Stack.VectorStackDemo *
mv itp-calc.jar ..
