
import unittest

import requests
from app import db
from TAGit.models.games import Game
from TAGit.models.tags import Tag
from TAGit.models.teams import Team


# *********** THE APPLICATION MUST BE UP AND RUNNING ON THE LOCALHOST PORT 5000 ************
# *** RUN THE RESET.PY FILE IN ORDER TO RESET THE DATABASE FOR THE UNIT TESTS IF SOMETHING WENT HORRIBLY WRONG ***
# *********** THE DATABASE CAN NOT CONTAIN GAME WITH ID= 1 OR -1, TEAM ID= 1,2 OR 3, HINT ID=0 OR 1 **********

class TestFlask(unittest.TestCase):

    def test_get_game(self):
        print("- Test the endpoint returning a game \n")
        game = Game(name="unittest", user_name="unittest", team_ids="0,1", tag_ids="0,1", time_end=12345)
        game.id = -1
        team0 = Team(name="team1", colour="color1", points=None)
        team0.id = 0
        team1 = Team(name="team2", colour="color2", points=None)
        team1.id = 1
        hint0 = Tag(hint="EV basement")
        hint0.id = 0
        hint1 = Tag(hint="ECA office")
        hint1.id = 1

        db.session.add(game)
        db.session.add(team1)
        db.session.add(team0)
        db.session.add(hint0)
        db.session.add(hint1)
        db.session.commit()

        try:
            response1 = requests.get('http://localhost:5000/game/unittest')

            self.assertEqual(response1.json(), dict(end_time=12345, game_id=-1, game_owner="unittest", hints=[
                "EV basement",
                "ECA office"
            ], tag_ids=[
                0,
                1
            ], team_colours=[
                "color1",
                "color2"
                ], team_ids=[
                0,
                1
            ], team_names=[
                "team1",
                "team2"
            ]))
        except Exception as e:
            raise e
        finally:
            db.session.delete(game)
            db.session.delete(team1)
            db.session.delete(team0)
            db.session.delete(hint0)
            db.session.delete(hint1)
            db.session.commit()
            del game
            del team0
            del team1
            del hint0
            del hint1

    def test_get_team_points(self):
        print("- Test the endpoint returning team score and adding a found hint to this teams points \n")
        game = Game(name="unittest", user_name="unittest", team_ids="0,1", tag_ids="0,1", time_end=12345)
        game.id = -1
        team0 = Team(name="team1", colour="color1", points=None)
        team0.id = 0
        team1 = Team(name="team2", colour="color2", points=None)
        team1.id = 1
        hint0 = Tag(hint="EV basement")
        hint0.id = 0
        hint1 = Tag(hint="ECA office")
        hint1.id = 1

        db.session.add(game)
        db.session.add(team1)
        db.session.add(team0)
        db.session.add(hint0)
        db.session.add(hint1)
        db.session.commit()

        try:
            response2 = requests.get('http://localhost:5000/team_score/0')
            self.assertEqual(response2.json(), dict(hints_id=[]))

            response3 = requests.post('http://localhost:5000/hint/0/0')
            self.assertEqual(response3.json(), dict(message="tag added"))

            response4 = requests.post('http://localhost:5000/hint/0/0')
            self.assertEqual(response4.json(), dict(message="tag already found"))

        except Exception as e:
            raise e
        finally:
            db.session.delete(game)
            db.session.delete(team1)
            db.session.delete(team0)
            db.session.delete(hint0)
            db.session.delete(hint1)
            db.session.commit()
            del game
            del team0
            del team1
            del hint0
            del hint1

    def test_get_top_3_team3(self):
        print("- Test the endpoint returning top 3 teams when there is only one team in the game \n")
        game = Game(name="unittest", user_name="unittest", team_ids="0", tag_ids="0,1", time_end=12345)
        game.id = 1
        team0 = Team(name="team1", colour="color1", points="0")
        team0.id = 0
        hint0 = Tag(hint="EV basement")
        hint0.id = 0
        hint1 = Tag(hint="ECA office")
        hint1.id = 1

        db.session.add(game)
        db.session.add(team0)
        db.session.add(hint0)
        db.session.add(hint1)
        db.session.commit()

        try:
            response = requests.get('http://localhost:5000/game_top_three/1')
            self.assertEqual(response.json(), dict(winner_ids=[0], team_score=[1]))
        except Exception as e:
            raise e
        finally:
            db.session.delete(game)
            db.session.delete(team0)
            db.session.delete(hint0)
            db.session.delete(hint1)
            db.session.commit()
            del game
            del team0
            del hint0
            del hint1

    def test_get_team_score(self):
        print("- Test the endpoint returning top 3 teams when there is 2 teams in the game \n")
        game = Game(name="unittest", user_name="unittest", team_ids="0,1", tag_ids="0,1", time_end=12345)
        game.id = 1
        team0 = Team(name="team1", colour="color1", points="0")
        team0.id = 0
        team1 = Team(name="team2", colour="color2", points="0,1")
        team1.id = 1
        hint0 = Tag(hint="EV basement")
        hint0.id = 0
        hint1 = Tag(hint="ECA office")
        hint1.id = 1

        db.session.add(game)
        db.session.add(team1)
        db.session.add(team0)
        db.session.add(hint0)
        db.session.add(hint1)
        db.session.commit()

        try:
            response = requests.get('http://localhost:5000/game_teams_scores/1')
            self.assertEqual(response.json(), dict(team_ids=[0, 1], team_scores=[1, 2]))

        except Exception as e:
            raise e
        finally:
            db.session.delete(game)
            db.session.delete(team1)
            db.session.delete(team0)
            db.session.delete(hint0)
            db.session.delete(hint1)
            db.session.commit()
            del game
            del team0
            del team1
            del hint0
            del hint1

    def test_get_top_3_team2(self):
        print("- Test the endpoint returning top 3 teams when there is 3 teams in the game \n")
        game = Game(name="unittest", user_name="unittest", team_ids="0,1,2", tag_ids="0,1", time_end=12345)
        game.id = 1
        team0 = Team(name="team1", colour="color1", points="0")
        team0.id = 0
        team1 = Team(name="team2", colour="color2", points="0,1")
        team1.id = 1
        team2 = Team(name="team3", colour="color3", points=None)
        team2.id = 2
        hint0 = Tag(hint="EV basement")
        hint0.id = 0
        hint1 = Tag(hint="ECA office")
        hint1.id = 1

        db.session.add(game)
        db.session.add(team2)
        db.session.add(team1)
        db.session.add(team0)
        db.session.add(hint0)
        db.session.add(hint1)
        db.session.commit()

        try:
            response = requests.get('http://localhost:5000/game_top_three/1')
            self.assertEqual(response.json(), dict(winner_ids=[1, 0, 2], team_score=[2, 1, 0]))

        except Exception as e:
            raise e
        finally:
            db.session.delete(game)
            db.session.delete(team1)
            db.session.delete(team0)
            db.session.delete(team2)
            db.session.delete(hint0)
            db.session.delete(hint1)
            db.session.commit()
            del game
            del team0
            del team2
            del team1
            del hint0
            del hint1


if __name__ == "__main__":
    unittest.main()
