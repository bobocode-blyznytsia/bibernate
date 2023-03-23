package com.bobocode.blyznytsia.bibernate.model;

public record EntityKey<T, K>(
    Class<T> entityType,
    K entityId
) {
}
