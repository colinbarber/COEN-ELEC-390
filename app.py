from flask import request
from TAGit._init_ import app, db
from TAGit.models.games import Game
from TAGit.models.user import User
from flask import jsonify


#tested
@app.route('/', methods=['GET'])
def default():
    return "helloworld"

#this method is being called in and registers a user to the database.
@app.route('/createclient/', methods=['POST'])
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

#I dont know why I wrote this
@app.route('/deleteTable', methods=['DELETE'])
def deleteTable():
    Game.__table__.drop(db.engine)
    return "DONE"

#tested
@app.route('/find_game/<int:post_id>', methods=['GET'])
def searchGamerById(post_id):
    g = Game.query.filter_by(id=post_id).first()
    json = jsonify(
                    {
                        "name": g.name,
                        "questions": [g.question0, g.question1]
                    }
                   )
    return json

@app.route('/add_game', methods=['POST'])
def addGame():
    content = request.get_json()
    name = content['name']
    question0 = content['question0']
    question1 = content['question1']

    mygame = Game(name=name,
                  question0=question0,
                  question1=question1)
    db.session.add(mygame)
    db.session.commit()
    return "game created"


if __name__ == '__main__':
    app.run()

