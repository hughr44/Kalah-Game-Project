# Kalah-Game-Project
created for CSCE 315 Programming Studio class

To play Kalah download this repository on your desktop and navigate to the folder in a terminal and perform the following commands

*to play against a player on a different computer must first initialize the server on one computer before the second player connects*

Server Side:

cd workspace\315Project2-V2

javac -d bin src\KalahGame\*.java

cd bin

java KalahGame.Server


*the following is used to play against the AI or another player on the same computer*

Client Side:

cd workspace\315Project2-V2\bin

java KalahGame.Driver
