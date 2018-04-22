package com.example.playmock;

import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
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
    public void shouldLoadEntity() {
        final String authorDtoQuery = "select a from Author a";
        author = entityManager.createQuery(authorDtoQuery, Author.class).getSingleResult();
        assertThat(author.getName(), is("Hildegunst von Mythenmetz"));
        assertThat(entityManager.contains(author), is(true));
    }

    @Test
    public void shouldLoadAsDto() {
        final String authorDtoQuery = "select new com.example.playmock.Author(a.id, a.name) from Author a";
        author = entityManager.createQuery(authorDtoQuery, Author.class).getSingleResult();
        assertThat(author.getName(), is("Hildegunst von Mythenmetz"));
        assertThat(entityManager.contains(author), is(false));
    }

    @Test
    public void shouldLoadDtoEagerly() {
        final String bookDtoQuery = "select new com.example.playmock.Book(b.id, b.title, a.id, a.name) from Book b join Author a on b.author = a";
        book = entityManager.createQuery(bookDtoQuery, Book.class).getSingleResult();
        assertThat(book.getAuthor().getName(), is("Hildegunst von Mythenmetz"));
        assertThat(entityManager.contains(book), is(false));
    }

    @Test
    public void shouldUpdateWithQuery() {
        final String bookUpdateQuery = "update Book set title = 'Testtitel' where id = :bookId";
        final int updateCount = entityManager.createQuery(bookUpdateQuery)
                .setParameter("bookId", book.getId())
                .executeUpdate();
        assertThat(updateCount, is(1));

        assertThat(entityManager.contains(book), is(true));
        assertThat(book.getTitle(), is("Unter Buchhaim"));

        entityManager.refresh(book);
        assertThat(book.getTitle(), is("Testtitel"));
    }

    @Test
    public void shouldLoadNatively() {
        final String authorNativeQuery = "select * from author";
        author = (Author) entityManager.createNativeQuery(authorNativeQuery, Author.class).getSingleResult();
        assertThat(author.getName(), is("Hildegunst von Mythenmetz"));
        assertThat(entityManager.contains(author), is(true));
    }

    @Test
    public void shouldLoadDtoNatively() {
        final String authorNativeQuery = "select a.author_id as id, a.name from author a";
        author = (Author) entityManager.createNativeQuery(authorNativeQuery)
                .unwrap(Query.class)
                .setResultTransformer(Transformers.aliasToBean(Author.class))
                .getSingleResult();
        assertThat(author.getName(), is("Hildegunst von Mythenmetz"));
        assertThat(entityManager.contains(author), is(false));
    }
}
