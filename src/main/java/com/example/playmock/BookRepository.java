package com.example.playmock;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface BookRepository extends Repository<Book, Integer> {

    @Query(value = "select * from book where book_id = :bookId", nativeQuery = true)
    Book find(int bookId);

    @Modifying
    @Query(value = "insert into book(book_id, title, author_id) values (:#{#book.id}, :#{#book.title}, :#{#book.author.id})", nativeQuery = true)
    void insert(Book book);

    @Modifying
    @Query(value = "insert into author(author_id, name) VALUES (:#{#author.id}, :#{#author.name})", nativeQuery = true)
    void insert(Author author);

    @Query(value = "select b.title, a.name as authorName from book b join author a on b.author_id = a.author_id where b.book_id = :bookId", nativeQuery = true)
    BookProjection findProjection(int bookId);

    @Query("select new com.example.playmock.BookDto(b.title, a.name) from Book b join Author a on b.author = a where b.id = :bookId")
    BookDto findDto(int bookId);
}
