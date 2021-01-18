{
  "swagger": "2.0",
  "info": {
    "description": "This is a API for dogs adoption.",
    "version": "1.0.0",
    "title": "Dogs adoption API - auaupi",
    "contact": {
      "email": "vitor.portela@paygo.com.br"
    },
    "license": {
      "name": "Apache 2.0",
      "url": "http://www.apache.org/licenses/LICENSE-2.0.html"
    }
  },
  "host": "virtserver.swaggerhub.com",
  "basePath": "/vitor-marques/auaupi-teste/1.0.0",
  "schemes": [
    "https"
  ],
  "paths": {
    "/dogs": {
      "get": {
        "summary": "shows all dogs available for adoption",
        "description": "Shows all dogs available for adoption with resumed informations and a link with an image of him\n",
        "operationId": "showDogs",
        "produces": [
          "application/json"
        ],
        "parameters": [
          {
            "name": "specificDog",
            "in": "query",
            "description": "pass an optional id to search for a specific dog, providing detailed information",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "search results matching criteria",
            "schema": {
              "type": "array",
              "items": {
                "$ref": "#/definitions/Dogs"
              }
            }
          },
          "400": {
            "description": "bad input parameter"
          }
        }
      },
      "post": {
        "summary": "adds an dog",
        "description": "Adds an dog to the list of dogs available",
        "operationId": "addDog",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "application/json"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "RegisterDog",
            "description": "New dog",
            "required": false,
            "schema": {
              "$ref": "#/definitions/Dogs"
            }
          },
          {
            "name": "adoptDog",
            "in": "query",
            "description": "pass an optional id to adopt a dog",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "201": {
            "description": "item created"
          },
          "400": {
            "description": "invalid input, object invalid"
          },
          "409": {
            "description": "an existing item already exists"
          }
        }
      }
    }
  },
  "definitions": {
    "Dogs": {
      "type": "object",
      "required": [
        "breed",
        "castrated?",
        "gender",
        "id",
        "image",
        "name",
        "port"
      ],
      "properties": {
        "id": {
          "type": "integer",
          "example": 42
        },
        "breed": {
          "type": "string",
          "example": "Husky"
        },
        "name": {
          "type": "string",
          "example": "Lucky"
        },
        "image": {
          "type": "string",
          "example": "https://images.dog.ceo/breeds/husky/n02110185_10175.jpg"
        },
        "gender": {
          "type": "string",
          "format": "m/f",
          "example": "m"
        },
        "port": {
          "type": "string",
          "format": "p/m/g",
          "example": "p"
        },
        "bitrh": {
          "type": "string",
          "format": "yyyy-mm-dd",
          "example": "2018-05-19T00:00:00.000+0000"
        },
        "castrated?": {
          "type": "boolean",
          "example": true
        }
      }
    }
  }
}