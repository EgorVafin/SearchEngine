## Application Searchengine-master
### Development of a search engine for given sites.

### Description of the application Searchengine-master:

The application can analyze sites specified in the configuration file to index pages and further search
the necessary information on these sites. In the process of analyzing (indexing) pages, the user, if desired, can
stop indexing, using a request through the application API. All indexed pages are divided into lemmas
and saved to the database. Next, the user can submit a search request. The application will find the most
relevant pages, sorts them and returns a list of them, adding a snippet and relevance.

### Application API list:
1. api/startIndexing - starting site indexing
2. api/stopIndexing - forced stop of site indexing
3. api/statistics - website statistics
4. api/search - search query for all or one site
5. api/indexPage - indexing a single page
6. / - global information

### Stack of technologies used:
1. Spring Boot
2. JAVA. JDK 21
3. Flyway
4. Mysql
5. Lombok
6. Jsoup
7. Maven
8. Docker

### To build and run application you need:
1. build jar: mvn clean package
2. run docker-compose: docker-compose up
3. thats all:)