package at.aau.ase.cl.api.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class Book {
    public UUID id;

    @NotNull
    public UUID ownerId;

    @Size(min = 1, max = 255)
    @NotNull
    public String title;


    public String author;

    public Book() {
    }

    public Book(UUID ownerId, String title, String author) {
        this.ownerId = ownerId;
        this.title = title;
        this.author = author;
    }
}
