### Setup
- Linux/Mac OS: open file /etc/hosts and add new line:
```
    127.0.0.1   websocket
```
- Build kafka-websocket-consumer image: Go to kafka-websocket-consumer and execute a command: mvn clean package -DskipTests then go to  kafka-websocket-consumer/docker
```
docker build -t namson0482/kafka-websocket-consumer .
docker tag namon0482/kafka-websocket-consumer:latest namson0482/kafka-websocket-consumer:1.0
docket push namson0482/kafka-websocket-consumer:1.0
```
- Build kafka-websocket-producer image: Go to kafka-websocket-consumer and execute a command: mvn clean package -DskipTests and go to  kafka-websocket-producer/docker
```
docker build -t namson0482/kafka-websocket-producer .
docker tag namon0482/kafka-websocket-producer:latest namson0482/kafka-websocket-producer:1.0
docket push namson0482/kafka-websocket-producer:1.0
```
- Build kafka-websocket-frontend image, go to  kafka-websocket-frontend
```
ng build --configuration production
docker build -t namson0482/websocket-frontend .
docker tag namson0482/websocket-frontend:latest namson0482/websocket-frontend:1.0
docket push namson0482/websocket-frontend:1.0
```
- Final, go to folder websocket_kafka/websocket_docker
```
docker-compose up -d
```
