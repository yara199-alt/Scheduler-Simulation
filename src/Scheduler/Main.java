package Scheduler;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("================ TEST CASE 1 (Assignment Sample: Q=3, CS=0) ================");
        List<CPUprocess> tc1 = new ArrayList<>();
        tc1.add(new CPUprocess("P1", 0, 4, 2));
        tc1.add(new CPUprocess("P2", 1, 7, 3));
        tc1.add(new CPUprocess("P3", 3, 2, 1));
        runACS(cloneList(tc1), 3, 0);
        new SJFSScheduler().simulate(cloneList(tc1), 0);
    }

    private static List<CPUprocess> cloneList(List<CPUprocess> original) {
        List<CPUprocess> copy = new ArrayList<>();
        for (CPUprocess p : original) {
            copy.add(new CPUprocess(p.id, p.arrivalTime, p.burstTime, p.intialPriority));
        }
        return copy;
    }

    public static void runACS(List<CPUprocess> processes, int q, int cs) {
        ACS acs = new ACS(q, cs, processes);
        SimulationResult result = new SimulationResult("ACS (Q=" + q + ", CS=" + cs + ")");
        while (!acs.isFinished()) {
            int currentTime = acs.getTime();
            String slot = acs.step();
            result.logTimeStep(currentTime, slot, acs.getTier1Queue(), acs.getTier2Queue(), "");
        }
        result.setCompletedProcesses(acs.getProcesses());
        result.printTimelineAndMatrices();
        Metrics.printSummaryTable(acs.getProcesses());
    }
}