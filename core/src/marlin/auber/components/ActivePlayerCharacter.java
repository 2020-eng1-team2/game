package marlin.auber.components;

import marlin.auber.common.Component;
import marlin.auber.common.Timer;

public class ActivePlayerCharacter extends Component {
    public Timer teleportCooldown = Timer.createTimer(0f);
}
