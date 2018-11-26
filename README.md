# TAGit - Smart Scavenger Hunt System

This is the flask application used to manage the database of the app
<br><br>
https://coen390-a-team.herokuapp.com

## Endpoints
***
| endpoint                              | type   | Function                                                       |
| --------------------------------------|--------|----------------------------------------------------------------|
| /                                     | GET    | Brings you to the a webpage to view the games                  |
| /new_game                             | GET    | Brings you to the a webpage to add a new game                  |
| /game/\<string:game_name\>            | GET    | returns a json containing the info of this game                |
| /team_score/\<int:team_id\>           | GET    | returns an array containing the hint_id of the hint that the team has found |
| /hint/\<int:team_id\>/\<int:tag_id\>  | POST   | Posts the hint that has been found as found by the team        |
| /game_top_three/\<int:game_id\>       | GET    | returns a json containing the top 3 teams of the game id sent  |


<br><br>
***
GET game endpoint will return a json similar to: 
```
{
    "end_time": 1514825676008,
    "game_id": 18,
    "game_owner": "jn",
    "hints": [
        "EV basment",
        "ECA office",
        "Roof of hall"
    ],
    "tag_ids": [
        88,
        89,
        90
    ],
    "team_colours": [
        "color1",
        "color2",
        "color3"
    ],
    "team_ids": [
        86,
        87,
        88
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
The GET top 3 team endpoint will return
```
{
    "team_score": [
        3,
        2,
        1
    ],
    "winner_ids": [
        87,
        88,
        86
    ]
}
```