create_game_schema = {
    "definitions": {},
    "type": "object",
    "required": [
        "name",
        "username",
        "endtime",
        "teams",
        "colours",
        "hints",
        "tags"
    ],
    "properties": {
        "name": {
            "type": "string",
            "default": "",
            "examples": [
                "bitch"
            ]
        },
        "username": {
            "type": "string",
            "default": "",
            "examples": [
                "jn"
            ]
        },
        "endtime": {
            "type": "number",
            "default": "",
            "examples": [
                1514825676008
            ]
        },
        "teams": {
            "type": "array",
            "items": {
                "type": "string",
                "default": "",
                "examples": [
                    "team1",
                    "team2",
                    "team3"
                ]
            }
        },
        "colours": {
            "type": "array",
            "items": {
                "type": "string",
                "default": "",
                "examples": [
                    "color1",
                    "color2",
                    "color3"
                ]
            }
        },
        "hints": {
            "type": "array",
            "items": {
                "$id": "#/properties/hints/items",
                "type": "string",
                "title": "The Items Schema",
                "default": "",
                "examples": [
                    "EV basment",
                    "ECA office",
                    "Roof of hall"
                ]
            }
        },
        "tags": {
            "type": "array",
            "items": {
                "type": "string",
                "default": "",
                "examples": [
                    "1h3w4",
                    "3h4iw8",
                    "3h4j5"
                ]
            }
        }
    }
}
