package com.example.leodw.worldepth.dagger;

import com.example.leodw.worldepth.WorldepthApplication;

/**
 * Base class for initializeing and accessing the Dagger Component Graph
 * This is the only singleton implementation in the project. The res of the singleton business logic managers
 * are created and handled/scoped by dagger.
 * Created by becze on 12/15/2015.
 */
public class DaggerManager {

    private static class Holder {
        static final DaggerManager INSTANCE = new DaggerManager();
    }

    /**
     * @return the Gagger generate graph
     */
    public static DaggerComponentGraph component() {
        return getInstance().graph;
    }

    public static DaggerManager getInstance() {
        return Holder.INSTANCE;
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    /**
     * Dagger component graph
     */
    private DaggerComponentGraph graph;

    // Just make it private
    private DaggerManager() {
    }

    /**
     * Rebuilds the dagger generated object graph
     */
    public void buildComponentAndInject(WorldepthApplication app) {
        graph = DaggerMainComponent.Initializer.init(app);
        graph.inject(app);
    }

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // GETTERS / SETTTERS
    // ------------------------------------------------------------------------
}
