package marlin.auber.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Component {
    public Entity parent;
    private static final Map<Class<? extends Component>, List<Component>> allInstances = new HashMap<>();

    protected void attach(Entity parent) {
        this.parent = parent;
        List<Component> allInstancesList = allInstances.computeIfAbsent(
            this.getClass(),
            ignored -> new ArrayList<>()
        );
        allInstancesList.add(this);
    }

    public static <T extends Component> Iterable<T> getAllComponentsOfType(Class<T> type) {
        // Safe - we only put in things of the same class
        @SuppressWarnings("unchecked")
        List<T> all = (List<T>) allInstances.get(type);
        return all;
    }
}
