import datetime
import time

from flask import request
from TAGit._init_ import app, db
from TAGit.models.games import Game
from TAGit.models.teams import Team
from TAGit.models.tags import Tag
from TAGit.Exception import InvalidUsage
from flask import jsonify


# tested
@app.route('/', methods=['GET'])
def default():
    app.logger.info('%s IP adresse', request.remote_addr)
    return "helloworld"


@app.route('/game/<string:game_name>', methods=['GET'])
def get_hints(game_name):
    app.logger.info("Game name of hints requested", game_name)
    game = Game.query.filter_by(name=game_name).first()
    hintids = toInt(game.tag_ids.split(","))
    hinttag = getHintAndTag(hintids)
    teamids = game.team_ids.split(",")
    team = getTeam(game.team_ids.split(","))
    return jsonify(
        {
            "game_id": game.id,
            "tag_ids": hintids,
            "hints": hinttag[0],
            "tags": hinttag[1],
            "team_ids": teamids,
            "team_names": team[0],
            "team_colours": team[1]
        })


def getTeam(team_ids):
    teamname = []
    teamcolour = []
    for ti in team_ids:
        team = Team.query.get(ti)
        teamname.append(team.name)
        teamcolour.append(team.colour)
    return teamname, teamcolour


"""
{
    "name": "bitch",
    "username": "jn",
    "endtime": "14:50",
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
"""
@app.route('/game/', methods=['PUT'])
def creat_game():
    content = request.get_json()
    app.logger.info("Game name of hints requested", content)
    gname = content["name"]
    uname = content["username"]
    #TODO endtime = content["endtime"]
    ts = time.time()
    endtime = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
    teams = content["teams"]
    colours = content["colours"]
    hints = content["hints"]
    tags = content["tags"]
    teamids = createTeam(teams, colours)
    tagids = createTag(hints, tags)
    game = Game(name=gname,
                user_name=uname,
                team_ids=','.join([str(i) for i in teamids]),
                tag_ids=','.join([str(i) for i in tagids]),
                time_end=endtime)
    db.session.add(game)
    db.session.commit()
    return jsonify(
        {
            "message": "sucsess"
        })


@app.route('/hint/<int:team_id>/<int:tag_id>', methods=['POST'])
def put_team_hint(team_id, tag_id):
    app.logger.info("team id", team_id)
    app.logger.info("tag id ", tag_id)
    team = Team.query.get(team_id)
    points = team.points
    if points is None:
        team.points = ','.join([str(i) for i in[tag_id]))
    else:
        points = (team.points.split(","))
        if tag_id in points:
            return jsonify({
                "message": "tag already found"
            })
        points.append(tag_id)
        team.points =  ','.join([str(i) for i in points])
    db.session.commit()
    return jsonify({
        "message": "tag added"
    })


@app.route('/teamscore/<string:team_id>', methods=['GET'])
def get_team_scores(team_id):
    app.logger.info("team id requested", team_id)
    team = Team.query.get(id=team_id)
    points = team.points
    if points is None:
        return jsonify(
            {
                "hints_id": []
            }
        )
    return jsonify(
        {
            "hints_id": toInt(team.points.split(","))
        }
    )


def createTag(hints, tags):
    tagid = []
    for i in range(0, len(hints)):
        tag = Tag(hint=hints[i], tag=tags[i])
        db.session.add(tag)
        db.session.flush()
        tagid.append(tag.id)
    db.session.commit()
    return tagid


def createTeam(teamnames, colours):
    id = []
    for i in range(0, len(teamnames)):
        team = Team(name=teamnames[i], colour=colours[i], points=None)
        db.session.add(team)
        db.session.flush()
        id.append(team.id)
    db.session.commit()
    return id


def toInt(input):
    out = []
    for i in input:
        out.append(int(i))
    return out


def getHintAndTag(ids):
    hints = []
    tags = []
    for id in ids:
        tag = Tag.query.get(id)
        tags.append(tag.tag)
        hints.append(tag.hint)
    return hints, tags


if __name__ == '__main__':
    app.run()
"""
#this method is being called in and registers a user to the database.
@app.route('/createclient', methods=['POST'])
def addUserToDB():
    content = request.get_json()
    app.logger.info('%s Request made', content)
    username = content['Username']
    password = content['password']
    email = content['email']

    myuser = User(username=username,
                  admin=0,
                  password=password,
                  email=email)
    db.session.add(myuser)
    db.session.commit()
    return "user created"


#{
#    "gameId": id,
#    "tags": [tag0, tag1,...]
#}
# You need all the tags to get the next hint if the hint are not in order it will be a bad hint message
# If this is the last tag it will return congradulations since the game is over
@app.route('/gethint', methods=['POST'])
def getGameNextHint():
    content = request.get_json()
    app.logger.info("Request made", content)

    try:
        gameid = content["gameId"]
        tags = content["tags"]
        thetag = Tag.query.filter_by(GameId=gameid).first()
        thetag.setTags()
        game = Game.query.filter_by(id=gameid).first()
        game.setHints()
    except (ValueError, KeyError, TypeError) as error:
        raise InvalidUsage("JSON format error", status_code=400)

    for tag in tags:
        if thetag.notLastNext():
            if str(tag) != str(thetag.next()):
                return jsonify(
                    {
                        "hint": "BAD_TAG"
                    })
        else:
            if str(tag) != str(thetag.next()):
                return jsonify(
                    {
                        "hint": "BAD_TAG"
                    })
            return jsonify(
                {
                    "hint": "CONGRADULATION"
                })

    return jsonify(
        {
            "hint": game.hints[thetag.counter]
        })


#returns the first hint and tag of a game
@app.route('/getfirsthint/<int:game_id>', methods=['GET'])
def getGameFirstHint(game_id):
    g = Game.query.filter_by(id=game_id).first()
    g.setHints()

    json = jsonify(
        {
            "hint": str(g.hints[0])
        }
    )
    return json


#tested
@app.route('/find_game/<int:gamet_id>', methods=['GET'])
def searchGamerById(game_id):
    g = Game.query.filter_by(id=game_id).first()
    t = Tag.query.filter_by(GameId=game_id).first()
    g.setHints()
    t.setTags()
    json = jsonify(
                    {
                        "name": str(g.Name),
                        "hints": str(g.hints),
                        "tags": str(t.tags)
                    }
                   )
    return json


@app.route('/add_game', methods=['POST'])
def addGame():
    content = request.get_json()
    name = content['name']
    hint0 = content['hint0']
    hint1 = content['hint1']

    mygame = Game(name=name,
                  hint0=hint0,
                  hint1=hint1)
    db.session.add(mygame)
    db.session.commit()
    return "game created"
"""
