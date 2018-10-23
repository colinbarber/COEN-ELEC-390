from TAGit._init_ import db, bcript

# this is a python reprisentation of the user table in the database
class User(db.Model):
    __tablename__ = 'Users'

    id = db.Column(db.Integer, primary_key=True)
    FirstName = db.Column(db.String(64), unique=False)
    LastName = db.Column(db.String(64), unique=False)
    Admin = db.Column(db.SmallInteger, unique=False)
    Password = db.Column(db.String(64), unique=False)
    Email = db.Column(db.String(120), unique=False)

    def __init__(self, email, firstname, lastname, admin, password):
        self.FirstName = firstname
        self.LastName = lastname
        self.Admin = admin
        #hash function with salt using sha2
        self.Password = bcrypt.generate_password_hash(password)
        self.Email = email

    #returns true if the hashed password match
    def check_password(self, password):
        return bcrypt.check_password_hash(self.password, password)

    def __repr__(self):
        return '<User %r>' % (self.Email)