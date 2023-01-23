
Beschreibung der App
====================

Im Rahmen des Kurses *Mobile Betriebssysteme und Netzwerke* ist eine App
entstanden, welche das Brettspiel Scotland Yard (oder auch bekannt als
Mister X) in die reale Welt holt. Mit der App ist es möglich in einer
Gruppe eine Art digitales Verstecken in der realen Welt zu spielen. Der
Clou des Spiels ist es, dass eine Person von den anderen Spieler\*innen
gefunden werden muss und der Standort dieser Person nur alle 5 Minuten
(Zeitraum kann selbst festgelegt werden) geteilt wird. Die Standorte der
anderen Spieler\*innen wird in Echtzeit übertragen und auf der Karte
dargestellt. Die Standorte aller Personen werden über eine Firebase
Realtime Database ausgetauscht. Zur Visualisierung der Karte wird das
Mapbox Android SDK verwendet. Sollte die gesuchte Person innerhalb der
Spielzeit (Zeitraum kann selbst festgelegt werden) gefunden werden,
haben die Sucher\*innen gewonnen und die zu suchende Person verloren.
Damit verifiziert werden kann, ob die gesuchte Person wirklich gefunden
wurde, wird versucht eine Bluetooth-Verbindung zwischen den Geräten
herzustellen.

Benutzung der App
=================

Für eine bessere Vorstellung der App, werden in den folgenden
Abschnitten die einzelnen Schritte erläutert (und durch Screenshots
visualisiert), die man durchgeht, wenn man das Spiel nutzt.

Spiel starten bzw. Spiel beitreten
----------------------------------

Beim Starten der App muss eine Game-ID und ein Username festgelegt
werden und es gibt zwei Optionen. Entweder ein neues Spiel wird
gestartet oder man tritt einem Spiel bei. Beim Spielen mit Freunden
würde in dem Fall eine Person das Spiel starten und die anderen würden
diesem über die Game-ID beitreten.

Auf andere Spieler warten
-------------------------

Nach der Erstellung eines Spiels bzw. dem Beitreten eines Spiels, werden
alle Spieler angezeigt, die mitspielen. Die Ansicht wird aktualisiert,
sobald neue Spieler dem Spiel beitreten. Die Person, die das Spiel
erstellt hat (der GameMaster) hat die Möglichkeit in dieser Ansicht das
Spiel zu starten.

Einstellungen festlegen
-----------------------

Nach dem Starten des Spiels kann der GameMaster die Einstellungen
vornehmen. Dabei kann eingestellt werden, wie lange das Spiel dauert,
wie oft Mr. X auftauchen soll und wer Mr. X ist.

Mr. X versteckt sich
--------------------

Sobald der GameMaster mit den Einstellungen fertig ist, werden alle
Spieler benachrichtigt und Mr. X kann sich verstecken.

Mr. X suchen
------------

Nachdem Mr. X sich versteckt hat beginnt das Spiel richtig. Auf der
Karte werden alle Standorte der Spieler angezeigt. Für die Spieler, die
Mr. X suchen, wird der Standort von Mr. X nur in dem vorher definierten
Intervall geupdatet. Über und unter der Karte befinden sich zwei
Countdowns. Der obere Countdown gibt an, wann das Spiel vorbei ist und
Mr. X gewonnen hat, falls er bis dahin nicht gefunden wurde und der
untere Countdown gibt an, wann der Standort von Mr. X aktualisiert wird.
Für die Spieler, die Mr. X suchen gibt es zusätzlich noch die Funktion
zu chatten und sich auszutauschen. Sobald der Chat-Button rot ist, hat
man eine ungelesene Nachricht. Wenn Mr. X gefunden wurde, kann der
Slider unter der Karte verwendet werden. Dadurch wird nach allen
verfügbaren Bluetooth Geräten gesucht und überprüft, ob sich das von Mr.
X auch darunter befindet.

Chat
----

Für die Kommunikation unter den Suchenden, gibt es die Möglichkeit zu
chatten. Die ausgetauschten Nachrichten können nicht von Mr. X gelesen
werden und können zur Absprache der Strategie verwendet werden.

Komponenten
===========

Die App ist nach dem Model-View-Control Modell aufgebaut. Im
Nachfolgenden werden die einzelnen Komponenten detailliert beschrieben.

Model
-----

