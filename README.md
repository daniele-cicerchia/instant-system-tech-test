# Java Tech Test for Instant System
#### TEST INSTRUCTIONS
The purpose of the exercise is to: develop a “server” application that exposes a REST API to allow a mobile application, or website, to display the list of nearby parking lots as shown on the attached screen.\
Here, we will use the following data source available in Poitiers to retrieve the list of parking lots: https://data.grandpoitiers.fr/api/... \
The number of parking spaces available in real time can be recovered via: https://data.grandpoitiers.fr/api/...

Instructions:
The application must be able to work in other cities: the URL and format of the parking data may be completely different, however the REST API exposed for the mobile application should not evolve.
The application to be developed as part of the exercise includes only the server part (no need to develop the mobile application).

#### INSTRUCTIONS ISSUES
- application requirements are ambiguous, open to several levels of complexity (e.g. precision of the results, performances required, environment, etc.);
- no provided details on the WS to develop, except from a reference web application screen;
- provided WSs to recover data offer no valid way to match parking details and availability (e.g. a shared identification field); I've used the name field ("nom") but it's a terrible solution.

### FEATURES
- used an R-Tree open source implementation (https://github.com/davidmoten/rtree) which has a search complexity of O(log(n)) on average;
- optimised distance computation by pre-computation of constants;
- parking data WS is thought as a pluggable one, e.g. by swapping a lib dependency; it must just implement the _ParkingDataServiceInterface_;
- _GeoLocationSearch_ works with a collection of objects which extend _GeoLocation_ class, so it may be used for other purpose than parkings;
- parking details are scheduled to be updated every morning at 5 AM, available places to be updated every 5 minutes; scheduling uses read/write locks to prevent concurrency issues;
- validation of input is obtained by Spring validation in API definition;
- logging is with SLF4J compatibility layer;

#### NOTES ON RESOLUTION
- I've used Java 8 because is the version I've most experience with;
- I've spent time to search for the best and quickest approach to geo point search, and I've found R-tree based algorithms offer a valid solution;
- I've followed the approach of defining a web service descriptor with OpenAPI (3.0.3) standard and then building the interfaces from it; check _openapi.yaml_ for details;
- I've used Gson to parse JSON arrays/objects coming from Poitiers parking data WS; unfortunately the default output is raw LinkedTreeMap and had no time to develop a more elegant custom type; as far as I've seen, it's the only issue Sonar found;
- _ParkingSearchService_ is the center of the application logic, which defines the data source for parkings and configures the _GeoLocationSearch_ for parking positions;
- because of the time already spent on development I've added just a test; time for developing didn't offer the possibility for a TDD approach, which could have been good in a small sized project like this.

#### INSTALLATION
1. after cloning the repo, set project language level to Java 8;
2. add code source for openAPi generated classes (target/generated-sources/openapi/src/main/java);
3. run "mvn clean package" to generate API interface and DTO;
4. start Spring Boot application (ParkingsApplication) or build and run the WAR file in target folder (_java -jar parkings-0.0.1-SNAPSHOT.war_);
5. the web service endpoint is available at http://localhost:8080/parking;
6. a small Postman collection is included to easily start testing the web service (_Instant_System_Tech_Test.postman_collection.json_).

#### WOULD HAVE DONE
- these requirements made me immediately think at a multi-application structure:
  - one for collecting data from parking data services
  - a shared memory in which to store such data (e.g. DB)
  - a SpringCloudConfig server for sharing application configuration (which relies on a Git repo)
  - one application to offer actual service for search
- security via SpringSecurity to ensure client identity;
- more tests.