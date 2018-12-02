
from app import db
from TAGit.models.games import Game
from TAGit.models.tags import Tag
from TAGit.models.teams import Team


try:
    game = Game.query.get(-1)
    db.session.delete(game)
    db.session.commit()
    del game
except Exception as e:
    print("good " + str(e))

try:
    game = Game.query.get(1)
    db.session.delete(game)
    db.session.commit()
    del game
except Exception as e:
    print("good " + str(e))

try:
    team0 = Team.query.get(0)
    db.session.delete(team0)
    db.session.commit()
    del team0
except Exception as e:
    print("good " + str(e))

try:
    team1 = Team.query.get(1)
    db.session.delete(team1)
    db.session.commit()
    del team1
except Exception as e:
    print("good " + str(e))

try:
    team2 = Team.query.get(2)
    db.session.delete(team2)
    db.session.commit()
    del team2
except Exception as e:
    print("good " + str(e))

try:
    hint0 = Tag.query.get(0)
    db.session.delete(hint0)
    db.session.commit()
    del hint0
except Exception as e:
    print("good " + str(e))

try:
    hint1 = Tag.query.get(1)
    db.session.delete(hint1)
    db.session.commit()
    del hint1
except Exception as e:
    print("good " + str(e))

db.session.close()
