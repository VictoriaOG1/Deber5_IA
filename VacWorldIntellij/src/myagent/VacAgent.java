
package myagent;

import vacworld.VacPercept;
import agent.Action;
import agent.Agent;
import agent.Percept;

public class VacAgent extends Agent {
    private final String ID = "1";

    // Agent state, position, direction, and map information.
    private InterState vacuumState;

    // Helper for agent to choose what next action do
    private Path path;

    public VacAgent() {
        vacuumState = new InterState();
        path = new Path(vacuumState);
    }

    @Override
    public void see(Percept p) {
        if (!(p instanceof VacPercept)) {
            return;
        }

        // Update the internal state with the percept
        vacuumState.updateS((VacPercept) p);
    }

    @Override
    public Action selectAction() {
        return path.nextAction();
    }

    @Override
    public String getId() {
        return ID;
    }
}
