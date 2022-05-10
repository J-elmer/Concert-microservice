# Concert-microservice
This is the concert microservice which is part of ReviewStars. To launch the complete application, see the ReviewStars repository.

To review this code, you can pull the repository locally.

**Installation**

*Prerequisites*:
Make sure you have Docker installed and can run Java applications.

*Steps*:

Navigate to the project directory and run

```
docker-compose up -d in your terminal
```

Once the containers are running, start the project locally (through IntelliJ for example).

You can test through postman. The application is configured to run on port 9090, so be sure to send requests to that port or change it in the application.properties file.

Note that for the whole functionallity to work, you should also make sure you run the performer and review microservice, otherwise creating, updating and deleting concerts will not work as expected.

