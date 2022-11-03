package net.mmeany.play.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Not really part of the API, but useful when testing in a developer environment.
 * <p>
 * The single exposed method attempts to create 100 blog posts filled with random data.
 * <p>
 * It does not use JPA to do this because Spring auditing would overwrite the created and updated fields, which is a
 * pain. As a result, raw JDBC hackery is at work!
 * <p>
 * Uses the DataFaker library to generate the random data.
 */
@RestController
@RequestMapping("/random")
@Slf4j
public class RandomDataController {

    private final DataSource dataSource;

    private final ObjectMapper maper;

    public RandomDataController(DataSource dataSource, ObjectMapper maper) {
        this.dataSource = dataSource;
        this.maper = maper;
    }

    @Operation(summary = "Generate 100 blog posts filled with random data")
    @GetMapping(value = "")
    public ResponseEntity<?> random() throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement blogStmt = connection.prepareStatement("insert into blog (title, content, created, created_by, updated, updated_by, version) " +
                     " values (?, ?, ?, ?, ?, ?, 0) RETURNING ID");
             PreparedStatement tagStmt = connection.prepareStatement("insert into blog_tags (blog_id, tag) " +
                     " values (?, ?)")
        ) {

            String[] availableTags = {
                    "java",
                    "javascript",
                    "spring",
                    "spring boot",
                    "jpa",
                    "rest",
                    "spring security",
                    "liquibase"
            };
            Faker faker = new Faker();
            Random random = new Random();
            for (int i = 0; i < 100; i++) {
                Set<String> tags = null;
                while (tags == null) {
                    try {
                        tags = random.nextInt(100) > 80
                                ? Set.of(availableTags[random.nextInt(0, availableTags.length)], availableTags[random.nextInt(0, availableTags.length)])
                                : Set.of(availableTags[random.nextInt(0, availableTags.length)]);
                    } catch (Exception e) {
                        log.debug("Retrying random tags");
                    }
                }
                int j = 1;
                String by = faker.name().username();
                Date when = faker.date().past(365, TimeUnit.DAYS);

                blogStmt.setString(j++, faker.book().title());
                // This content field is a clob, need more hackery here to insert String data into it.
                blogStmt.setClob(j++, new javax.sql.rowset.serial.SerialClob(faker.lorem().paragraph(random.nextInt(2, 10)).toCharArray()));

                // We all love SQL dates!
                blogStmt.setDate(j++, new java.sql.Date(when.getTime()));
                blogStmt.setString(j++, by);
                blogStmt.setDate(j++, new java.sql.Date(when.getTime()));
                blogStmt.setString(j, by);
                try {
                    ResultSet rs = blogStmt.executeQuery();
                    if (rs.next()) {
                        long pk = rs.getLong(1);

                        tags.forEach(tag -> {
                            try {
                                tagStmt.setLong(1, pk);
                                tagStmt.setString(2, tag);
                                tagStmt.executeUpdate();
                            } catch (SQLException e) {
                                log.debug("Unable to insert the tag for blog {}: {}", pk, e.getMessage());
                            }
                        });
                    }
                    connection.commit();
                } catch (Exception e) {
                    connection.rollback();
                    log.debug("Skip probable duplicate: {}", e.getMessage());
                }
            }
        }
        return ResponseEntity.ok().build();
    }
}