In dem Model werden alle für das Spiel verwendeten Daten lokal
gespeichert. Für die Realisierung wird dabei die Klasse
`GlobalState.java`{.java} verwendet. Mithilfe des Singleton-Pattern wird
sichergestellt, dass es von dieser Klasse nicht mehrere Instanzen gibt.
In dieser Klasse werden folgende Daten gespeichert:

  **Datentyp**                                        **Name**           **Beschreibung**
  --------------------------------------------------- ------------------ ------------------------------------------------------------------------------------------
  `ArrayList<Message>`{.java}                         allMessages        Speichert die Nachrichten aller Spieler.
  `List<User>`{.java}                                 allUserData        Speichert die Daten aller Spieler.
  `HashMap<String, HashMap<String, String>>`{.java}   allExistingGames   Speichert alle bereits existierenden Spiele inklusive der Usernamen und Bluetooth Namen.
  `GameData`{.java}                                   gameData           Speichert die allgemeinen Daten für das Spiel.
  `User`{.java}                                       myUserData         Speichert die eigenen Spieler Daten.
  `Long`{.java}                                       startTime          Speichert, ab wann Mr. X gesucht werden kann.
  `ArrayList<Long> mrXStartTimes`{.java}              mrXStartTimes      Speichert die Zeiten, zu denen Mr. X auf der Karte auftaucht

Für die Speicherung der allgemeinen Spieldaten, der Nachrichten und der
Spieler, wurden eigene Klassen erstellt. Diese werden im folgenden kurz
erörtert.

### Message

Die Klasse `Message.java`{.java} speichert für jede Nachricht die
benötigten Daten. Diese sind:

  **Datentyp**      **Name**    **Beschreibung**
  ----------------- ----------- -------------------------------------------------------------------------------
  `String`{.java}   username    Speichert den Username des Spielers, der die Nachricht versendet hat.
  `int`{.java}      timestamp   Speichert die Zeit im timestamp-Format, zu der die Nachricht versendet wurde.
  `String`{.java}   message     Speichert den Inhalt der Nachricht.

### GameData

Die Klasse `GameData.java`{.java} speichert für das Spiel die benötigten
Daten. Diese sind:

  **Datentyp**          **Name**          **Beschreibung**
  --------------------- ----------------- ------------------------------------------------------------
  `int`{.java}          gameId            Speichert die Game-ID.
  `Integer`{.java}      gameDuration      Speichert die Dauer des Spiels in Minuten.
  `Integer`{.java}      showMrXInterval   Speichert das Interval, in dem Mr. X auftaucht in Minuten.
  `GameStatus`{.java}   gameStatus        Speichert den GameStatus als Enumeration.
  `String`{.java}       mrX               Speichert den Username von Mr. X.

### User

Die Klasse `User.java`{.java} speichert für jeden Spieler die benötigten
Daten. Diese sind:

  **Datentyp**       **Name**              **Beschreibung**
  ------------------ --------------------- ---------------------------------------------
  `double`{.java}    longitude             Speichert den Längengrad des Spielers.
  `double`{.java}    latitude              Speichert den Breitengrad des Spielers.
  `boolean`{.java}   iAmMrX                Speichert, ob der Spieler Mr. X ist.
  `boolean`{.java}   iAmTheGameMaster      Speichert, ob der Spieler GameMaster ist.
  `int`{.java}       userID                Speichert die User-ID des Spielers.
  `String`{.java}    bluetoothDeviceName   Speichert den Bluetooth Namen des Spielers.
  `String`{.java}    username              Speichert den Username des Spielers.

View
----

