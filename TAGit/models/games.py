from TAGit._init_ import db


# this is a python reprisentation of the user table in the database
class Game(db.Model):
    __tablename__ = 'Game'

    id = db.Column(db.Integer, primary_key=True)
    user_name = db.Column(db.String(64), unique=False)
    team_ids = db.Column(db.String(255), unique=False)
    tag_ids = db.Column(db.String(255), unique=False)
    time_end = db.Column(db.DateTime(timezone=True))
    name = db.Column(db.String(64), unique=True)

    # TODO the name must be unique
    def __init__(self, name, user_name, team_ids, tag_ids, time_end):
        self.user_name = user_name
        self.team_ids = team_ids
        self.tag_ids = tag_ids
        self.time_end = time_end
        self.name = name

    def __repr__(self):
        return '<Game %r>' % (self.name)
"""
# this is a python reprisentation of the user table in the database
class Game(db.Model):
    __tablename__ = 'Game'

    id = db.Column(db.Integer, primary_key=True)
    Name = db.Column(db.String(64), unique=False)
    Hint0 = db.Column(db.String(512), unique=False)
    Hint1 = db.Column(db.String(512), unique=False)
    Hint2 = db.Column(db.String(512), unique=False)

    hints = [Hint0, Hint1, Hint2]

    def setHints(self):
        if self.Hint0 is None:
            self.hints = []
        elif self.Hint1 is None:
            self.hints = [self.Hint0]
        elif self.Hint2 is None:
            self.hints = [self.Hint0, self.Hint1]
        else:
            self.hints = [self.Hint0, self.Hint1, self.Hint2]


    def __init__(self, name, hint0, hint1, hint2):
        self.Name = name
        self.Hint0 = hint0
        self.Hint1 = hint1
        self.Hint2 = hint2

    def __repr__(self):
        return '<Game %r>' % (self.Name)
"""