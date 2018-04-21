package com.example.playmock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class JpaTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void shouldPersistCorrectly() {

        final Author author = new Author("Hildegunst von Mythenmetz");
        entityManager.persist(author);

        final Book book = new Book("Unter Buchhaim");
        book.setAuthor(author);
        entityManager.persist(book);

        entityManager.flush();
    }
}
