package com.example.catalogservice.message;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
public class JsonMessageConverter extends MessagingMessageConverter {
    private static final JavaType OBJECT = TypeFactory.defaultInstance().constructType(Object.class);

    private final ObjectMapper objectMapper;

    private final Jackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();

    public JsonMessageConverter() {
        this(JacksonUtils.enhancedObjectMapper());
    }

    public JsonMessageConverter(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "'objectMapper' must not be null.");
        this.objectMapper = objectMapper;
    }

    @Override
    protected Headers initialRecordHeaders(Message<?> message) {
        RecordHeaders headers = new RecordHeaders();
        this.typeMapper.fromClass(message.getPayload().getClass(), headers);
        return headers;
    }

    @Override
    protected Object convertPayload(Message<?> message) {
        throw new UnsupportedOperationException("Select a subclass that creates a ProducerRecord value "
                + "corresponding to the configured Kafka Serializer");
    }

    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
        Object value = record.value();
        if(record.value() == null) {
            return KafkaNull.INSTANCE;
        }

        JavaType javaType = determineJavaType(record, type);

        if(value instanceof Bytes) {
            value = ((Bytes) value).get();
        }

        if(value instanceof String) {
            try {
                return this.objectMapper.readValue((String) value, javaType);
            }
            catch(IOException e) {
                throw new ConversionException("Failed to convert from JSON", record, e);
            }
        } else if(value instanceof byte[]) {
            try {
                return this.objectMapper.readValue((byte[]) value, javaType);
            } catch(IOException e) {
                throw new ConversionException("Failed to convert from JSON", record, e);
            }
        } else {
            throw new IllegalStateException("Only String, Bytes, or byte[] supported");
        }
    }

    private JavaType determineJavaType(ConsumerRecord<?, ?> record, Type type) {
        log.info("{}", this.typeMapper.getTypePrecedence());

        JavaType javaType = this.typeMapper.getTypePrecedence().equals(Jackson2JavaTypeMapper.TypePrecedence.INFERRED) && type != null
                ? TypeFactory.defaultInstance().constructType(type)
                : this.typeMapper.toJavaType(record.headers());

        log.info("1: {}", javaType);

        if(javaType == null) {
            if(type != null) {
                javaType = TypeFactory.defaultInstance().constructType(type);
            } else {
                javaType = OBJECT;
            }
        }

        log.info("2: {}", javaType);

        return javaType;
    }
}
