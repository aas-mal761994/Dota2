# Bayes Java Dota Challlenge

This is the [task](TASK.md).

Any additional information about your solution goes here.

#Assumptions
* Timestamp unit is millisecond.

#Decisions
* `MatchEventParserTest.java` contains just test code to exemplify how to test the event parsing logic for all 4 mentioned events.
* I added properties to `application.yml` to facilitate the development: `spring.jpa.show-sql: true` and `spring.jpa.properties.hibernate.format_sql: true`.
* I avoided adding more classes then necessary, specifically for the values returned from the repository methods. Instead of returning `List<Map<String, Object>>` I could have moved `rest.model` classes to another package and use these classes as DTOs to transport values between layers. That could be a strategy to avoid unecessary data transformation (e.g.: `Tuple` &rarr; `Map`&rarr; model).
* I didn't focus on performance, although I understand that it can be important in cases where the data combat log are huge and the processing has to be performed under defined thresholds.
* I didn't write code for exception handling, response handling (e.g.: translate exceptions to proper HTTP responses), log, monitoring. 
  These are all important aspects to take into account to put a service in production.
* I have also enabled swagger ui for this application, which is accessible at  http://localhost:8080/swagger-ui/#/match-controller
  


