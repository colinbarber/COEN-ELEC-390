from flask_wtf import FlaskForm
from wtforms import StringField, SubmitField, IntegerField, PasswordField, SelectField, FieldList, DateTimeField
from wtforms.validators import DataRequired, Length, Email, ValidationError, NumberRange
message = "Integer between 1 and 10 required"


class GameForm(FlaskForm):
    name = StringField('Name', validators=[DataRequired(), Length(min=1, max=255)])
    username = StringField('UserName', validators=[DataRequired(), Length(min=1, max=255)])
    endtime = DateTimeField('End Time', format='%Y-%m-%d %H:%M:%S')
    teams = FieldList(StringField('Teams', validators=[DataRequired(), Length(min=1, max=255)]), min_entries=1, max_entries=1000)
    colours = FieldList(StringField('Colours', validators=[DataRequired(), Length(min=1, max=255)]), min_entries=1, max_entries=1000)
    hints = FieldList(StringField('Hints', validators=[DataRequired(), Length(min=1, max=255)]), min_entries=1, max_entries=1000)
    submit = SubmitField('Create')
