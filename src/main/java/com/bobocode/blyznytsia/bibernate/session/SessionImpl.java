package com.bobocode.blyznytsia.bibernate.session;

import com.bobocode.blyznytsia.bibernate.Note;

import com.bobocode.blyznytsia.bibernate.User;
import com.bobocode.blyznytsia.bibernate.mapper.ResultSetMapper;
import com.bobocode.blyznytsia.bibernate.mapper.ResultSetMapperImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.ds.PGSimpleDataSource;

public class SessionImpl{

  public User find(Class<?> entityClass, Object primaryKey) {
    ResultSetMapper resultSetMapper = new ResultSetMapperImpl(this);
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
    dataSource.setUser("root");
    dataSource.setPassword("root");

    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT * FROM users WHERE id = ?")){
      preparedStatement.setInt(1, 1);
      ResultSet resultSet = preparedStatement.executeQuery();
      User user = resultSetMapper.mapToEntity(resultSet, User.class);
      System.out.println(user);
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
