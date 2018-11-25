from TAGit.__init__ import db
from TAGit.models.tags import Tag
from TAGit.models.teams import Team


# Turn and array of integers to a comma separated string
def to_comma_separated_str(my_list):
    return ','.join([str(i) for i in my_list])


# Takes a list containing team ids and returns a toggle containing a list of their name and colour
def get_team(team_ids):
    team_name = []
    team_colour = []
    for ti in team_ids:
        team = Team.query.get(ti)
        team_name.append(team.name)
        team_colour.append(team.colour)
    return team_name, team_colour


# Takes a list of hints and a list of tags creates the tags in the database and returns a list of their ids
def create_tag(hints):
    tag_id = []
    for hint in hints:
        tag = Tag(hint=hint)
        db.session.add(tag)
        db.session.flush()
        tag_id.append(tag.id)
    db.session.commit()
    return tag_id


# Takes a list of team names and a list of colours creates the teams in the database and returns a list of their ids
def create_team(team_names, colours):
    team_id = []
    for i in range(0, len(team_names)):
        team = Team(name=team_names[i], colour=colours[i], points=None)
        db.session.add(team)
        db.session.flush()
        team_id.append(team.id)
    db.session.commit()
    return team_id


# takes a list of string that are integers and returns a list of integers
def to_int(my_list):
    out = []
    for i in my_list:
        out.append(int(i))
    return out


# takes in a list of tag ids and returns a toggle containing a list of their hints and a list of their tags
def get_tag(ids):
    hints = []
    for i in ids:
        tag = Tag.query.get(i)
        hints.append(tag.hint)
    return hints


# takes a list of team ids and returns there corresponding score
def get_score_from_team_id(team_ids):
    scores = []
    for team_id in team_ids:
        team = Team.query.get(team_id)
        points = team.points
        if points is None:
            scores.append(0)
        else:
            hints_found = to_int(points.split(","))
            scores.append(len(hints_found))
    return scores

