# Logging with ElasticSearch
## Spring Boot, ElasticSearch, Actuator, Lombok.

### Objetivo
En este ejemplo se ha construido un pequeño proyecto que utiliza como origen de datos una base de datos en elasticserach.
Además se ha configurado para que el metrics de actuator sea consumido por elasticsearch cada minuto y así poder tener una analítica de los datos.
Se ha creado una funcionalidad para que cada petición que se hace a la api se muestre en el logging.

### Claves
El indice del proyecto se creara automaticamente en elasticsearch si no existiera.

---