# TAGit - Smart scavenger hunt System

This is the fask application used to manage the database of the app
<br><br>

##Endpoints
***
| endpoint                              | type   | Function                                                        |
| --------------------------------------|--------|----------------------------------------------------------------|
| https://coen390-a-team.herokuapp.com/ | GET    | return helloworld as a string                                   |
| find_game/\<id of the game\>          | GET    | returns the name of the game and the questions in an array      |
| /createclient                         | POST   | creates a user in the database you must pass it the proper json |
| /add_game                             | POST   | creates a game in the database you must pass it the proper json |
| /deleteTable                          | DELETE | selfexplanatory |



●	To obtain the first hint and start the game: /getfirsthint/<int:game_id>

●	To obtain the second hint you must send thefirst tag: /gethint
{
   "gameId": 1,
   "tags": [420]
}

●	To finish the game send the first and second tag: /gethint 
{
   "gameId": 1,
   "tags": [420, 1017]
}
<br><br>
***

### Design

***

<br><br>

### System Architecture

***


<br><br>

### Hardware Architecture

***

TBD
<br><br>

### Software Architecture

***

_**User/Login Creation**_<br>


_**Team Creation**_<br>
!
_**Finding A Match**_<br>

_**Sensor Detects Hit**_<br>
<br><br>

### Class Diagrams

***

_**User/Login Creation**_<br>
<br><br>

### Use Cases

***


●	_Use Case 1:_ User interaction, the user must be able to interact with his profile, other users and teams in order to create matches and keep track of who has the best team. Interaction between actor, phone and database.

●	_Use Case 2:_ Phone interaction with Gun, once a match has started the phone will need to interact with the gun to keep track of statistics on the player, the gun will need to be paralyzed if the user if shot.

●	_Use Case 3:_ Interaction of the phone, sensor and database. Once the sensor detects that it has been hit it must communicate with the phone to keep track of which gun interacted with it. Then the phone must disable the gun (for a limited time) and store this information in the database so that other users can access the data and keep up with their scores.	
<br><br>
