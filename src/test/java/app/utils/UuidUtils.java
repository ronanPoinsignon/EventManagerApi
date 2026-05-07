package app.utils;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

@Service
public class UuidUtils {

    private Supplier<UUID> generateStrategy;
    private UUID lastUUID;

    public UuidUtils() {
        generateStrategy = UUID::randomUUID;
    }

    public void stopGeneration() {
        generateStrategy = UUID::randomUUID;
    }

    public void playGeneration() {
        generateStrategy = () -> lastUUID == null ? generate() : lastUUID;
    }

    public UUID generate() {
        lastUUID = generateStrategy.get();
        return lastUUID;
    }

}
