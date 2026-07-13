package com.example.telecom.common.test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Abstract base class for in-memory repository implementations used in tests.
 *
 * <p>Provides thread-safe CRUD operations backed by a {@link ConcurrentHashMap}.
 * Subclasses must implement {@link #getId(Object)} so the base can derive
 * the storage key for any entity.
 *
 * @param <T> the entity type managed by this repository
 */
public abstract class InMemoryRepositoryBase<T> {

    protected final ConcurrentHashMap<String, T> store = new ConcurrentHashMap<>();

    /**
     * Derives the storage key from an entity instance.
     *
     * @param entity the entity to derive an ID from
     * @return the unique identifier for the entity
     */
    public abstract String getId(T entity);

    /**
     * Stores an entity under the given identifier.
     *
     * @param id     the storage key
     * @param entity the entity to store
     * @return the previous entity associated with the key, or {@code null} if none
     */
    public T save(final String id, final T entity) {
        return store.put(id, entity);
    }

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the storage key
     * @return an {@link Optional} containing the entity, or empty if not found
     */
    public Optional<T> findById(final String id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Returns all entities currently stored.
     *
     * @return a list of all entities (never {@code null})
     */
    public List<T> findAll() {
        return List.copyOf(store.values());
    }

    /**
     * Removes the entity with the given identifier.
     *
     * @param id the storage key of the entity to remove
     * @return the removed entity, or {@code null} if no entity was associated with the key
     */
    public T delete(final String id) {
        return store.remove(id);
    }

    /**
     * Returns the number of stored entities.
     *
     * @return entity count
     */
    public long count() {
        return store.size();
    }

    /**
     * Removes all stored entities.
     */
    public void clear() {
        store.clear();
    }

    /**
     * Saves an entity using its own ID (derived via {@link #getId(Object)}).
     *
     * @param entity the entity to store
     * @return the previous entity associated with the key, or {@code null} if none
     */
    public T save(final T entity) {
        return save(getId(entity), entity);
    }

    /**
     * Saves all given entities, each keyed by its own ID.
     *
     * @param entities entities to store
     */
    public void saveAll(final List<T> entities) {
        entities.forEach(this::save);
    }

    /**
     * Finds all entities whose IDs match any of the given keys.
     *
     * @param ids keys to look up
     * @return list of matching entities in iteration order of {@code ids}
     */
    public List<T> findAllById(final List<String> ids) {
        return ids.stream()
                .map(store::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}
