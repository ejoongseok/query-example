# read 모델용 jpql, native query sample

## Entity

### Address
```Java
@Entity
public class Address {

    @Id
    private Long id;

    @OneToOne
    private Person person;

    private String state;

    private String city;

    private String street;

    private String zipCode;
}
```

### Person
```Java
@Entity
public class Person {

    @Id
    private Long id;

    private String firstName;

    private String lastName;

    @OneToOne(mappedBy = "person")
    private Address address;
```

## DTO
```Java
public record ResultDto(
        String firstName,
        String lastName,
        String street,
        String city,
        String state,
        String zipCode) {
}
```

## 문법
### jpql
```Java
final String sql = """
                SELECT new com.example.queryexample.ResultDto(
                    p.firstName, p.lastName, a.street, a.city, a.state, a.zipCode
                )
                FROM Person as p
                JOIN p.address as a
                WHERE (:firstName is null or :firstName = '' or  p.firstName = :firstName) 
                AND (:lastName is null or :lastName = '' or p.lastName = :lastName)
                """;
        final Query jpqlQuery = entityManager.createQuery(sql, ResultDto.class);
        jpqlQuery.setParameter("firstName", "John");
        jpqlQuery.setParameter("lastName", null);
        final List<ResultDto> resultList = jpqlQuery.getResultList();
```


### NamedJdbcTemplate
```Java
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
```


##레퍼런스
- NamedJdbcTemplate: https://www.baeldung.com/spring-jdbctemplate-in-list
- ignoring null Parameters: https://www.baeldung.com/spring-data-jpa-null-parameters
- Java TextBlock: https://www.baeldung.com/java-text-blocks
