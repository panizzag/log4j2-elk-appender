# log4j-2-elastic

Custom appender (core plugin) that logs messages directly into [Elasticsearch](https://www.elastic.co/products/elasticsearch), without using Beats.

## Why?

Installing beats is the recommended way to ingest data into an ELK. However, is not always possible. This plugin performs the ingestion through HTTP, without the need of installing anything else than Elk.

### Prerequisites
* git client
* JDK (8)
* Maven 3+ (tested on 3.3.5)
* ELK 7 (tested on 7.8.0)

## Usage
* include the following dependency in your mule project:
```
<dependency>
    <groupId>com.mulesoft.services</groupId>
    <artifactId>log4j2-elk-apppender</artifactId>
    <version>1.0.0</version>
</dependency>
```
* include the  "Elk" type appender into your log4j2 configuration like in the following example:
```
<Elk name="elk-appender"
     server="http://localhost:9200"
     index="mule-dev">
    <PatternLayout pattern="%m%n"/>
</Elk>
```

## Development environment

Execute  
```mvn clean install``` 
To install in the local repository

