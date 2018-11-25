
"""
import unittest
from http.client import HTTPException

import requests
# *********** THE APPLICATION MUST BE UP AND RUNNING ON THE LOCALHOST PORT 500 ************
class TestFlask(unittest.TestCase):

    def test_hello_world(self):
        response = requests.get('http://localhost:5000')
        self.assertEqual(response.json(), {'message': 'hello world'})

    def test_get_game(self):
        response = requests.get('http://localhost:5000/game/bitch')
        self.assertEqual(response.json(), dict(end_time=1514825676008, game_id=18, game_owner="jn", hints=[
            "EV basment",
            "ECA office",
            "Roof of hall"
        ], tag_ids=[
            88,
            89,
            90
        ], team_colours=[
            "color1",
            "color2",
            "color3"
        ], team_ids=[
            86,
            87,
            88
        ], team_names=[
            "team1",
            "team2",
            "team3"
        ]))

    def test_get_game2(self):
        response = requests.get('http://localhost:5000/game/$')
        self.assertRaises(HTTPException)


if __name__ == "__main__":
    unittest.main()

    """
