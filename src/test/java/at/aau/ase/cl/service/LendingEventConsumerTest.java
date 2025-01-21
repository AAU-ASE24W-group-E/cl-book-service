package at.aau.ase.cl.service;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.api.model.BookFormat;
import at.aau.ase.cl.api.model.BookSortingProperty;
import at.aau.ase.cl.api.model.BookStatus;
import at.aau.ase.cl.event.LendingEvent;
import at.aau.ase.cl.event.LendingStatus;
import at.aau.ase.cl.mapper.BookMapper;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
class LendingEventConsumerTest {
    @Inject
    BookService bookService;

    @Inject
    BookOwnerService bookOwnerService;

    @InjectKafkaCompanion
    KafkaCompanion kafka;

    Serializer<UUID> keySerializer;
    Serializer<LendingEvent> valueSerializer;

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
    void consumeLendingEventShouldUpdateStatus() throws Exception {
        var bookId = bookService.createBook(BookMapper.INSTANCE.map(new Book(null,
                "Dummy Title",
                "978-7-7762-1477-0",
                List.of("en"),
                BookFormat.HARDCOVER,
                "Dummy Publisher",
                2025,
                "dummy",
                "Dummy Edition",
                List.of("Dummy Author")))).id;
        UUID ownerId = UUID.randomUUID();
        bookOwnerService.createBookOwnership(ownerId, bookId);

        var books = bookOwnerService.getOwnBooks(ownerId, BookSortingProperty.TITLE, false);
        assertEquals(1, books.size());
        assertEquals(BookStatus.UNAVAILABLE, books.getFirst().status);

        LendingEvent event = new LendingEvent(bookId, ownerId, LendingStatus.BORROWED);

        kafka.produceWithSerializers(keySerializer, valueSerializer)
                .fromRecords(new ProducerRecord<>("cl.lending", event.bookId(), event))
                .awaitCompletion(Duration.ofSeconds(5));

        // wait for the message to be processed
        for (int i = 0; i < 10; i++) {
            books = bookOwnerService.getOwnBooks(ownerId, BookSortingProperty.TITLE, false);
            if (books.getFirst().status != BookStatus.UNAVAILABLE) {
                break;
            }
            Thread.sleep(1000);
        }

        assertEquals(BookStatus.LENT, books.getFirst().status);
    }
}