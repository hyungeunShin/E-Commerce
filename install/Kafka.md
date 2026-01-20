## https://kafka.apache.org/quickstart/

## 카프카 설치

```shell
docker pull apache/kafka:4.1.1
```

```shell
docker run -d --name kafka -p 9092:9092 apache/kafka:4.1.1
```

## 토픽 생성

```shell
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092
```

## 토픽 확인

```shell
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092
```

## Producer 실행

```shell
docker exec -it kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic quickstart-events
```

## Consumer 실행

```shell
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic quickstart-events --from-beginning
```