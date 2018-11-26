from datetime import datetime

from flask_wtf import FlaskForm, validators
from wtforms import StringField, SubmitField, IntegerField, PasswordField, SelectField, FieldList, DateTimeField, Field
from wtforms.validators import DataRequired, Length, Email, ValidationError, NumberRange

message = "Integer between 1 and 10 required"


class GameForm(FlaskForm):
    name = StringField('Name', validators=[DataRequired(), Length(min=1, max=255)])
    username = StringField('UserName', validators=[DataRequired(), Length(min=1, max=255)])
    endtime = DateTimeField('End Time', validators=[DataRequired()], format="%Y-%m-%d %H:%M")
    teams = StringField('Teams', validators=[DataRequired(), Length(min=1, max=255)])
    colours = StringField('Colours', validators=[DataRequired(), Length(min=1, max=255)])
    hints = StringField('Hints', validators=[DataRequired(), Length(min=1, max=255)])
    submit = SubmitField('Create')
