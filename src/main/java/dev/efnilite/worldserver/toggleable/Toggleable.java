package dev.efnilite.worldserver.toggleable;

import dev.efnilite.worldserver.WorldServer;
import dev.efnilite.worldserver.util.VisibilityHandler;

public abstract class Toggleable {

    protected VisibilityHandler visibilityHandler;

    public Toggleable() {
        this.visibilityHandler = WorldServer.getVisibilityHandler();
    }
}
