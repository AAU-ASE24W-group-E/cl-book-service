package at.aau.ase.cl.event;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class LendingEventDeserializer extends ObjectMapperDeserializer<LendingEvent> {
    public LendingEventDeserializer() {
        super(LendingEvent.class);
    }
}
