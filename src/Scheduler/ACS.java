import java.util.ArrayList;
import java.util.List;

public class ACS {
    private final int quantum;
    private final int cs;
      private final List<CPUprocess> processes;
 private final Tier1Scheduler tier1;

public ACS(int quantum, int cs, List<CPUprocess> processes) {
    this.cs = cs;
    this.quantum = quantum;
    this.processes = new ArrayList<>(processes); 
    this.tier1 = new Tier1Scheduler(quantum, cs, processes);
  
}




}
