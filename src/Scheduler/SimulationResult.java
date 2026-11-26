package Scheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SimulationResult {
    public static class TimeStepLog {
        int time;
        String runningProcess;
        String tier1State;
        String tier2State;
        String extraNotes; 
        public TimeStepLog(int time, String runningProcess, String tier1State, String tier2State, String extraNotes) {
            this.time = time;
            this.runningProcess = runningProcess;
            this.tier1State = tier1State;
            this.tier2State = tier2State;
            this.extraNotes = extraNotes;
        }
    }
    private String algorithmName;
    private List<TimeStepLog> timeline;
    private List<CPUprocess> completedProcesses;
    public SimulationResult(String algorithmName) {
        this.algorithmName = algorithmName;
        this.timeline = new ArrayList<>();
        this.completedProcesses = new ArrayList<>();
    }
    public void logTimeStep(int time, String runningProcess, Queue<CPUprocess> tier1, List<CPUprocess> tier2, String notes) {
        String t1Str = formatQueue(tier1);
        String t2Str = formatList(tier2);
        timeline.add(new TimeStepLog(time, runningProcess, t1Str, t2Str, notes));
    }
    private String formatQueue(Queue<CPUprocess> queue) {
        if (queue == null || queue.isEmpty()) {
            return "Tier 1: [Empty]";
        }
        StringBuilder sb = new StringBuilder("Tier 1: [");
        int i = 0;
        for (CPUprocess p : queue) {
            sb.append(p.id)
              .append(" (remaining: ").append(p.remainingTime)
              .append(", Priority: ").append(p.priority).append(")");
            if (i < queue.size() - 1) sb.append(", ");
            i++;
        }
        sb.append("]");
        return sb.toString();
    }
    private String formatList(List<CPUprocess> list) {
        if (list == null || list.isEmpty()) {
            return "Tier 2: [Empty]";
        }
        StringBuilder sb = new StringBuilder("Tier 2: [");
        for (int i = 0; i < list.size(); i++) {
            CPUprocess p = list.get(i);
            sb.append(p.id)
              .append(" (remaining: ").append(p.remainingTime)
              .append(", Priority: ").append(p.priority).append(")");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    public void setCompletedProcesses(List<CPUprocess> processes) {
        this.completedProcesses = processes;
    }
    public List<CPUprocess> getCompletedProcesses() {
        return completedProcesses;
    }
    public List<TimeStepLog> getTimeline() {
        return timeline;
    }
    public void printTimelineAndMatrices() {
        System.out.println("=========================================================================");
        System.out.println("Execution Timeline & Heuristic Demotion Matrices (" + algorithmName + ")");
        System.out.println("=========================================================================");
        System.out.printf("%-6s | %-12s | %s%n", "Time", "Running", "Queues State & Events");
        System.out.println("-------------------------------------------------------------------------");
        for (TimeStepLog log : timeline) {
            if (log.extraNotes != null && !log.extraNotes.isEmpty()) {
                System.out.printf("%-6d | %-12s | %s%n", log.time, log.runningProcess, log.extraNotes);
            }
            System.out.printf("%-6d | %-12s | %s%n", log.time, log.runningProcess, log.tier1State);
            if (log.tier2State != null && !log.tier2State.isEmpty()) {
                System.out.printf("%-6s | %-12s | %s%n", "", "", log.tier2State);
            }
            System.out.println("-------------------------------------------------------------------------");
        }
    }
}