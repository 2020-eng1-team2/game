package marlin.auber.common;

import java.util.*;

/**
 * <p>An Entity is a container for components. The entity has no smarts itself, beyond
 * keeping track of the components it has, the heavy lifting is done by components and systems.
 *
 * <p><b>Note that currently Entity does not support more than one component of the same type.</b>
 * This may be changed in a future release.
 *
 * <p>To create an entity, use {@link Entity#create} with a unique ID and a set of components.
 *
 * <p>To get an entity, use {@link Entity#getEntityById} if you know the ID
 * or {@link Entity#getAllEntitiesWithComponents} with the components you want.
 * To later get a component, use {@link Entity#getComponent}.
 *
 * <p><b>DO NOT HOLD REFERENCES TO ENTITIES OR COMPONENTS IN ANY OTHER PLACE!</b>
 * Otherwise, you run the risk of NullPointerExceptions as entities and components
 * may come and go at any point in time.
 */
public class Entity {
    private final String id;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    private static final Map<String, Entity> allEntities = new HashMap<>();

    public Entity(String id) {
        this.id = id;
    }

    /**
     * Create a new entity with the given ID and components.
     * @param id the ID of the new entity
     * @param components component instances to attach to it
     * @return the new entity
     */
    public static Entity create(String id, Component... components) {
        Entity e = new Entity(id);
        allEntities.put(id, e);
        for (Component comp : components) {
            e.attachComponent(comp);
        }
        return e;
    }

    /**
     * Attach a component to this entity.
     * @param comp the component to attach
     */
    public void attachComponent(Component comp) {
        this.components.put(comp.getClass(), comp);
        comp.attach(this);
    }

    /**
     * Removes a component from this entity. Once this is called,
     * the component is considered destroyed and can be garbage collected.
     * @param type the type of component to remove
     */
    public void removeComponent(Class<? extends Component> type) {
        Component old = this.components.remove(type);
        if (old != null) {
            old.destroy();
        }
    }

    /**
     * Destroys this entity and all attached components.<br>
     *
     * Once this method has been called, this entity will no longer appear in
     * {@link Entity#getAllEntitiesWithComponents}.
     */
    public void destroy() {
        for (Component comp : this.components.values()) {
            comp.destroy();
        }
        this.components.clear();
        allEntities.remove(this.id);
    }

    /**
     * Get a component of type <i>T</i> attached to this entity.
     * May return null if a component of type <i>T</i> is not attached.
     * @param clazz the class of T - should always be {@code T.class}
     * @param <T> the type of component to retrieve
     * @return the attached component, or null
     */
    public <T extends Component> T getComponent(Class<T> clazz) {
        // Safe - we ensure that we only insert matching class types
        @SuppressWarnings("unchecked")
        T result = (T) this.components.get(clazz);
        return result;
    }

    /**
     * Does this entity have a component of type <i>clazz</i>
     * @param clazz The type of component to check for
     * @return whether a component of type <i>clazz</i> is attached
     */
    public boolean hasComponent(Class<? extends Component> clazz) {
        return this.components.containsKey(clazz);
    }

    public String getId() {
        return id;
    }

    /**
     * Get an Entity, given its ID.
     * @param id the entity ID
     * @return the entity
     */
    public static Entity getEntityById(String id) {
        return allEntities.get(id);
    }

    /**
     * Returns all entities which have all the components in <i>componentTypes</i> attached.
     *
     * If this is called as {@code List<Entity> e = Entity.getAllEntitiesWithComponents(X.class)},
     * one can assume that {@code e.getComponent(X.class)} will not be null.
     *
     * @param componentTypes the components to get
     * @return all entities with attached components of type <i>componentTypes</i>
     */
    @SafeVarargs
    public static List<Entity> getAllEntitiesWithComponents(Class<? extends Component>... componentTypes) {
        List<Entity> result = new ArrayList<>();
        List<Class<? extends Component>> compTypes = Arrays.asList(componentTypes);
        for (Entity e : allEntities.values()) {
            if (e.components.keySet().containsAll(compTypes)) {
                result.add(e);
            }
        }
        return result;
    }
}
