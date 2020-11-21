package marlin.auber.common;

import java.util.*;

public class Entity {
    private final String id;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    private static final Map<String, Entity> entities = new HashMap<>();

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
        entities.put(id, e);
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
        for (Entity e : entities.values()) {
            if (e.components.keySet().containsAll(compTypes)) {
                result.add(e);
            }
        }
        return result;
    }
}
