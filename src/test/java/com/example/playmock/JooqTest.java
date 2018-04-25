package com.example.playmock;

import com.example.playmock.jooq.tables.daos.AuthorDao;
import com.example.playmock.jooq.tables.pojos.Author;
import com.example.playmock.jooq.tables.records.AuthorRecord;
import lombok.NonNull;
import lombok.Value;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
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

    private AuthorDao authorDao;

    @Autowired
    private AuthorRepository authorRepository;

    @Before
    public void setUp() throws Exception {
        authorDao = new AuthorDao(sql.configuration());
    }

    @Test
    public void shouldInsertFields() {
        assertThat(
                sql.insertInto(AUTHOR, AUTHOR.AUTHOR_ID, AUTHOR.NAME)
                        .values(26, "Hildegunst von Mythenmetz")
                        .execute())
                .isEqualTo(1);
    }

    @Test
    public void shouldInsertFieldsWithRepo() {
        assertThat(
                authorRepository.insert(26, "Hildegunst von Mythenmetz"))
                .isEqualTo(1);
    }

    @Test
    public void shouldInsertRecord() {
        final AuthorRecord authorRecord = new AuthorRecord(26, "Hildegunst von Mythenmetz");
        authorRecord.attach(sql.configuration());
        assertThat(
                authorRecord.store())
                .isEqualTo(1);
    }

    @Test
    public void shouldInsertPojo() {
        final Author author = new Author(26, "Hildegunst von Mythenmetz");
        assertThat(
                sql.newRecord(AUTHOR, author).store())
                .isEqualTo(1);
    }

    @Test
    public void shouldInsertEntityWithRepo() {
        final AuthorEntity authorEntity = new AuthorEntity(26, "Hildegunst von Mythenmetz");
        assertThat(
                authorRepository.insert(authorEntity))
                .isEqualTo(1);
    }

    @Test
    public void shouldInsertPojoWithDao() {
        assertThat(authorDao.count()).isEqualTo(0);
        final Author author = new Author(26, "Hildegunst von Mythenmetz");
        authorDao.insert(author);
        assertThat(authorDao.count()).isEqualTo(1);
        assertThat(
                authorDao.fetchOneByAuthorId(author.getAuthorId()))
                .isEqualTo(author);
    }
}

@Value
class AuthorEntity {
    @NonNull
    Integer authorId;

    @NonNull
    String name;
}

interface AuthorRepository {
    int insert(Integer authorID, String name);

    default int insert(AuthorEntity authorEntity) {
        return insert(authorEntity.getAuthorId(), authorEntity.getName());
    }
}

@Repository
class DefaultAuthorRepository implements AuthorRepository {

    @Autowired
    private DSLContext sql;

    @Override
    public int insert(Integer authorID, String name) {
        return sql.insertInto(AUTHOR, AUTHOR.AUTHOR_ID, AUTHOR.NAME)
                .values(authorID, name)
                .execute();
    }
}