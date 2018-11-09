from TAGit._init_ import db


# this is a python representation of the Tag table in the database
class Tag(db.Model):
    __tablename__ = 'Tag'

    id = db.Column(db.Integer, primary_key=True)
    hint = db.Column(db.String(64), unique=False)

    def __init__(self, hint):
        self.hint = hint

    def __repr__(self):
        return '<Game %r>' % self.id
