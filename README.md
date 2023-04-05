<p align="center">
    <img src="doc/bibernate.png" width="50%" alt="Bring Framework"/>
</p>

*Bibernate* is custom simplified Object-Relational Mapping framework.

## Main features

## Provided annotations
| Annotation    | Target                  | Description                                                                                                                                |
|:--------------|:------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------|
| `@Entity`     | `TYPE`                  | *The annotation used to specify entity*                                                                                                    |
| `@Id`         | `TYPE`                  | *The annotation used to mark primary key field of an entity. Each entity should contain exactly one field, annotated with such annotation* |
| `@Table`      | `TYPE`                  | *The annotation used to specify a custom table name for an entity*                                                                         |
| `@ManyToOne`  | `FIELD`                 | *The annotation is used to map many-to-one relationships with using reference types*                                                       |
| `@OneToMany`  | `FIELD`                 | *The annotation is used to map bidirectional one-to-many relationships with using collection types*                                        |
| `@OneToOne`   | `FIELD`                 | *The annotation is used to map one-to-one relationships with using reference types*                                                        |


## Get started