import traceback
from flask import jsonify
from jsonschema import ValidationError
from werkzeug.exceptions import HTTPException, BadRequest
from TAGit.__init__ import app


class InvalidUsage(Exception):
    status_code = 400

    def __init__(self, message, status_code=None, payload=None):
        Exception.__init__(self)
        self.message = message
        if status_code is not None:
            self.status_code = status_code
        self.payload = payload

    def to_dict(self):
        rv = dict(self.payload or ())
        rv['message'] = self.message
        rv['code'] = self.status_code
        return rv


@app.errorhandler(InvalidUsage)
def handle_invalid_usage(error):
    app.logger.error(traceback.format_exc())
    app.logger.error("{0} | Code: {1} ".format(error.message, error.status_code))
    response = jsonify(error.to_dict())
    response.status_code = error.status_code
    return response


@app.errorhandler(ValidationError)
def handle_bad_validation(error):
    app.logger.error(traceback.format_exc())
    app.logger.error("{0} | Code: {1}".format(error.message, 404))
    response = jsonify({
        "message": "Internal Server Error 💩"
    })
    response.status_code = 500
    return response


@app.errorhandler(HTTPException)
def handle_bad_request(error):
    if isinstance(error, HTTPException) or isinstance(error, BadRequest):
        app.logger.error(traceback.format_exc())
        app.logger.error("{0} | Code: {1}".format(error.description, error.code))
    else:
        app.logger.error("Not an HTTPException | Code: " + str(500))
    response = jsonify({
        "message": "Internal Server Error 💩"
        })
    response.status_code = 500
    return response

