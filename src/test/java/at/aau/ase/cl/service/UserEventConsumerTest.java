package at.aau.ase.cl.service;

import at.aau.ase.cl.domain.BookOwnerEntity;
import at.aau.ase.cl.event.UserEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
class UserEventConsumerTest {
    @Inject
    BookOwnerService bookOwnerService;

    @InjectKafkaCompanion
    KafkaCompanion kafka;

    Serializer<UUID> keySerializer;
    Serializer<UserEvent> valueSerializer;

    @BeforeEach
    void setUp() {
        keySerializer = Serdes.serdeFrom(UUID.class).serializer();
        valueSerializer = new ObjectMapperSerializer<>();
    }

    @AfterEach
    void tearDown() {
        keySerializer.close();
        valueSerializer.close();
    }

    @Test
    void consumeUserEventShouldUpdateOwner() throws Exception {
        UserEvent event = new UserEvent(UUID.randomUUID(), "test", 46.6, 15.5);

        assertNull(bookOwnerService.getBookOwner(event.id()));

        kafka.produceWithSerializers(keySerializer, valueSerializer)
                .fromRecords(new ProducerRecord<>("cl.user", event.id(), event))
                .awaitCompletion(Duration.ofSeconds(5));

        // wait for the message to be processed
        BookOwnerEntity owner = null;
        for (int i = 0; i < 10; i++) {
            owner = bookOwnerService.getBookOwner(event.id());
            if (owner != null) {
                break;
            }
            Thread.sleep(1000);
        }

        assertNotNull(owner);
        assertEquals(event.id(), owner.id);
        assertEquals(event.username(), owner.name);
        assertEquals(event.latitude(), owner.location.getLatitude());
        assertEquals(event.longitude(), owner.location.getLongitude());
    }
}