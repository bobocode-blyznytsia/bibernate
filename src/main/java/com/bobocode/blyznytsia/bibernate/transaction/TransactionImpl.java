package com.bobocode.blyznytsia.bibernate.transaction;

import com.bobocode.blyznytsia.bibernate.exception.TransactionException;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link Transaction}.
 */
@Slf4j
public class TransactionImpl implements Transaction {
  private final Connection connection;

  private TransactionStatus status;

  public TransactionImpl(Connection connection) {
    this.connection = connection;
    this.status = TransactionStatus.NOT_ACTIVE;
  }

  /**
   * (non-Javadoc)
   *
   * @see Transaction#begin() for more information
   */
  @Override
  public void begin() {
    if (status == TransactionStatus.ACTIVE) {
      throw new IllegalStateException("Transaction is already active");
    }
    log.debug("Begin transaction");
    try {
      connection.setAutoCommit(false);
      status = TransactionStatus.ACTIVE;
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
    if (status != TransactionStatus.ACTIVE) {
      throw new IllegalStateException("Can't commit not active transaction");
    }
    log.debug("Commit transaction");
    try {
      connection.commit();
      status = TransactionStatus.COMMITTED;
    } catch (SQLException e) {
      status = TransactionStatus.FAILED_COMMIT;
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
          String.format("Cannot rollback transaction with status %s", status));
    }
    log.debug("Rollback transaction");
    try {
      connection.rollback();
      status = TransactionStatus.ROLLED_BACK;
    } catch (SQLException e) {
      status = TransactionStatus.FAILED_ROLLBACK;
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
    return TransactionStatus.ACTIVE == getStatus();
  }

  public TransactionStatus getStatus() {
    return status;
  }

  private boolean canRollback() {
    return status == TransactionStatus.ACTIVE || status == TransactionStatus.FAILED_COMMIT;
  }

}