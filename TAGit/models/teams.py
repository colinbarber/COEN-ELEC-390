from TAGit._init_ import db
from werkzeug.security import generate_password_hash, \
     check_password_hash


# this is a python reprisentation of the user table in the database
class Team(db.Model):
    __tablename__ = 'Team'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), unique=False)
    colour = db.Column(db.String(64), unique=False)
    points = db.Column(db.String(64), unique=False)

    def __init__(self, name, colour, points):
        self.name = name
        self.colour = colour
        self.points = points

    def __repr__(self):
        return '<Team %r>' % (self.name)