package com.bobocode.blyznytsia.bibernate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The annotation is used to map bidirectional one-to-many relationships with using collection
 *  types. Only {@link java.util.List} is supported. This type of relationship requires obligatory
 *  mapping in child entity. Generic type of list must be entity.  All relations are eagerly fetched.
 * <pre>
 *     Example:
 *
 *     &#064;Entity
 *        public class User {
 *        &#064;Id int id;
 *        ...
 *        &#064;OneToMany(mappedBy="user")
 *        List&#060;Note&#062; notes;
 *     }
 *
 *     &#064;Entity
 *        public class Note {
 *        &#064;Id int id;
 *        ...
 *        &#064;ManyToOne(joinColumnName="user_id")
 *        User user;
 *     }
 * </pre>
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {

  /**
   * The field name in child entity class that owns the relationship. Obligatory to set.
   */
  String mappedBy();

}
