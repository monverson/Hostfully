# Hostfully Technical Task
## Prerequisite to run locally
- You need jdk17 for compiling project. You will need to do mvn clean install and then start main class.
## For testing locally
- Unit and integration tests are written for each different scenario.If you want to test it on postman, the sample request is as follows : 
- For Booking Creation:
- Request URL : localhost:8080/bookings
- Request Body:  {
    "id" : 1,
    "startDate": "2023-07-01",
    "endDate": "2023-07-04"
}
    
