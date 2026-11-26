package Scheduler;
import java.util.ArrayList;
import java.util.List;

public class Tier2Scheduler {
    private List<CPUprocess> tier2Processes = new ArrayList<>();

    public void addProcess(CPUprocess process, int currtime){
        tier2Processes.add(process);
        process.Tier2EnterTime = currtime;
    }


    public CPUprocess gethighestPriorityProcess(int currtime){
        if (tier2Processes.isEmpty()) return null;
        Aging.aging(tier2Processes, currtime);

        CPUprocess highestPriorityProcess = tier2Processes.get(0);
        for (CPUprocess process : tier2Processes) {
            if (process.priority < highestPriorityProcess.priority) {
                highestPriorityProcess = process;
            }
        }
        tier2Processes.remove(highestPriorityProcess);
        return highestPriorityProcess;
    }

    public List<CPUprocess> getTier2Processes() {
        return tier2Processes;
    }


}
