cd src
javac -encoding utf-8 -Xlint:unchecked -cp .;..\librarys -d ..\out fc/Main.java
copy fc\ui\*.fxml ..\out\fc\ui
set /P input=