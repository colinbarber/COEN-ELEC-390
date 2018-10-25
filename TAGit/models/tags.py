from TAGit._init_ import db
from werkzeug.security import generate_password_hash, \
     check_password_hash

# this is a python reprisentation of the user table in the database
class Tag(db.Model):
    __tablename__ = 'Tags'

    id = db.Column(db.Integer, primary_key=True)
    GameId = db.Column(db.Integer, unique=False)
    Tag0 = db.Column(db.String(64), unique=False)
    Tag1 = db.Column(db.String(64), unique=False)
    Tag2 = db.Column(db.String(64), unique=False)

    counter = 0
    tags = 0

    def setTags(self):
        if self.Tag0 is None:
            self.tags = []
        elif self.Tag1 is None:
            self.tags = [self.Tag0]
        elif self.Tag2 is None:
            self.tags = [self.Tag0, self.Tag1]

    def __init__(self, gameid, tag0, tag1, tag2):
        self.GameId = gameid
        self.Tag0 = tag0
        self.Tag1 = tag1
        self.Tag2 = tag2

    def next(self):
        self.counter = self.counter+1
        return self.tags[self.counter-1]

    def notLastNext(self):
        if self.counter == len(self.tags)-1:
            return False
        return True

    def getCount(self):
        return self.counter-1

    def __repr__(self):
        return '<User %r>' % (self.Email)