from flask import render_template
from flask import request
from TAGit._init_ import app, db
from TAGit.models.games import Game
from flask import jsonify


#not tested yet
@app.route('/', methods=['GET'])
def default():
    return "helloworld"

#this method is being called in clientregi.html and registers a user to the database.
#the link is established from the route this method was given -- it is the same as the html file
@app.route('/createclient/', methods=['POST'])
def addUserToDB():

    firstname = request.form['firstName']
    lastname = request.form['lastName']
    password = request.form['password']
    email = request.form['email']

    myuser = User(firstname=firstname,
                  lastname=lastname,
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

#not tested yet
@app.route('/find/<int:post_id>', methods=['GET'])
def searchUserById(post_id):
    g = Game.query.filter_by(id=post_id).first()
    json = jsonify(
                    {
                        "name": g.name,
                        "questions": [g.question0, g.question1]
                    }
                   )
    return json


if __name__ == '__main__':
    app.run()

