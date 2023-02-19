package com.example.queryexample;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@Sql(scripts = "/projection-insert-data.sql")
@Sql(scripts = "/projection-clean-up-data.sql", executionPhase = AFTER_TEST_METHOD)
public class QueryTest {

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    void query() {
        final String sql = """
                SELECT new com.example.queryexample.ResultDto(
                    p.firstName, p.lastName, a.street, a.city, a.state, a.zipCode
                )
                FROM Person as p
                JOIN p.address as a
                WHERE (:firstName is null or :firstName = '' or  p.firstName = :firstName) 
                AND (:lastName is null or :lastName = '' or p.lastName = :lastName)
                """;
        final Query nativeQuery = entityManager.createQuery(sql, ResultDto.class);
        nativeQuery.setParameter("firstName", "John");
        nativeQuery.setParameter("lastName", null);
        final List<ResultDto> resultList = nativeQuery.getResultList();

        System.out.println("resultList = " + resultList);

        assertThat(resultList).hasSize(1);
    }

    @Test
    void jdbcNamedQuery() {
        final String sql = """
                SELECT
                    p.first_name, p.last_name , a.street, a.city, a.state, a.zip_code
                FROM person  p
                INNER JOIN address a ON p.id = a.person_id
                WHERE (:firstName is null or :firstName = '' or  p.first_name = :firstName)
                AND (:lastName is null or :lastName = '' or p.last_name = :lastName)
                """;
        final SqlParameterSource parameter = new MapSqlParameterSource()
                .addValue("firstName", "John")
                .addValue("lastName", null);

        final List<ResultDto> resultList = jdbcTemplate.query(sql, parameter, (rs, rowNum) -> {
            final String firstName = rs.getString("first_name");
            final String lastName = rs.getString("last_name");
            final String street = rs.getString("street");
            final String city = rs.getString("city");
            final String state = rs.getString("state");
            final String zipCode = rs.getString("zip_code");
            return new ResultDto(firstName, lastName, street, city, state, zipCode);
        });

        System.out.println("resultList = " + resultList);

        assertThat(resultList).hasSize(1);
    }
}
