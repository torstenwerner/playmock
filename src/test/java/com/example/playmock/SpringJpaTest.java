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

        final BookProjection bookProjection = bookRepository.findProjection(26);
        assertThat(bookProjection)
                .isNotNull()
                .extracting(BookProjection::getTitle, BookProjection::getAuthorName)
                .containsExactly("Unter Buchhaim", "Hildegunst von Mythenmetz");

        final BookDto bookDto = bookRepository.findDto(26);
        assertThat(bookDto)
                .isNotNull()
                .extracting(BookDto::getTitle, BookDto::getAuthorName)
                .containsExactly("Unter Buchhaim", "Hildegunst von Mythenmetz");
    }
}
