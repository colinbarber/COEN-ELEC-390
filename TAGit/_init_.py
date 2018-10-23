import os
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import exc
from config import LOCAL_DATABASE_URI

app = Flask(__name__)
# sets up the connection to the database
#app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('DATABASE_URL')
app.config['SQLALCHEMY_DATABASE_URI'] = LOCAL_DATABASE_URI
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False


db = SQLAlchemy(app)
