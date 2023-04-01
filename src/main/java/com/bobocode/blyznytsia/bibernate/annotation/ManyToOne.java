package com.bobocode.blyznytsia.bibernate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The annotation is used to map many-to-one relationships with using reference types. You can mark
 *  reference fields in your entity class to handle many-to-one relation. Type of this filed must
 *  be entity as well. All relations are eagerly fetched.
 * <pre>
 *     Example:
 *
 *     &#064;Entity
 *        public class User {
 *        &#064;Id int id;
 *        ...
 *     }
 *
 *     &#064;Entity
 *        public class Note {
 *        &#064;Id int id;
 *        ...
 *        &#064;ManyToOne(joinColumnName=user_id)
 *        User user;
 *     }
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {

  /**
   * The name of the foreign key column. Obligatory to set.
   */
  String joinColumnName();

}
