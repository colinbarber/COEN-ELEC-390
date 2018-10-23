# Tag You're Lit - Smart Laser Tag System

This project aims to create a mobile application-based laser tag system for 2 or more players. 
<br><br>

### Design

***

![image](https://user-images.githubusercontent.com/29033135/46921913-84eb6780-cfcf-11e8-99eb-fe53093a8ba5.png)
![image](https://user-images.githubusercontent.com/29033135/46921924-b19f7f00-cfcf-11e8-9799-0d9bb3b5b685.png)
![image](https://user-images.githubusercontent.com/29033135/46921930-c419b880-cfcf-11e8-8f9a-991647adf297.png)
<br><br>

### System Architecture

***

![image](https://user-images.githubusercontent.com/29033135/46921951-13f87f80-cfd0-11e8-9ef7-436e948d53fa.png)
<br><br>

### Hardware Architecture

***

TBD
<br><br>

### Software Architecture

***

_**User/Login Creation**_<br>


_**Team Creation**_<br>
![image](https://user-images.githubusercontent.com/29033135/46921980-5ae67500-cfd0-11e8-8623-471956a6c095.png)<br>

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
