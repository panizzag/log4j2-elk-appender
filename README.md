# log4j-2-elastic

Custom appender (core plugin) that logs messages directly into [Elasticsearch](https://www.elastic.co/products/elasticsearch), without using Beats.

## Why?

Installing beats is the recommended way to ingest data into an ELK. However, is not always possible. This plugin performs the ingestion through HTTP, without the need of installing anything else than Elk.

### Prerequisites
* git client
* JDK (8)
* Maven 3+ (tested on 3.3.5)
* ELK (tested on 6.x)

## Usage

* include the  "Elk" type appender into your log4j2 configuration like in the following example:
```
<Elk
        name="elk-appender"
        server="http://host-1:9200"
        index="example-log-index"
        username="usr",
        password="psw"
        >
    <PatternLayout pattern="%m%n"/>
</Elk>
```

