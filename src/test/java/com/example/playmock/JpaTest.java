package com.example.playmock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class JpaTest {

    @PersistenceContext
    private EntityManager entityManager;

    private Author author = new Author("Hildegunst von Mythenmetz");
    private Book book = new Book("Unter Buchhaim");

    @Before
    public void setup() {
        book.setAuthor(author);
        entityManager.persist(author);
        entityManager.persist(book);
    }

    @Test
    public void shouldPersistCorrectly() {
        assertThat(book.getId(), notNullValue());

        entityManager.flush();

        book.setTitle("Testtitel");
        entityManager.flush();

        assertThat(entityManager.find(Book.class, book.getId()).getTitle(), is("Testtitel"));
    }

    @Test
    public void shouldLoadAsDto() {
        entityManager.flush();
        entityManager.clear();

        author = entityManager.createQuery("select new com.example.playmock.Author(a.id, a.name) from Author a", Author.class).getSingleResult();
        assertThat(author.getName(), is("Hildegunst von Mythenmetz"));
    }

    @Test
    public void shouldLoadDtoEagerly() {
        entityManager.flush();
        entityManager.clear();

        book = entityManager.createQuery(
                "select new com.example.playmock.Book(b.id, b.title, a.id, a.name) from Book b join Author a on b.author = a",
                Book.class)
                .getSingleResult();
        assertThat(book.getAuthor().getName(), is("Hildegunst von Mythenmetz"));
    }
}
