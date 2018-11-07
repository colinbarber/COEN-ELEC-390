from TAGit._init_ import db


# this is a python representation of the Team table in the database
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
        return '<Team %r>' % self.name
