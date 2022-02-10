package com.davidxie.Online.Facial.Recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class DataAccess implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataAccess.class);

    public static void main(String args[]) {
        SpringApplication.run(DataAccess.class, args);
    }
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {

        Path path = Paths.get("/Volumes/dev/online-faceid/online-facial-recognition-back-end/737.jpg");
        byte[] data = Files.readAllBytes(path);


        // Split up the array of whole names into an array of first/last names
//        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
//                .map(name -> name.split(" "))
//                .collect(Collectors.toList());

        List<Object[]> input = new ArrayList<>();
        input.add(new Object[] {"David", "Xie", data});

        // Use a Java 8 stream to print out each tuple of the list
//        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO allUser(userName, email, image) VALUES (?,?,?)", input);

        log.info("Querying for customer records where first_name = 'Josh':");
        jdbcTemplate.query(
                "SELECT userID, userName, email, image FROM allUser WHERE userName = ?", new Object[] { "Josh" },
                (rs, rowNum) -> new User(rs.getInt("userID"), rs.getString("userName"), rs.getString("email"), rs.getBytes("image"))
        ).forEach(customer -> log.info(customer.toString()));
    }
}
