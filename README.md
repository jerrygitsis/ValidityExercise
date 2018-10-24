# ValidityExercise

To compile and run code follow these steps:

 1. open cmd prompt
 2. chdir to the path you would like to clone this repo into
 3. enter "git clone https://github.com/jerrygitsis/ValidityExercise"
 4. enter "chdir ./ValidityExercise"
 5. unzip gradle.zip and src.zip, leave them in the ValidityExercise folder
 6. enter "gradlew build"
 7. enter "java -jar build/libs/duplicate-record-finder-0.1.0.jar"
     > Open your browser and enter :
     > http://localhost:8080/findDups?file=[path to file]
     > where path to file is the csv file with the '/'s replaced by '_'s
     > for example :
     > http://localhost:8080/findDups?file=C:_Users_Jerry_Desktop_ValidityExercise_advanced.csv
 8. enter ctrl + C in command promt to kill the proccess

