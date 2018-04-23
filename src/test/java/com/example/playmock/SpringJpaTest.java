package com.example.playmock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class SpringJpaTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void shouldLoadBook() {

        final Book book = new Book(26, "Unter Buchhaim", 1, "Hildegunst von Mythenmetz");

        bookRepository.insert(book.getAuthor());
        bookRepository.insert(book);

        assertThat(bookRepository.find(26))
                .isNotNull()
                .extracting("title", "author.name")
                .containsExactly("Unter Buchhaim", "Hildegunst von Mythenmetz");

        final BookSummary summary = bookRepository.findSummary(26);
        assertThat(summary)
                .isNotNull()
                .extracting(BookSummary::getTitle, BookSummary::getAuthorName)
                .containsExactly("Unter Buchhaim", "Hildegunst von Mythenmetz");
    }
}
