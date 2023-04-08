package com.bobocode.blyznytsia.bibernate.transaction;

import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.ACTIVE;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.COMMITTED;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.FAILED_COMMIT;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.FAILED_ROLLBACK;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.NOT_ACTIVE;
import static com.bobocode.blyznytsia.bibernate.transaction.TransactionStatus.ROLLED_BACK;

import com.bobocode.blyznytsia.bibernate.exception.TransactionException;
import com.bobocode.blyznytsia.bibernate.session.Session;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link Transaction}.
 */
@Slf4j
public class TransactionImpl implements Transaction {
  private final Connection connection;
  private final Session session;

  private TransactionStatus status;

  public TransactionImpl(Connection connection, Session session) {
    this.connection = connection;
    this.session = session;
    this.status = NOT_ACTIVE;
  }

  /**
   * (non-Javadoc)
   *
   * @see Transaction#begin() for more information
   */
  @Override
  public void begin() {
    if (status == ACTIVE) {
      throw new IllegalStateException("Transaction is already active");
    }
    log.debug("Begin transaction");
    try {
      connection.setAutoCommit(false);
      status = ACTIVE;
    } catch (SQLException e) {
      throw new TransactionException("Error occurred while transaction beginning", e);
    }
  }

  /**
   * (non-Javadoc)
   *
   * @see Transaction#commit() for more information
   */
  @Override
  public void commit() {
    if (status != ACTIVE) {
      throw new IllegalStateException("Can't commit not active transaction");
    }
    log.debug("Commit transaction");
    try {
      session.flush();
      connection.commit();
      status = COMMITTED;
    } catch (SQLException e) {
      status = FAILED_COMMIT;
      throw new TransactionException("Error occurred while transaction committing", e);
    }
  }

  /**
   * (non-Javadoc)
   *
   * @see Transaction#rollback() for more information
   */
  @Override
  public void rollback() {
    if (!canRollback()) {
      throw new IllegalStateException(
          "Cannot rollback transaction with status %s".formatted(status));
    }
    log.debug("Rollback transaction");
    try {
      connection.rollback();
      status = ROLLED_BACK;
    } catch (SQLException e) {
      status = FAILED_ROLLBACK;
      throw new TransactionException("Error occurred while transaction rollback", e);
    }
  }

  /**
   * (non-Javadoc)
   *
   * @see Transaction#isActive() for more information
   */
  @Override
  public boolean isActive() {
    return ACTIVE == getStatus();
  }

  @Override
  public TransactionStatus getStatus() {
    return status;
  }

  private boolean canRollback() {
    return status == ACTIVE || status == FAILED_COMMIT;
  }
}
