package improve;

import vacworld.VacPercept;
import agent.Action;
import agent.Agent;
import agent.Percept;

public class VacuAgent extends Agent {

    private State state;
    private Route route;

    public VacuAgent() {
        state = new State();
        route = new Route(state);
    }

    @Override
    public void see(Percept p) {
        // Check that the percept is the right type
        if (!(p instanceof VacPercept)) {
            System.out.println("Percept is not of type VacPercept");
            return;
        }

        // Update the state with the percept seen
        state.updateState((VacPercept) p);
    }

    @Override
    public Action selectAction() {
        return route.nextAction();
    }

    @Override
    public String getId() {
        return "VAC-EC";
    }
}