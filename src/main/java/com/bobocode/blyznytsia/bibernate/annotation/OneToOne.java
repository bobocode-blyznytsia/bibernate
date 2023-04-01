package com.bobocode.blyznytsia.bibernate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The annotation is used to map one-to-one relationships with using reference types. You can mark
 *  reference fields in your entity class to handle one-to-one relation. Type of this filed must be
 *  entity as well. The annotation can be used for both bidirectional and unidirectional relations.
 *  All relations are eagerly fetched.
 * <pre>
 *     Example:
 *
 *     &#064;Entity
 *        public class User {
 *        &#064;Id int id;
 *        ...
 *        &#064;OneToOne(mappedBy="user")
 *        UserProfile profile;
 *     }
 *
 *     &#064;Entity
 *        public class UserProfile {
 *        &#064;Id int id;
 *        ...
 *        &#064;OneToOne(joinColumnName="user_id")
 *        User user;
 *     }
 * </pre>
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {

  /**
   * The name of the foreign key column.
   */
  String joinColumnName() default "";

  /**
   * The field name in entity class that owns the relationship.
   */
  String mappedBy() default "";

}
