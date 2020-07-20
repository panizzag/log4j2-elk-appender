# log4j-2-elastic

Custom appender (core plugin) that logs messages directly into [Elasticsearch](https://www.elastic.co/products/elasticsearch), without using Beats.

## Why?

Installing beats is the recommended way to ingest data into an ELK. However, is not always possible. This plugin performs the ingestion through HTTP, without the need of installing anything else than Elk.

### Prerequisites
* git client
* Java 8+ JDK
* Maven 3+
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
### Configuration
In the table below are summarized available attribute parameters (all optional except "name"):

Parameter | Type | Default | Description
---|---|---|---
name | String | | Appender name
server | String | http://localhost:9200 | ELK server URL
index | String | Appender name | Elasticsearch destination index 
username | String | | Optional. Username for basic authentication
password | String | | Optional. Password for basic authentication

## Contributing

This is a tiny product with a simple and clean design and minimum external dependencies. The main components are:
* A standard Log4j2 appender implementation.
* An Elasticsearch client component based on Elasticsearch high level Java client.
* A log message content translator enhanced with parameters JSON encoding with Jackson. 
  
Any kind of contribution will be appreciated. You can contact me by email or open issues or pull requests for bugs, enhancements and suggestions about any aspect of this product (e.g. architectural considerations, code design, performance, etc...).  

I've included a standard [CONTRIBUTING](CONTRIBUTING.md) file for details on code of conduct, and process for submitting pull requests.

## Versioning

I use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/ivanocortesini/log4j-2-elastic/tags). 

## Authors

* **IvanoCortesini** ([Github](https://github.com/ivanocortesini) - [LinkedIn](https://www.linkedin.com/in/ivanocortesini/))

See also the list of [contributors](https://github.com/ivanocortesini/log4j-2-elastic/contributors) who participated in this project.

## License

This project is licensed under the Apache License 2.0 License - see the [LICENSE](LICENSE) file for details

