## FlintX Demo Integration Service

## Part I:

### Prerequisites:
- FlintX Project Template
- FlintX Core & Product Domain Services
- Google Cloud Platform account & cloud storage bucket
- Docker
- JDK 17+, Maven 3.8+, IDE (e.g. IntelliJ)

### Steps:
1. Deploy & run FlintX Fluent Services<br>
   see git clone https://github.com/fluentintegrations/flintx-config.git
2. Add required Camel Quarkus extensions
   - camel-quarkus-google-storage <br>
   `./mvnw quarkus:add-extension -Dextensions="org.apache.camel.quarkus:camel-quarkus-google-storage"`
   - camel-quarkus-mapstruct <br>
   `./mvnw quarkus:add-extension -Dextensions="org.apache.camel.quarkus:camel-quarkus-mapstruct"`
   - flintx-fluent-mapping <br>
   `./mvnw quarkus:add-extension -Dextensions="com.fluentintegrations:flintx-fluent-mapping:1.0.0"`
3. Add google-storage account key to `application.properties`
	- get google service account key (see https://cloud.google.com/iam/docs/keys-create-delete) <br>
	- generate base64 `cat {{googleServiceAccount.json}} | base64`
	- set `service.account.key=base64:{{key}}` in `application.properties`
4. Add Mapstruct mapping
	- add `mapstruct-processor` to the `maven-compiler-plugin` section in the pom.xml
5. Send event to FlintX Product Service
	- add FlintX api key to `application.properties` <br>
      `flintx.apikey={{apipkey}}`
	- add FlintX Fluent Product Service url to `application.properties` <br> 
	  `-Dflintx-fluent-product-service.url=http://127.0.0.1:8086`
6. Build native Docker image
	- set `quarkus.container-image.name` to `flintx-fluent-demo`
	- build docker image
	```shell
	./mvnw clean package -Pnative  -e \
	-Dmaven.test.skip=true \
	-Dquarkus.native.container-build=true \
	-Dquarkus.container-image.build=true
	```
	- deploy to docker
	```shell
	docker run -d --rm  -p 8090:8090 --ip=172.21.0.11 --net flintxNetwork --name=flintx-fluent-demo --platform linux/arm64 \
	-e flintx-fluent-product-service.url=http://172.21.0.7:8086 \
	-e quarkus.log.level=INFO fluentintegrations/flintx-template:0.0.1
	```
7. Copy file to Google Cloud Storage
	```shell
	gcloud storage cp demoFiles/demoProducts.json gs://flintx_demo
	```



## Part II:
### Prerequisites:
- FlintX Core & Product Domain Services
- Apache Kafka
- Docker
- JDK 17+, Maven 3.8+, IDE (e.g. IntelliJ)

1. Add required Camel Quarkus extensions
   - camel-quarkus-kafka <br>
   `./mvnw quarkus:add-extension -Dextensions="org.apache.camel.quarkus:camel-quarkus-kafka"`
2. Create Kafka topics 
   `./bin/kafka-topics.sh --create --topic event --bootstrap-server localhost:9092
   `./bin/kafka-topics.sh --create --topic eventVerification --bootstrap-server localhost:9092`
3. Start Kafka consumer
   `kafka-console-consumer.sh --topic eventVerification --from-beginning --bootstrap-server localhost:9092`
4. Send demo products to Kafka topic
   `jq -rc . demoProducts.json | kafka-console-producer.sh --topic event --bootstrap-server localhost:9092`