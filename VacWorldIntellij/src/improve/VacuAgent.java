package improve;

import vacworld.VacPercept;
import agent.Action;
import agent.Agent;
import agent.Percept;

public class VacuAgent extends Agent {

    private State iState;
    private Route route;

    public VacuAgent() {
        iState = new State();
        route = new Route(iState);
    }

    @Override
    public void see(Percept p) {
        if (!(p instanceof VacPercept)) {
            System.out.println("Percept is not of type VacPercept");
            return;
        }

        iState.update((VacPercept) p);
    }

    @Override
    public Action selectAction() {
        return route.nextAction();
    }

    @Override
    public String getId() {
        return "VAC-e";
    }
}