from flask import request
from TAGit._init_ import app, db
from TAGit.models.games import Game
from TAGit.models.user import User
from TAGit.models.tags import Tag
from flask import jsonify


#tested
@app.route('/', methods=['GET'])
def default():
    return "helloworld"


#this method is being called in and registers a user to the database.
@app.route('/createclient', methods=['POST'])
def addUserToDB():
    content = request.get_json()
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
# If this is the last tag it will return congratulations since the game is over
@app.route('/gethint', methods=['POST'])
def getGameNextHint():
    content = request.get_json()
    gameid = content['gameId']
    tags = content['tags']

    thetag = Tag.query.filter_by(GameId=gameid).first()
    thetag.setTags()
    game = Game.query.filter_by(id=gameid).first()
    game.setHints()

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
                    "hint": "CONGRATULATIONS! YOU WIN"
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
            "hint": str(g.hints[0]),
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


if __name__ == '__main__':
    app.run()

