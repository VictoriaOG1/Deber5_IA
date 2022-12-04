
package myagent;

import vacworld.VacPercept;
import agent.Action;
import agent.Agent;
import agent.Percept;


public class VacAgent extends Agent
{
    private final String ID = "1";

    // Este tiene el estado, la posicion, y direccion
    private InternalState vacuumState;

    //Ayuda a escoger el siguiente movimiento
    private Path path;

    //Constrcutor de la clase
    public VacAgent()
    {
        vacuumState = new InternalState();
        path = new Path(vacuumState);
    }

    @Override
    public void see(Percept p)
    {
        // Update the internal state with the percept
        vacuumState.update((VacPercept) p);
    }

    @Override
    public Action selectAction()
    {
        //Escoger la siguiente acción
        return path.nextAction();
    }

    @Override
    public String getId()
    {
        return ID;
    }
}
