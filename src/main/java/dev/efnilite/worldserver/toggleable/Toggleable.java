package dev.efnilite.worldserver.toggleable;

import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.util.VisibilityHandler;

public abstract class Toggleable {

    protected boolean enabled;
    protected VisibilityHandler visibilityHandler;

    public Toggleable(boolean enabled) {
        this.enabled = enabled;
        this.visibilityHandler = WorldServer.getVisibilityHandler();
    }

    public boolean isEnabled() {
        return enabled;
    }
}