Für das Spiel werden verschiedene Views/Activities benötigt. Diese sind:

  **Name der Activity**          **Beschreibung**
  ------------------------------ ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  `StartScreen.java`{.java}      Diese Activity wird beim Start der App geöffnet. Hier haben die Spieler die Möglichkeit entweder ein neues Spiel zu erstellen oder einem Spiel beizutreten.
  `JoiningScreen.java`{.java}    Sobald der Spieler ein Spiel erstellt hat oder einem beigetreten ist, wird diese Activity geöffnet. Hier sehen alle Spieler, wer alles mitspielt. Sobals ein neuer Spieler dem Spiel beitritt, wird der Name angezeigt. Für das Anzeigen der Spieler wird ein RecyclerView verwendet. Der Spieler, der das Spiel erstellt hat (GameMaster) hat in dieser Activity die Möglichkeit, das Spiel zu starten.
  `SettingsScreen.java`{.java}   Wenn der GameMaster das Spiel gestartet hat, werden ihm die Einstellungen angezeigt. Hier kann er einstellen, wie lange das SPiel dauert, wie oft Mr. X auftaucht und wer Mr. X ist.
  `LoadingScreen.java`{.java}    Diese Activity wird mehrfach verwendet und verwendet die Methode `getIntent().getExtras()`{.java}, um die Daten zu laden, welche angeben, was dargestellt werden soll. Diese Activity wird verwendet, wenn der GameMaster die Einstellungen festlegt, Mr. X sich versteckt und wenn das Spiel vorbei ist.
  `MapScreen.java`{.java}        Diese Activity zeigt die Karte und die Standorte aller Spieler. Über und unter der Karte befinden sich zwei Countdowns. Der obere Countdown zeigt an, wie lange das Spiel noch geht und der untere zeigt an, wann Mr. X das nächste mal auftaucht. In der linken oberen Ecke befindet sich ein Button, mit dem man den Chat öffnet und mit den anderen Spielern schreiben kann. Unter der Karte befindet sich zudem ein Slider, den man verwenden muss, wenn man Mr. X gefunden hat. Der Chat-Button und der Slider werden nur angezeigt, wenn man selber nicht Mr. X ist.
  `ChatScreen.java`{.java}       Diese Activity zeigt den Chat der Spieler an. Alle Nachrichten werden chronologisch mit Hilfe eines RecyclerViews angezeigt, wobei einkommende Nachrichten auf der linken Seite inklusive Name erscheinen und gesendete Nachrichten auf der rechten Seite.

Control
-------

Die App besitzt zwei Controller. Den `GameDataController.java`{.java}
und den `FireBaseController.java`{.java}. Der
`GameDataController.java`{.java} steuert die komplette Logik zwischen
dem Model und dem View, die sich auf die lokal gespeicherten Daten
bezieht und besitzt folgende Methoden:

  **Name der Methode**                          **Beschreibung**
  --------------------------------------------- --------------------------------------------------------------------------
  `gameIdExistsAlready()`{.java}                Überprüft, ob eine Game-ID bereits existiert.
  `createNewGame()`{.java}                      Erstellt ein neues Spiel.
  `joinGame()`{.java}                           Tritt einem bereits vorhanden Spiel bei.
  `bluetoothDeviceNameExistsAlready()`{.java}   Überprüft, ob der Bluetooth Name bereits vergeben ist.
  `usernameExistsAlready()`{.java}              Überprüft, ob der Username bereits vergeben ist.
  `usernameAndGameIdAreSetCorrect()`{.java}     Überprüft, ob die Eingabe von dem Username und der Game-ID korrekt sind.
  `generateMrXStartTimes()`{.java}              Berechnet die Zeiten, zu denen Mr. X auftaucht.
  `getLastMrXStartTime()`{.java}                Gibt die Zeit zurück, zu der Mr. X das letzte Mal aufgetaucht ist.
  `updateUserLocation()`{.java}                 Ändert die Positionsdaten eines Spielers
  `getMrXUserId()`{.java}                       Gibt die User-ID von Mr. X zurück.
  `didIFoundMrX()`{.java}                       Überprüft, ob man Mr. X gefunden hat.
  `amIMrX()`{.java}                             Überprüft, ob man selber Mr. X ist.
  `getAllUser()`{.java}                         Gibt alle Spieler zurück.
  `getGameDuration()`{.java}                    Gibt die Dauer des Spiels zurück.
  `getShowMrXInterval()`{.java}                 Gibt das Zeitintervall zurück, in dem Mr. X auftaucht.
  `getAllMessages()`{.java}                     Gibt alle Nachrichten zurück.
  `getGameStatus()`{.java}                      Gibt den aktuellen GameStatus zurück.
  `getUserByName()`{.java}                      Gibt die Daten eines Spieler zurück über den Username.

