# TAGit - Smart Scavenger Hunt System

This is the flask application used to manage the database of the app
<br><br>
https://coen390-a-team.herokuapp.com

##Endpoints
***
| endpoint                              | type   | Function                                                       |
| --------------------------------------|--------|----------------------------------------------------------------|
| /                                     | GET    | return hello world in a json                                   |
| /game/\<string:game_name\>            | GET    | returns a json containing the info of this game                |
| /team_score/\<int:team_id\>           | GET    | returns an array containing the hint_id of the hint that the team has found |
| /hint/\<int:team_id\>/\<int:tag_id\>  | POST   | Posts the hint that has been found as found by the team        |
| /game                                 | PUT    | Puts a new game in the db if the json passed is valid          |


<br><br>
***
GET game endpoint will return a json similar to: 
```
{
    "game_id": 16,
    "hints": [
        "EV basment",
        "ECA office",
        "Roof of hall"
    ],
    "tag_ids": [
        82,
        83,
        84
    ],
    "tags": [
        "1h3w4",
        "3h4iw8",
        "3h4j5"
    ],
    "team_colours": [
        "color1",
        "color2",
        "color3"
    ],
    "team_ids": [
        80,
        81,
        82
    ],
    "team_names": [
        "team1",
        "team2",
        "team3"
    ]
}
```
<br><br>
***
The GET team_score endpoint will return
```
{
    "hints_id": [
        84,
        83,
        82
    ]
}
```
<br><br>
***
The POST hint endpoint will return
```
{
    "message": "tag added"
}
```
or
```
{
    "message": "tag already found"
}
```
<br><br>
***
The PUT game endpoint will expect
```
{
    "name": "bitch",
    "username": "jn",
    "endtime": 1514825676008,
    "teams": [
        "team1",
        "team2",
        "team3"
    ],
    "colours": [
        "color1",
        "color2",
        "color3"
    ],
    "hints": [
        "EV basment",
        "ECA office",
        "Roof of hall"
    ],
    "tags": [
        "1h3w4",
        "3h4iw8",
        "3h4j5"
    ]
}
```
And Return
```
{
    "message": "success"
}
```