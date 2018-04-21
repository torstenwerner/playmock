package com.example.playmock;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "author")
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "author_id")
    private Integer id;

    private String name;

    public Author(String name) {
        this.name = name;
    }
}
