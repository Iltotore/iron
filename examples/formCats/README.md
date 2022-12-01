# Minimalist form/API using Iron and Cats

This is a simple web API built using Iron combined with Cats, Circe and Http4s.

It receives a user via a `POST` request to `localhost/register`.

Example request:

```json
{
    "name": "totore",
    "password": "abc123",
    "age": 18
}
```

The passed fields must satisfy the following constraints:
- Name should be alphanumeric and have a length between 3 and 10
- Password should contain at least a letter, a digit and have a length between 6 and 20
- Age should be strictly positive

If a or multiple invalid values are passed, the response will look like this:
```json
HTTP/1.1 400 Bad Request
Date: Wed, 23 Nov 2022 07:27:06 GMT
Connection: keep-alive
Content-Type: application/json
Content-Length: 133

{
  "messages": [
    "Password must contain atleast a letter, a digit and have a length between 6 and 20",
    "Age should be strictly positive"
  ]
}
```

## Run the example

Use the following command to run the example:

```sh
mill examples.formCats.run
```

Note: this command must be run in the Iron root directory.