Der `FireBaseController.java`{.java} steuert die komplette Logik
zwischen der Firebase-Datenbank und dem Model bzw. dem
`GameDataController.java`{.java}.

  **Name der Methode**             **Beschreibung**
  -------------------------------- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  `createNewGame()`{.java}         Lädt die Daten eines neu erstellten Spiels in die Datenbank.
  `joinGame()`{.java}              Lädt die Daten des Spielers in die Datenbank, wenn er einem Spiel beigetreten ist.
  `getAllExistingGames()`{.java}   Lädt alle bereits existierenden Game-IDs aus der Datenbank inklusive der Usernames und der Bluetooth Namen. Dadurch kann überprüft werden, ob man ein Spiel erstellen kann oder einem Spiel beitreten kann.
  `loadOtherPlayers()`{.java}      Sobald in der Datenbank ein neuer Spieler hinzugefügt wurde, wird diese Methode ausgelöst und die Daten werden lokal gespeichert. Dadurch können in der Activity `JoiningScreen.java`{.java} alle Spieler angezeigt werden.
  `settingDone()`{.java}           Speichert in der Datenbank, dass der GameMaster mit den Einstellungen fertig ist und das Spiel starten kann.
  `joiningDone()`{.java}           Speichert in der Datenbank, dass der GameMaster das Spiel gestartet hat.
  `loadNewGameStatus()`{.java}     Überprüft, ob sich der GameStatus verändert hat. Dadurch wissen die anderen Spieler, die nicht der GameMaster sind, wann das Spiel startet.
  `sendMessage()`{.java}           Beim versenden von Nachrichten, werden diese mit Hilfe dieser Methode in der Datenbank gespeichert.
  `loadMessages()`{.java}          Überprüft, ob ein Spieler eine Nachricht an die Datenbank gesendet hat. Wenn eine neue Nachricht ankommt, wird diese lokal hinzugefügt und kann in der Activity `ChatScreen.java`{.java} angezeigt werden.
  `updateMyLocation()`{.java}      Sendet die aktuelle Location an die Datenbank.

