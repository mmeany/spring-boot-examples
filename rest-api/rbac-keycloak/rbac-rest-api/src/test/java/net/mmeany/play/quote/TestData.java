package net.mmeany.play.quote;

import lombok.experimental.UtilityClass;
import net.mmeany.play.quote.controller.model.QuoteDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@UtilityClass
public class TestData {

    public static final String TEST_REALM = "dev";
    public static final String TEST_CLIENT_ID = "blog-service";
    public static final String ADMIN_USERNAME = "test-user-1";
    public static final String ADMIN_PASSWORD = "Password123";
    public static final String MANAGER_USERNAME = "test-user-2";
    public static final String MANAGER_PASSWORD = "Password123";
    public static final String MEMBER_USERNAME = "test-user-3";
    public static final String MEMBER_PASSWORD = "Password123";
    public static final String NO_ROLES_USERNAME = "test-user-4";
    public static final String NO_ROLES_PASSWORD = "Password123";

    public static final String QUOTE = "a quote";

    public static final String BAD_USERNAME = "hackety";
    public static final String BAD_PASSWORD = "hacker";

    public static QuoteDto quoteDto() {
        return new QuoteDto(QUOTE);
    }

    public static void assertQuoteDto(QuoteDto quoteDto) {
        assertThat(quoteDto, notNullValue());
        assertThat(quoteDto.quote(), is(QUOTE));
    }
}
