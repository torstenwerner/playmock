package com.example.playmock;

import org.assertj.core.api.Assertions;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
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

        assertThat(entityManager.contains(author), is(true));
        entityManager.detach(author);
        assertThat(entityManager.contains(author), is(false));

        assertThat(entityManager.contains(book), is(true));
        entityManager.clear();
        assertThat(entityManager.contains(book), is(false));

        final Author mergedAuthor = entityManager.merge(author);
        assertThat(entityManager.contains(author), is(false));
        assertThat(entityManager.contains(mergedAuthor), is(true));
    }

    @Test
    public void shouldLoadEntity() {
        author.setName("Dancelot von Silbendrechsler");

        final String authorDtoQuery = "select a from Author a where a = :author";
        final Author loadedAuthor = entityManager.createQuery(authorDtoQuery, Author.class)
                .setParameter("author", author)
                .getSingleResult();
        assertThat(loadedAuthor.getName(), is("Dancelot von Silbendrechsler"));
        assertThat(entityManager.contains(loadedAuthor), is(true));

        assertThat(loadedAuthor, sameInstance(author));
    }

    @Test
    public void shouldLoadAsDto() {
        final String authorDtoQuery = "select new com.example.playmock.Author(a.id, a.name) from Author a where a = :author";
        author = entityManager.createQuery(authorDtoQuery, Author.class)
                .setParameter("author", author)
                .getSingleResult();
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
        final String authorNativeQuery = "select * from author where author_id = :authorId";
        author = (Author) entityManager.createNativeQuery(authorNativeQuery, Author.class)
                .setParameter("authorId", author.getId())
                .getSingleResult();
        assertThat(author.getName(), is("Hildegunst von Mythenmetz"));
        assertThat(entityManager.contains(author), is(true));
    }

    @Test
    public void shouldLoadDtoNatively() {
        final String authorNativeQuery = "select a.author_id as id, a.name from author a where a.author_id = :authorId";
        author = (Author) entityManager.createNativeQuery(authorNativeQuery)
                .unwrap(Query.class)
                .setParameter("authorId", author.getId())
                .setResultTransformer(Transformers.aliasToBean(Author.class))
                .getSingleResult();
        assertThat(author.getName(), is("Hildegunst von Mythenmetz"));
        assertThat(entityManager.contains(author), is(false));
    }

    @Test
    public void shouldReference() {
        entityManager.flush();
        entityManager.clear();

        final Author proxiedAuthor = entityManager.getReference(Author.class, author.getId());
        assertThat(proxiedAuthor, notNullValue());
        assertThat(proxiedAuthor, not(sameInstance(author)));
        assertThat(proxiedAuthor, is(author));

        final int invalidId = author.getId() + 1;
        final Author invalidAuthor = entityManager.getReference(Author.class, invalidId);
        assertThat(invalidAuthor, notNullValue());
        assertThat(invalidAuthor.getId(), is(invalidId));
        assertThat(entityManager.contains(invalidAuthor), is(true));
        assertThatThrownBy(invalidAuthor::getName)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Unable to find com.example.playmock.Author with id %d", invalidId);
    }
}
