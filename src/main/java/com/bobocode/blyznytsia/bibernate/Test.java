package com.bobocode.blyznytsia.bibernate;

import com.bobocode.blyznytsia.bibernate.mapper.ResultSetMapper;
import com.bobocode.blyznytsia.bibernate.mapper.ResultSetMapperImpl;
import com.bobocode.blyznytsia.bibernate.session.SessionImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.ds.PGSimpleDataSource;

public class Test {

  public static void main(String[] args) {
    SessionImpl session = new SessionImpl();
    ResultSetMapper resultSetMapper = new ResultSetMapperImpl(session);
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
    dataSource.setUser("root");
    dataSource.setPassword("root");

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT * FROM notes WHERE id = ?")){
      preparedStatement.setInt(1, 1);
      ResultSet resultSet = preparedStatement.executeQuery();
      Note note = resultSetMapper.mapToEntity(resultSet, Note.class);
      System.out.println(note);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
