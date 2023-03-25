package com.bobocode.blyznytsia.bibernate.transaction;

import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.ACTIVE;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.COMMITTED;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.FAILED_COMMIT;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.FAILED_ROLLBACK;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.ROLLED_BACK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.bobocode.blyznytsia.bibernate.exception.TransactionException;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionTest {
  @InjectMocks
  public TransactionImpl transaction;

  @Mock
  private Connection connection;

  @Test
  void beginTransactionTest() throws SQLException {
    transaction.begin();

    verify(connection).setAutoCommit(false);
    assertEquals(ACTIVE, transaction.getStatus());
  }

  @Test
  void beginTransactionWhenTransactionAlreadyActiveTest() {
    transaction.begin();

    assertThrows(IllegalStateException.class, () -> transaction.begin());
  }

  @Test
  void commitTransactionTest() throws SQLException {
    transaction.begin();
    transaction.commit();

    verify(connection).commit();
    assertEquals(COMMITTED, transaction.getStatus());
  }

  @Test
  void commitNotActiveTransactionTest() {
    assertThrows(IllegalStateException.class, () -> transaction.commit());
  }

  @Test
  void commitTransactionFailedTest() throws SQLException {
    transaction.begin();

    doThrow(new SQLException()).when(connection).commit();

    assertThrows(TransactionException.class, () -> transaction.commit());
    assertEquals(FAILED_COMMIT, transaction.getStatus());
  }

  @Test
  void rollbackTransactionTest() throws SQLException {
    transaction.begin();
    transaction.rollback();

    verify(connection).rollback();
    assertEquals(ROLLED_BACK, transaction.getStatus());
  }

  @Test
  void rollbackTransactionFailedTest() throws SQLException {
    transaction.begin();

    doThrow(new SQLException()).when(connection).rollback();

    assertThrows(TransactionException.class, () -> transaction.rollback());
    assertEquals(FAILED_ROLLBACK, transaction.getStatus());
  }

  @Test
  void rollbackNotActiveTransaction() {
    assertThrows(IllegalStateException.class, () -> transaction.rollback());
  }
}