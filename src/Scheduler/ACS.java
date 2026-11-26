

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ACS {
    public static final String CS = "CS";
    public static final String IDLE = "IDLE";

    private final int cs;
    private final List<CPUprocess> procs;   
    private final Tier1Scheduler tier1;
    private final Tier2Scheduler tier2 = new Tier2Scheduler();

    private int time = 0;
    private int finishedCount = 0;
    private CPUprocess lastP = null;     
    private CPUprocess tier2Running = null;  
    private int csLeft = 0;             

    public ACS(int quantum, int cs, List<CPUprocess> processes) {
        if (quantum <= 0)
            throw new IllegalArgumentException("quantum must be > 0");
        if (cs < 0)
            throw new IllegalArgumentException("CS cannot be negative.");
        if (processes == null || processes.isEmpty())
            throw new IllegalArgumentException("No processes to schedule.");

        List<CPUprocess> copies = new ArrayList<>();
        for (CPUprocess p : processes) {
            if (p.burstTime <= 0 || p.arrivalTime < 0)
                throw new IllegalArgumentException("Invalid process data for " + p.id);
            copies.add(new CPUprocess(p.id, p.arrivalTime, p.burstTime, p.intialPriority));
        }

        this.cs = cs;
        this.procs = copies;
        this.tier1 = new Tier1Scheduler(quantum, copies);

        // Arrivals at t = 0 must already be in the queue before the first step()/getter call.
        tier1.addArrivals(0);
    }

    public String step() {
        if (isFinished())
            throw new IllegalStateException("The simulation is already finished.");
        boolean cpuFree = csLeft == 0 && tier2Running == null && !tier1.hasCurrentP();
        if (cpuFree) {
            CPUprocess next;
             //priority for tier1 
            if (!tier1.isIdle()) {
                tier1.dispatch();
                next = tier1.getCurrentP();
            } else {
                next = tier2.gethighestPriorityProcess(time);
                tier2Running = next;          
            }

            if (next == null) {
                //cpu is idle
                lastP = null;             
            } else if (cs > 0 && lastP != null && lastP != next) {
                //if next == lastP, then no cs for it
                csLeft = cs;
            }
        }
        String slot;

        if (csLeft > 0) {
            csLeft--;
            slot = CS;

        } else if (tier2Running != null) {
            slot = tier2Running.id;
            tier2Running.remainingTime--;
            lastP = tier2Running;
            if (tier2Running.remainingTime == 0) {
                tier2Running.finishTime = time + 1;
                finishedCount++;
                tier2Running = null;          
            }

        } else if (tier1.hasCurrentP()) {
            CPUprocess p = tier1.getCurrentP();
            slot = p.id;
            tier1.run(time);
            lastP = p;
            for (CPUprocess d : tier1.Demoted())
                tier2.addProcess(d, time + 1);

            finishedCount += tier1.Finished().size();

        } else {
            //nothing to run -> cpu idle
            slot = IDLE;         
        }

        time++;
        tier1.addArrivals(time);
        return slot;
    }

    public void runAll() {
        while (!isFinished())
            step();
    }

  
    public boolean isFinished() {
        return finishedCount == procs.size();
    }

    //the start of the next time unit that step() will execute
    public int getTime() {
        return time;
    }

    //processes waiting in tier 1
    public Deque<CPUprocess> getTier1Queue() {
        return tier1.getReadyQueue();
    }

    //processes waiting in tier 2
    public List<CPUprocess> getTier2Queue() {
        return new ArrayList<>(tier2.getTier2Processes());
    }
    //get all processes
    public List<CPUprocess> getProcesses() {
    return java.util.Collections.unmodifiableList(procs);
}

}