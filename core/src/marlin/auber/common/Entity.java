package marlin.auber.common;

import java.util.*;

public class Entity {
    private final String id;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    private static final Map<String, Entity> entities = new HashMap<>();

    public Entity(String id) {
        this.id = id;
    }

    public static Entity create(String id, Component... components) {
        Entity e = new Entity(id);
        entities.put(id, e);
        for (Component comp : components) {
            e.attachComponent(comp);
        }
        return e;
    }

    public void attachComponent(Component comp) {
        this.components.put(comp.getClass(), comp);
        comp.attach(this);
    }

    public <T extends Component> T getComponent(Class<T> clazz) {
        // Safe - we ensure that we only insert matching class types
        @SuppressWarnings("unchecked")
        T result = (T) this.components.get(clazz);
        return result;
    }

    public boolean hasComponent(Class<? extends Component> clazz) {
        return this.components.containsKey(clazz);
    }

    public String getId() {
        return id;
    }

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
