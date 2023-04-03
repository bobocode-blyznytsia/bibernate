package com.bobocode.blyznytsia.bibernate.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import com.bobocode.blyznytsia.bibernate.testdata.Address;
import com.bobocode.blyznytsia.bibernate.testdata.PersonWithOneToOne;
import com.bobocode.blyznytsia.bibernate.testdata.PlainPerson;
import java.sql.ResultSet;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultSetMapperImplTest {

  @Mock
  private ResultSet resultSet;
  @Mock
  private EntityPersister entityPersister;

  @Test
  void mapToEntityWithSimpleFields() throws Exception {
    ResultSetMapper mapper = new ResultSetMapperImpl(entityPersister);
    when(resultSet.getObject("id")).thenReturn(1);
    when(resultSet.getObject("first_name")).thenReturn("Joshua");
    when(resultSet.getObject("last_name")).thenReturn("Bloch");
    when(resultSet.getObject("birthday")).thenReturn(java.sql.Date.valueOf("2000-01-01"));

    PlainPerson plainPerson = mapper.mapToEntity(resultSet, PlainPerson.class);
    assertEquals(1, plainPerson.getId());
    assertEquals("Joshua", plainPerson.getFirstName());
    assertEquals("Bloch", plainPerson.getLastName());
    assertEquals(LocalDate.parse("2000-01-01"), plainPerson.getBirthday());
  }

  @Test
  void mapToEntityWithRelationFields() throws Exception {
    ResultSetMapper mapper = new ResultSetMapperImpl(entityPersister);
    when(resultSet.getObject("id")).thenReturn(1);
    when(resultSet.getObject("first_name")).thenReturn("Joshua");
    when(resultSet.getObject("last_name")).thenReturn("Bloch");
    when(resultSet.getObject("birthday")).thenReturn(java.sql.Date.valueOf("2000-01-01"));
    when(entityPersister.findOneBy(Address.class, "person_id", 1))
        .thenReturn(Optional.of(getTestAddress()));

    PersonWithOneToOne personWithOneToOne = mapper.mapToEntity(resultSet, PersonWithOneToOne.class);
    assertEquals(1, personWithOneToOne.getId());
    assertEquals("Joshua", personWithOneToOne.getFirstName());
    assertEquals("Bloch", personWithOneToOne.getLastName());
    assertEquals(LocalDate.parse("2000-01-01"), personWithOneToOne.getBirthday());
    assertEquals(getTestAddress(), personWithOneToOne.getAddress());
  }

  private Address getTestAddress() {
    Address testAddress = new Address();
    testAddress.setId(10L);
    testAddress.setCity("Lviv");
    testAddress.setStreet("Shevchenko");
    return testAddress;
  }

}
