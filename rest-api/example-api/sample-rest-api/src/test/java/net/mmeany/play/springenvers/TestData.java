package net.mmeany.play.springenvers;

import lombok.experimental.UtilityClass;
import net.mmeany.play.springenvers.controller.model.NoteDto;
import net.mmeany.play.springenvers.controller.model.NoteRequestImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

@UtilityClass
public class TestData {

    public static final String TEST_USER_1_USERNAME = "test-user-1";
    public static final String TEST_USER_1_PASSWORD = "Password123";
    public static final String TEST_USER_2_USERNAME = "test-user-2";
    public static final String TEST_USER_2_PASSWORD = "Password123";
    public static final String TEST_USER_3_USERNAME = "test-user-3";
    public static final String TEST_USER_3_PASSWORD = "Password123";
    public static final Long ID = 1L;
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final Date DATE = new Date();

    public static final String BAD_USERNAME = "hackety";
    public static final String BAD_PASSWORD = "hacker";

    public static NoteRequestImpl noteRequest() {
        return new NoteRequestImpl(
                TITLE,
                DESCRIPTION
        );
    }

    public static NoteRequestImpl badNoteRequest(String title, String description) {
        return new NoteRequestImpl(
                title,
                description
        );
    }

    public static NoteDto noteDto() {
        return new NoteDto(1L, DATE, TEST_USER_1_USERNAME, TITLE, DESCRIPTION);
    }

    public static void assertNoteDto(NoteDto noteDto) {
        assertThat(noteDto, notNullValue());
        assertAll(
                () -> assertThat(noteDto.id(), is(ID)),
                () -> assertThat(noteDto.title(), is(TITLE)),
                () -> assertThat(noteDto.description(), is(DESCRIPTION)),
                () -> assertThat(noteDto.createdBy(), is(TEST_USER_1_USERNAME)),
                () -> assertThat(today(noteDto.created()), is(today(new Date())))
        );
    }

    private static String today(Date date) {
        return date == null
                ? ""
                : new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
