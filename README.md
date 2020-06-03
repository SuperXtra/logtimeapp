## Log Time App

### Environment

// TODO

### Running

To run application

```
sbt run
```

With service running you can send HTTP requests:

######User
```
curl --location --request POST 'localhost:8080/user/register'
```

```
curl --location --request POST 'localhost:8080/user/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userUUID": "???"
}'
```

######Project
```
curl --location --request POST 'localhost:8080/project' \
--header 'Authorization: Bearer ???' \
--header 'Content-Type: application/json' \
--data-raw '{
"projectName": "sample project name"
}'
```

```
curl --location --request PUT 'localhost:8080/project' \
--header 'Authorization: Bearer ???' \
--header 'Content-Type: application/json' \
--data-raw '{
"oldProjectName": "sample project name",
"projectName": "sample project name to update"
}
```

```
curl --location --request DELETE 'localhost:8080/project' \
--header 'Authorization: Bearer ???' \
--header 'Content-Type: application/json' \
--data-raw '{
"projectName": "sample project name to update"
}'

```

#####Task

```
curl --location --request POST 'localhost:8080/task' \
--header 'Authorization: Bearer ???' \
--header 'Content-Type: application/json' \
--data-raw '{
"projectName": "sample project name",
"taskDescription": "sample task description",
"startTime": "2020-01-15T04:00:00+02:00",
"durationTime": 500,
"volume": 5
}'
```

```
curl --location --request PUT 'localhost:8080/task' \
--header 'Authorization: Bearer ???' \
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


```
curl --location --request DELETE 'localhost:8080/task' \
--header 'Authorization: ???' \
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