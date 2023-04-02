package com.bobocode.blyznytsia.bibernate.session;

/**
 * Implementations of this interface are responsible for creating instances of a {@link Session}.
 * Usually application has one instance of a {@link SessionFactory}.
 */
public interface SessionFactory {

  /**
   * Creates new {@link Session} instance each time when it's invoked
   *
   * @return the {@link Session} instance
   */
  Session openSession();

  /**
   * Returns true if {@link SessionFactory} is open.
   * Returns true until the factory has been closed.
   *
   * @return boolean indicating whether the factory is open
   */
  boolean isOpen();

  /**
   * Closes {@link SessionFactory} and all acquired sessions
   */
  void close();

}
