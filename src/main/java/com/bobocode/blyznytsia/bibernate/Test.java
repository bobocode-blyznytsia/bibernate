package com.bobocode.blyznytsia.bibernate;

import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersisterImpl;
import java.sql.Connection;
import java.sql.SQLException;
import org.postgresql.ds.PGSimpleDataSource;

public class Test {

  public static void main(String[] args) {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
    dataSource.setUser("root");
    dataSource.setPassword("root");

    try (Connection connection = dataSource.getConnection()) {
      EntityPersister entityPersister = new EntityPersisterImpl(connection);

      /*Note note = entityPersister.findById(Note.class, 1)
          .orElseThrow();
      System.out.println(note);*/

      User user = entityPersister.findById(User.class, 1)
          .orElseThrow();
      //UserProfile userProfile = user.getProfile();

      /*UserProfile userProfile = entityPersister.findById(UserProfile.class, 1)
              .orElseThrow();*/

      //System.out.println(user);
      System.out.println(user);
      //System.out.println(userProfile.getUser());
      for (Note note : user.getNotes()) {
        System.out.println(note);
        System.out.println(note.getUser());
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
