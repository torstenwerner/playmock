package com.example.playmock;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "book")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "book_id")
    private Integer id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    public Book(String title) {
        this.title = title;
    }

    public Book(int book_id, String title, int author_id, String name) {
        this.id = book_id;
        this.title = title;
        this.author = new Author(author_id, name);
    }
}
