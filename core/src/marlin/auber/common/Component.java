package marlin.auber.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Component {
    public Entity parent;
    private static final Map<Class<? extends Component>, List<Component>> allInstances = new HashMap<>();

    /**
     * Attach this component to a given entity.
     *
     * <b>This should only be called from within {@link Entity}!</b>
     * @param parent the entity to attach to
     */
    protected void attach(Entity parent) {
        this.parent = parent;
        List<Component> allInstancesList = allInstances.computeIfAbsent(
            this.getClass(),
            ignored -> new ArrayList<>()
        );
        allInstancesList.add(this);
    }
}
