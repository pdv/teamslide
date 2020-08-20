# teamslide

### Endpoints

#### `POST /league`

Inserts a new league into the database

Parameters (form url-encoded)
- name : string
- price : int
- location : comma-separated lat/lon

Example
```
curl -d 'name=tigers&price=20&location=-19.0,19.0' localhost:8080/league
```

#### `GET /search`

Finds the largest subset of teams within a given radius with a total
price less than the provided budget. Because leagues are weighted
equally, this will always return the cheapest leagues in a given area.

Returns json-encoded list, possibly empty

Parameters (url query)
- location : comma-separated lat/lon
- radius : int, in miles
- budget : int

Example
```
curl 'localhost:8080/search?location=19.1,19.1&radius=10000&budget=200'
```

### Operation

```
PORT=8080
DATABASE_URL='postgreseql://your.postgres/database'
java $JVM_OPTS -jar target/teamslide-standalone.jar
```

### Dev Setup

#### Requirements

- Postgres / [Postgis](https://postgis.net/)
- Clojure / [Leiningin](https://leiningen.org/)

#### Commands

```
lein run
```
Run the dev server

```
lein test
```
Run tests

```
lein uberjar
```
Build a production jar

