## Log Time App

### Environment
```
Application uses heroku instance of postgresql - for development purposes

It is possible to play locally but to do so you need to create new postgresql, create db schema using 
dbSchema.sql file from configuration and change address of db in config file.
```
```
Application runs on port: 9000
```

```
There are three configuration files:
1. Security configuration to set up security algorithm and application secret key (auth-configuration.conf)
2. Database configuration to set up connection to database (database-configuration.conf)
3. Application vonfiguration to set up port it will run on (application.conf)
```

```
For integration testing there is needed installed Docker.
```


### Running

To run application

```
sbt run
```

With service running you can send HTTP requests:

######User
###### create user
```
curl --location --request POST 'localhost:9000/user/register'
```
###### authenticate user
```
curl --location --request POST 'localhost:9000/user/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userUUID": "df0f9d0e-0d22-4e13-b3d4-f6146ba4f949"
}'
```

##### Project
###### create project
```
curl --location --request POST 'localhost:9000/project' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTQ4N2FmMjktNjE0OS00YjY2LWIxYWUtNzIwN2Q1ZDBkMzBmIiwiZXhwaXJlZEF0IjoxNTkxMjkzNzU0MzU1fQ.2dSak_uJTKTjmDlclW-JWwZZgvECsNw28ghpJctbxA4' \
--header 'Content-Type: application/json' \
--data-raw '{
"projectName": "sample project name"
}'
```
###### update project name
```
curl --location --request PUT 'localhost:9000/project' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTQ4N2FmMjktNjE0OS00YjY2LWIxYWUtNzIwN2Q1ZDBkMzBmIiwiZXhwaXJlZEF0IjoxNTkxMjkzNzU0MzU1fQ.2dSak_uJTKTjmDlclW-JWwZZgvECsNw28ghpJctbxA4' \
--header 'Content-Type: application/json' \
--data-raw '{
"oldProjectName": "sample project name",
"projectName": "sample project name to update"
}
```
###### deactivate project
```
curl --location --request DELETE 'localhost:9000/project' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTQ4N2FmMjktNjE0OS00YjY2LWIxYWUtNzIwN2Q1ZDBkMzBmIiwiZXhwaXJlZEF0IjoxNTkxMjkzNzU0MzU1fQ.2dSak_uJTKTjmDlclW-JWwZZgvECsNw28ghpJctbxA4' \
--header 'Content-Type: application/json' \
--data-raw '{
"projectName": "sample project name to update"
}'

```

####Task
###### create task
```
curl --location --request POST 'localhost:9000/task' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTQ4N2FmMjktNjE0OS00YjY2LWIxYWUtNzIwN2Q1ZDBkMzBmIiwiZXhwaXJlZEF0IjoxNTkxMjkzNzU0MzU1fQ.2dSak_uJTKTjmDlclW-JWwZZgvECsNw28ghpJctbxA4' \
--header 'Content-Type: application/json' \
--data-raw '{
"projectName": "sample project name",
"taskDescription": "sample task description",
"startTime": "2020-01-15T04:00:00+02:00",
"durationTime": 500,
"volume": 5
}'
```
###### update task
```
curl --location --request PUT 'localhost:9000/task' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTQ4N2FmMjktNjE0OS00YjY2LWIxYWUtNzIwN2Q1ZDBkMzBmIiwiZXhwaXJlZEF0IjoxNTkxMjkzNzU0MzU1fQ.2dSak_uJTKTjmDlclW-JWwZZgvECsNw28ghpJctbxA4' \
--header 'Content-Type: application/json' \
--data-raw '{
	"oldTaskDescription": "sample task description",
    "newTaskDescription": "sample new task description",
    "startTime": "2020-03-10T14:00:00+02:00",
    "durationTime": 500,
    "volume": 3,
    "comment": "sample but interesting comment"
}'
```
###### deactivate task
```
curl --location --request DELETE 'localhost:9000/task' \
--header 'Authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1dWlkIjoiMTQ4N2FmMjktNjE0OS00YjY2LWIxYWUtNzIwN2Q1ZDBkMzBmIiwiZXhwaXJlZEF0IjoxNTkxMjkzNzU0MzU1fQ.2dSak_uJTKTjmDlclW-JWwZZgvECsNw28ghpJctbxA4' \
--header 'Content-Type: application/json' \
--data-raw '{
	"taskDescription": "sample new task description", 
	"projectName": "sample project name to update"
}'
```
### Testing

To run unit/router tests:

```
sbt test
```
To run integration tests:

```
sbt it:test
```
------------------------------------------------------

###Database schema
![schema](dbSchema.jpg)