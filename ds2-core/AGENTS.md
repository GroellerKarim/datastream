# DS2-Core Backend
ds2-core

# Technology Stack
- Java 26
- Spring Boot 4
    - Hibernate JPA with postgres
- Lombok
- Flyway 

## Architecture Style

Layered Architecture

- Domain
The domain is isolated, no library, pure pojo's and encapsulates the core business logic. Business Logic always runs inside of domain objects. 
Should the domain rely on multiple implementations,  external services, or rely on libraries, a hexagonal approach is to be chosen.
In that case the domain will provide the ports (interfaces) and the service or persistence layers will provide the adapters (implementations of the domain interface). 

- Persistence 
JPA Entities will be created in a DB optimal away. 

- Service
The service orchestrates the persistence and domain layers in the tied business case. It should not perform actual business logic and be purely focused on the orchestration
of the layers. 

- Presentation
The presentation layer has the following folders 
``api`` - containing the REST endpoints
``dto.request`` - containing the incoming definitions, i.e. body definitions or complex url parameter definitions
``dto.response`` - containing the response definitions of our endpoints 


## Patterns

### Domain-Entity Conversions

The domain layer must provide "Data" interfaces. A read-only interface of it's values. The corresponding JPA interface will implement this domain Data interface. 
When loading an entity from the DB and converting it to a real DomainObject, the domain should have a static method ``DomainObject.loadFromExisting(DomainObjectData data);`` 
which properly constructs a DomainObject with the data provided. 

All other constructors in a domain object must be tied to business cases (and likely not provide an AllArgsConstructor but only what is needed).

### Exception Handling
There is a Top-Level eu.groeller.ds.DatastreamException(String message, String userMessage)

Each layer must have their own type of exceptions. 

``domain.exception.DatastreamDomainException``
``persistence.exception.DatastreamPersistenceException``
``service.exception.DatastreamServiceException``
``presentation.exception.DatastreamPresentationException``

Every single exception must be wrapped into a DatastreamException. 
A GlobalExceptionHandler must catch all.

There can be however many subtypes of an exception. For example an ``IllegalExerciseEntryDomainException extends DatastreamDomainException``
Inside of an exception, the constructor must be private and instances of the exception must only be constructable through static factory methods. 
Instatiation Example: ``IllegalExerciseEntryDomainException.failedParsing(.... required parameters);``