![Model-View-Control Diagramm](uml.pdf){#fig:game width="\\linewidth"}

Tests
=====

Unit-Tests
----------

Die Unit-Tests befinden sich in der Klasse
`GameDataControllerTests.java`{.java}. Mithilfe dieser Tests wird die
Logik der Klasse `GameDataController.java`{.java} getestet. Da diese
Tests auf das Model (`GlobalState`{.java}) zugreifen und mit dem
Singleton-Pattern dafür gesorgt wird, dass es von dieser Klasse nicht
mehrere Instanzen gibt, wird vor jedem Test die Methode `clear()`{.java}
aufgerufen, welche mit der `@Before`{.java}-Annotation versehen ist und
das Model zurücksetzt.

  **Name**                                **Beschreibung**                                                                                                                                                                               **Check**
  --------------------------------------- ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- -----------
  createNewGameTest1()                    Dieser Test überprüft, ob ein Spiel erstellt werden kann.                                                                                                                                      
  createNewGameTest2()                    Dieser Test überprüft, ob ein Fehler geworfen wird, wenn zwei Spiele erstellt werden.                                                                                                          
  createNewGameTest3()                    Dieser Test überprüft, ob kein Spiel vorhanden ist, wenn kein Spiel erstellt wurde.                                                                                                            
  createNewGameTest4()                    Dieser Test überprüft, ob ein Spiel vorhanden ist, nachdem es erstellt wurde.                                                                                                                  
  addUserTest1()                          Dieser Test überprüft, ob einem Spiel beigetreten werden kann.                                                                                                                                 
  addUserTest2()                          Dieser Test überprüft, ob ein Fehler geworfen wird, wenn zwei Spielen beigetreten wird.                                                                                                        
  addUserTest3()                          Dieser Test überprüft, ob die bereits bestehenden Spiele richtig geladen werden, damit überprüft werden kann, ob ein Spiel bereits existiert oder nicht.                                       
  usernameAndGameIdAreSetCorrectTest1()   Dieser Test überprüft, ob die Eingabe von einer korrekten Game-ID und einem korrekten Username auch als korrekt erkannt wird.                                                                  
  usernameAndGameIdAreSetCorrectTest2()   Dieser Test überprüft, ob die Eingabe von einer korrekten Game-ID und einem nicht korrekten Username auch als nicht korrekt erkannt wird und dementsprechend ein Fehler geworfen wird.         
  usernameAndGameIdAreSetCorrectTest3()   Dieser Test überprüft, ob die Eingabe von einer nicht korrekten Game-ID und einem korrekten Username auch als nicht korrekt erkannt wird und dementsprechend ein Fehler geworfen wird.         
  usernameAndGameIdAreSetCorrectTest4()   Dieser Test überprüft, ob die Eingabe von einer nicht korrekten Game-ID und einem nicht korrekten Username auch als nicht korrekt erkannt wird und dementsprechend ein Fehler geworfen wird.   
  generateMrXStartTimesTest1()            Dieser Test überprüft, ob die Zeiten berechnet werden können, zu denen Mr. X auftaucht.                                                                                                        
  generateMrXStartTimesTest2()            Dieser Test überprüft, ob ein Fehler geworfen wird, wenn die Zeiten von Mr. X berechnet werden sollen, ohne dass die Startzeit des Spiels festgelegt wurde.                                    
  generateMrXStartTimesTest3()            Dieser Test überprüft, ob die Anzahl der Zeiten, an denen Mr. X auftaucht korrekt berechnet werden.                                                                                            
  generateMrXStartTimesTest4()            Dieser Test überprüft, ob die Anzahl der Zeiten, an denen Mr. X auftaucht korrekt berechnet werden.                                                                                            
  generateMrXStartTimesTest5()            Dieser Test überprüft, ob die Anzahl der Zeiten, an denen Mr. X auftaucht korrekt berechnet werden.                                                                                            
  getLastMrXStartTimeTest1()              Dieser Test überprüft, ob bei der Auswahl der nächsten Zeit, zu der Mr. X auftauchen soll, die richtige zurückgegeben wird.                                                                    
  getUserByNameTest1()                    Dieser Test überprüft, ob mit dem Username eines existierenden Spielers auf die Daten des Spielers zugegriffen werden kann.                                                                    
  getUserByNameTest2()                    Dieser Test überprüft, ob ein Fehler geworfen wird, wenn versucht wird auf einen Spieler zuzugreifen, der nicht existiert.                                                                     
  updateUserLocationTest1()               Dieser Test überprüft, ob der Standort eines existierenden Spielers geändert werden kann.                                                                                                      
  updateUserLocationTest2()               Dieser Test überprüft, ob ein Fehler geworfen wird, wenn der Standort eines Spielers geändert wird, der nicht existiert.                                                                       
  getMrXGameIdTest1()                     Dieser Test überprüft, ob die User-ID von Mr. X korrekt zurückgegeben wird.                                                                                                                    
  getMrXGameIdTest2()                     Dieser Test überprüft, ob die User-ID von Mr. X korrekt zurückgegeben wird, nachdem Mr. X geändert wurde.                                                                                      
  didIFoundMrXTest1()                     Dieser Test überprüft, ob Mr. X gefunden wird, wenn der korrekte Bluetooth Name angegeben wird.                                                                                                
  didIFoundMrXTest2()                     Dieser Test überprüft, ob Mr. X nicht gefunden wird, wenn ein nicht korrekter Bluetooth Name angegeben wird.                                                                                   
  didIFoundMrXTest3()                     Dieser Test überprüft, ob Mr. X nicht gefunden wird, wenn ein nicht korrekter Bluetooth Name angegeben wird.                                                                                   
  didIFoundMrXTest4()                     Dieser Test überprüft, ob Mr. X nicht gefunden wird, wenn ein nicht korrekter Bluetooth Name angegeben wird.                                                                                   
  amIMrXTest1()                           Dieser Test überprüft, ob ich nicht Mr. X bin, wenn dass der Fall ist.                                                                                                                         
  amIMrXTest2()                           Dieser Test überprüft, ob ich Mr. X bin, wenn dass der Fall ist.                                                                                                                               

UI-Tests
--------

Die UI-Tests werden mit dem Espresso-Framework durchgeführt und
überprüfen, ob die UI-Elemente alle sichtbar sind. Dafür wird vor jedem
Test die Methode `setData()`{.java}, welche mit der
`@Before`{.java}-Annotation versehen ist, aufgerufen. Diese Methode
sorgt dafür, dass alle nötigen Daten gespeichert sind, die benötigt
werden, um diese Activity aufzurufen.

Aussicht
========

Bei der Entwicklung der App konnte in dem zur Verfügung stehenden
Zeitraum nicht alles umgesetzt werden, was zu Beginn geplant war. Aus
diesem Grund werden im Folgenden Features aufgelistet, die in der
Zukunft noch umgesetzt werden sollen.

-   Die Spieler dürfen sich nur in einem bestimmten Radius bewegen.

-   Anstelle der Firebase Datenbank soll eine andere Datenbank verwendet
    werden.

-   Verschiedene Spiel-Modi einführen.
