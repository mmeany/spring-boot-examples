package net.mmeany.play.quote.config.security;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RoleTest {

    @Test
    void roleExists() {
        assertThat(Role.exists("poipoi"), is(false));
        assertThat(Role.exists("admin"), is(false));
        assertThat(Role.exists("Admin"), is(false));
        assertThat(Role.exists("ADMIN"), is(true));
    }
}
