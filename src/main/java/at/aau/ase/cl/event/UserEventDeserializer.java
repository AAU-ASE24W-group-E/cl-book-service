package at.aau.ase.cl.event;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UserEventDeserializer extends ObjectMapperDeserializer<UserEvent> {
    public UserEventDeserializer() {
        super(UserEvent.class);
    }
}
