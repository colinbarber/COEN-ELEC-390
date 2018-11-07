from flask import request
from TAGit.Exception import InvalidUsage
from flask_expects_json import expects_json
from flask import jsonify

from TAGit._init_ import app
from TAGit.models.games import Game
from TAGit.app_helper import *
from TAGit.schema import create_game_schema


# default route
@app.route('/', methods=['GET'])
def default():
    app.logger.info("IP address: " + str(request.remote_addr))
    return jsonify(
        {
            "message": "hello world"
        })

# returns a json containing the info of this game
@app.route('/game/<string:game_name>', methods=['GET'])
def get_game(game_name):
    app.logger.info("Game name requested" + game_name)
    game = Game.query.filter_by(name=game_name).first()
    if game is None:
        raise InvalidUsage(message='Game not found', status_code=404)
    hint_ids = to_int(game.tag_ids.split(","))
    hint_tag = get_hint_and_tag(hint_ids)
    team_ids = to_int(game.team_ids.split(","))
    team = get_team(game.team_ids.split(","))
    return jsonify(
        {
            "game_id": game.id,
            "tag_ids": hint_ids,
            "hints": hint_tag[0],
            "tags": hint_tag[1],
            "team_ids": team_ids,
            "team_names": team[0],
            "team_colours": team[1]
        })


# returns an array containing the hint_id of the hint that the team has found
@app.route('/team_score/<int:team_id>', methods=['GET'])
def get_team_scores(team_id):
    app.logger.info("team id requested: " + str(team_id))
    team = Team.query.get(team_id)
    points = team.points
    if points is None:
        return jsonify(
            {
                "hints_id": []
            }
        )
    return jsonify(
        {
            "hints_id": to_int(team.points.split(","))
        }
    )


# Puts a new game in the db if the json passed is valid
@app.route('/game', methods=['PUT'])
@expects_json(create_game_schema)
def create_game():
    content = request.get_json()
    app.logger.info("Game name of hints requested", content)
    g_name = content["name"]
    u_name = content["username"]
    end_time = content["endtime"]
    teams = content["teams"]
    colours = content["colours"]
    hints = content["hints"]
    tags = content["tags"]
    team_ids = create_team(teams, colours)
    tag_ids = create_tag(hints, tags)
    game = Game(name=g_name,
                user_name=u_name,
                team_ids=to_comma_separated_str(team_ids),
                tag_ids=to_comma_separated_str(tag_ids),
                time_end=end_time)
    db.session.add(game)
    db.session.commit()
    return jsonify(
        {
            "message": "success"
        })


# Posts the hint that has been found as found by the team
@app.route('/hint/<int:team_id>/<int:tag_id>', methods=['POST'])
def put_team_hint(team_id, tag_id):
    app.logger.info("team id: " + str(team_id))
    app.logger.info("tag id: " + str(tag_id))
    team = Team.query.get(team_id)
    points = team.points
    if points is None:
        team.points = to_comma_separated_str([tag_id])
    else:
        points = to_int(team.points.split(","))
        if tag_id in points:
            return jsonify({
                "message": "tag already found"
            })
        points.append(tag_id)
        team.points = to_comma_separated_str(points)
    db.session.commit()
    return jsonify({
        "message": "tag added"
    })


if __name__ == '__main__':
    app.run()
