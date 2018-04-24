package com.example.playmock;

import org.jooq.DSLContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static com.example.playmock.jooq.tables.Author.AUTHOR;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class JooqTest {

    @Autowired
    private DSLContext sql;

    @Test
    public void dummy() {

        final int count = sql.insertInto(AUTHOR, AUTHOR.AUTHOR_ID, AUTHOR.NAME)
                .values(26, "Hildegunst von Mythenmetz")
                .execute();
        assertThat(count).isEqualTo(1);
    }
}
