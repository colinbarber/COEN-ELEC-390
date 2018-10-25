from TAGit._init_ import db

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