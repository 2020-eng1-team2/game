package marlin.auber.controllers;

import marlin.auber.common.Controller;
import marlin.auber.models.Auber;

public class AuberKeyboardController implements Controller {
    private Auber auber;

    public AuberKeyboardController(Auber auber) {
        this.auber = auber;
    }

    @Override
    public void tick() {

    }
}
