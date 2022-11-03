package net.mmeany.play.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Not really part of the API, but useful when testing in a developer environment.
 * <p>
 * The single exposed method attempts to create 100 blog comments filled with random data.
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

    @Operation(summary = "Generate 100 blog comments filled with random data")
    @GetMapping(value = "")
    public ResponseEntity<?> random() throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement blogStmt = connection.prepareStatement("insert into blog (title, content, created, created_by, updated, updated_by, version) " +
                     " values (?, ?, ?, ?, ?, ?, 0) RETURNING ID");
             PreparedStatement tagStmt = connection.prepareStatement("insert into blog_tags (blog_id, tag) " +
                     " values (?, ?)")
        ) {
            Faker faker = new Faker();
            Random random = new Random();

            // Start by creating a comment for every blog post
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id FROM blog");
                 PreparedStatement commentStmt = connection.prepareStatement("insert into comment (blog_id, in_reply_to_id, title, content, created, created_by, updated, updated_by, version)" +
                         " values (?, ?, ?, ?, ?, ?, ?, ?, 0)")) {
                while (rs.next()) {
                    long blogId = rs.getLong(1);
                    for (int k = 0; k < random.nextInt(1, 4); k++) {
                        String by = faker.name().username();
                        Date when = faker.date().past(365, TimeUnit.DAYS);
                        int j = 1;
                        commentStmt.setLong(j++, blogId);
                        commentStmt.setNull(j++, Types.BIGINT);
                        commentStmt.setString(j++, faker.book().title());
                        commentStmt.setClob(j++, new javax.sql.rowset.serial.SerialClob(faker.lorem().paragraph(random.nextInt(2, 10)).toCharArray()));
                        commentStmt.setDate(j++, new java.sql.Date(when.getTime()));
                        commentStmt.setString(j++, by);
                        commentStmt.setDate(j++, new java.sql.Date(when.getTime()));
                        commentStmt.setString(j, by);
                        commentStmt.executeUpdate();
                        connection.commit();
                        commentStmt.clearParameters();
                    }
                }
            }

            // For every comment, create upto 2 replies to
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, blog_id FROM comment");
                 PreparedStatement commentStmt = connection.prepareStatement("insert into comment (blog_id, in_reply_to_id, title, content, created, created_by, updated, updated_by, version)" +
                         " values (?, ?, ?, ?, ?, ?, ?, ?, 0)")) {
                while (rs.next()) {
                    long commentId = rs.getLong(1);
                    long blogId = rs.getLong(2);
                    for (int k = 0; k < random.nextInt(1, 4); k++) {
                        String by = faker.name().username();
                        Date when = faker.date().past(365, TimeUnit.DAYS);
                        int j = 1;
                        commentStmt.setLong(j++, blogId);
                        commentStmt.setLong(j++, commentId);
                        commentStmt.setString(j++, faker.book().title());
                        commentStmt.setClob(j++, new javax.sql.rowset.serial.SerialClob(faker.lorem().paragraph(random.nextInt(2, 10)).toCharArray()));
                        commentStmt.setDate(j++, new java.sql.Date(when.getTime()));
                        commentStmt.setString(j++, by);
                        commentStmt.setDate(j++, new java.sql.Date(when.getTime()));
                        commentStmt.setString(j, by);
                        commentStmt.executeUpdate();
                        connection.commit();
                        commentStmt.clearParameters();
                    }
                }
            }
        }
        return ResponseEntity.ok().build();
    }
}
