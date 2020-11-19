package marlin.auber.components;

import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Component;

public class AABB extends Component {
    public Vector2 size;
    public int tags;

    public static final int TAG_RENDER = 1 << 1;
    public static final int TAG_COLLISION = 1 << 2;
    public static final int TAG_COLLISION_X_ONLY = 1 << 3;

    public AABB(float w, float h) {
        this(w, h, 0);
    }
    public AABB(float w, float h, int tags) {
        this.size = new Vector2(w, h);
        this.tags = tags;
    }

    public boolean hasTag(int tag) {
        return (this.tags & tag) == tag;
    }
}
