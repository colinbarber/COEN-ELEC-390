from TAGit._init_ import db

# this is a python reprisentation of the user table in the database
class Game(db.Model):
    __tablename__ = 'Game'

    id = db.Column(db.Integer, primary_key=True)
    name = question0 = db.Column(db.String(64), unique=False)
    question0 = db.Column(db.String(512), unique=False)
    question1 = db.Column(db.String(512), unique=False)

    def __init__(self, name, question0, question1):
        self.name = name
        self.question0 = question0
        self.question1 = question1

    def __repr__(self):
        return '<Game %r>' % (self.name)