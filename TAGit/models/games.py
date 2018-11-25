from TAGit.__init__ import db


# this is a python representation of the Game table in the database
class Game(db.Model):
    __tablename__ = 'Game'

    id = db.Column(db.Integer, primary_key=True)
    user_name = db.Column(db.String(64), unique=False)
    team_ids = db.Column(db.TEXT, unique=False)
    tag_ids = db.Column(db.TEXT, unique=False)
    time_end = db.Column(db.BigInteger, unique=False)
    name = db.Column(db.String(64), unique=True)

    # TODO the name must be unique
    def __init__(self, name, user_name, team_ids, tag_ids, time_end):
        self.user_name = user_name
        self.team_ids = team_ids
        self.tag_ids = tag_ids
        self.time_end = time_end
        self.name = name

    def __repr__(self):
        return '<Game %r>' % self.name
