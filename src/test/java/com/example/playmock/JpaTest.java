package com.example.playmock;

import org.assertj.core.api.Condition;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
//@Commit
public class JpaTest {

    @PersistenceContext
    private EntityManager entityManager;

    private Author author = new Author("Hildegunst von Mythenmetz");
    private Book book = new Book("Unter Buchhaim");

    private Condition<Object> managedByPersistenceContext;

    @Before
    public void setup() {
        book.setAuthor(author);
        entityManager.persist(author);
        entityManager.persist(book);
        managedByPersistenceContext = new Condition<>(entityManager::contains, "managed by persistence context");
    }

    @Test
    public void shouldPersistCorrectly() {
        assertThat(book.getId()).isNotNull();

        entityManager.flush();

        book.setTitle("Testtitel");
        entityManager.flush();

        assertThat(entityManager.find(Book.class, book.getId()).getTitle()).isEqualTo("Testtitel");

        assertThat(author).is(managedByPersistenceContext);
        entityManager.detach(author);
        assertThat(author).isNot(managedByPersistenceContext);

        assertThat(book).is(managedByPersistenceContext);
        entityManager.clear();
        assertThat(book).isNot(managedByPersistenceContext);

        final Author mergedAuthor = entityManager.merge(author);
        assertThat(author).isNot(managedByPersistenceContext);
        assertThat(mergedAuthor).is(managedByPersistenceContext);
    }

    @Test
    public void shouldFlushEntityChanges() {
        book.setTitle("Testtitel");

        entityManager.flush();
        entityManager.clear();

        assertThat(entityManager.find(Book.class, book.getId()).getTitle()).isEqualTo("Testtitel");
    }

    @Ignore("first assertion fails")
    @Test
    public void shouldInsertManualId() {
        final Author author2 = new Author(Integer.MAX_VALUE, "Danzelot von Silbendrechsler");
        final Author author3 = entityManager.merge(author2);

        entityManager.flush();
        entityManager.clear();

        assertThat(author3.getId()).isEqualTo(author2.getId());
        assertThat(entityManager.find(Author.class, author3.getId()).getName()).isEqualTo("Danzelot von Silbendrechsler");
    }

    @Test
    public void shouldInsertCustomerWithId() {
        final Customer customer = new Customer(0, "a sample customer");
        entityManager.persist(customer);

        entityManager.flush();
        entityManager.clear();

        assertThat(entityManager.find(Customer.class, 0))
                .isNotNull()
                .extracting(Customer::getId, Customer::getName).containsExactly(0, "a sample customer");
    }

    @Test
    public void shouldLoadEntity() {
        author.setName("Dancelot von Silbendrechsler");

        final String authorDtoQuery = "select a from Author a where a = :author";
        final Author loadedAuthor = entityManager.createQuery(authorDtoQuery, Author.class)
                .setParameter("author", author)
                .getSingleResult();
        assertThat(loadedAuthor)
                .is(managedByPersistenceContext)
                .isSameAs(author)
                .extracting(Author::getName).containsExactly("Dancelot von Silbendrechsler");
    }

    @Test
    public void shouldLoadAsDto() {
        final String authorDtoQuery = "select new com.example.playmock.Author(a.id, a.name) from Author a where a = :author";
        author = entityManager.createQuery(authorDtoQuery, Author.class)
                .setParameter("author", author)
                .getSingleResult();

        assertThat(author)
                .isNot(managedByPersistenceContext)
                .extracting(Author::getName).containsExactly("Hildegunst von Mythenmetz");
    }

    @Test
    public void shouldLoadDtoEagerly() {
        final String bookDtoQuery = "select new com.example.playmock.Book(b.id, b.title, a.id, a.name) from Book b join Author a on b.author = a";
        book = entityManager.createQuery(bookDtoQuery, Book.class).getSingleResult();
        assertThat(book)
                .isNot(managedByPersistenceContext)
                .extracting("author.name").containsExactly("Hildegunst von Mythenmetz");
    }

    @Test
    public void shouldUpdateWithQuery() {
        final String bookUpdateQuery = "update Book set title = 'Testtitel' where id = :bookId";
        final int updateCount = entityManager.createQuery(bookUpdateQuery)
                .setParameter("bookId", book.getId())
                .executeUpdate();
        assertThat(updateCount).isEqualTo(1);

        assertThat(entityManager.contains(book)).isTrue();
        assertThat(book.getTitle()).isEqualTo("Unter Buchhaim");

        entityManager.refresh(book);
        assertThat(book.getTitle()).isEqualTo("Testtitel");
    }

    @Test
    public void shouldLoadNatively() {
        final String authorNativeQuery = "select * from author where author_id = :authorId";
        author = (Author) entityManager.createNativeQuery(authorNativeQuery, Author.class)
                .setParameter("authorId", author.getId())
                .getSingleResult();
        assertThat(author.getName()).isEqualTo("Hildegunst von Mythenmetz");
        assertThat(entityManager.contains(author)).isTrue();
    }

    @Test
    public void shouldLoadDtoNatively() {
        final String authorNativeQuery = "select a.author_id as id, a.name from author a where a.author_id = :authorId";
        author = (Author) entityManager.createNativeQuery(authorNativeQuery)
                .unwrap(Query.class)
                .setParameter("authorId", author.getId())
                .setResultTransformer(Transformers.aliasToBean(Author.class))
                .getSingleResult();
        assertThat(author.getName()).isEqualTo("Hildegunst von Mythenmetz");
        assertThat(entityManager.contains(author)).isFalse();
    }

    @Test
    public void shouldLoadTupleNatively() {
        final String authorNativeQuery = "select a.author_id as id, a.name from author a where a.author_id = :authorId";
        final Tuple tuple = (Tuple) entityManager.createNativeQuery(authorNativeQuery, Tuple.class)
                .setParameter("authorId", author.getId())
                .getSingleResult();
        assertThat(tuple.get("name", String.class)).isEqualTo("Hildegunst von Mythenmetz");
    }

    @Test
    public void shouldReference() {
        entityManager.flush();
        entityManager.clear();

        final Author proxiedAuthor = entityManager.getReference(Author.class, author.getId());
        assertThat(proxiedAuthor).as("a proxy reference to author")
                .isNotNull()
                .isNotSameAs(author)
                .isEqualTo(author);

        final int invalidId = author.getId() + 1;
        final Author invalidAuthor = entityManager.getReference(Author.class, invalidId);

        assertThat(invalidAuthor)
                .isNotNull()
                .is(managedByPersistenceContext)
                .has(authorIdValue(invalidId));

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(invalidAuthor::getName)
                .withMessageStartingWith("Unable to find com.example.playmock.Author with id");
    }

    Condition<Author> authorIdValue(int expectedId) {
        return new Condition<>(author -> author.getId() == expectedId, "id value " + expectedId);
    }
}
