package Scheduler;
import java.util.List;

public class Metrics {
    public static int getTurnaroundTime(CPUprocess p) {
        return p.finishTime - p.arrivalTime;
    }
    public static int getWaitingTime(CPUprocess p) {
        return getTurnaroundTime(p) - p.burstTime;
    }
    public static int getResponseTime(CPUprocess p) {
        return p.firstStartTime - p.arrivalTime;
    }
    public static double AvgTurnaroundTime(List<CPUprocess> processes) {
        if (processes == null || processes.isEmpty()) return 0.0;
        double total = 0;
        for (CPUprocess p : processes) {
            total += getTurnaroundTime(p);
        }
        return total / processes.size();
    }
    public static double AvgWaitingTime(List<CPUprocess> processes) {
        if (processes == null || processes.isEmpty()) return 0.0;
        double total = 0;
        for (CPUprocess p : processes) {
            total += getWaitingTime(p);
        }
        return total / processes.size();
    }
    public static double AvgResponseTime(List<CPUprocess> processes) {
        if (processes == null || processes.isEmpty()) return 0.0;
        double total = 0;
        for (CPUprocess p : processes) {
            total += getResponseTime(p);
        }
        return total / processes.size();
    }
    public static void printSummaryTable(List<CPUprocess> processes) {
        if (processes == null || processes.isEmpty()) {
            System.out.println("No process data available to display metrics.");
            return;
        }
        System.out.println("\n=========================================================================");
        System.out.println("Scheduling Analysis Table");
        System.out.println("=========================================================================");
        System.out.printf("%-10s | %-12s | %-11s | %-15s | %-12s | %-13s%n",
                "Process", "Arrival Time", "Finish Time", "Turnaround Time", "Waiting Time", "Response Time");
        System.out.println("-------------------------------------------------------------------------");
        for (CPUprocess p : processes) {
            int tat = getTurnaroundTime(p);
            int wt = getWaitingTime(p);
            int rt = getResponseTime(p);

            System.out.printf("%-10s | %-12d | %-11d | %-15d | %-12d | %-13d%n",
                    p.id, p.arrivalTime, p.finishTime, tat, wt, rt);
        }
        System.out.println("=========================================================================");
        System.out.printf("Average Turnaround Time: %.2f time units%n", AvgTurnaroundTime(processes));
        System.out.printf("Average Waiting Time:    %.2f time units%n", AvgWaitingTime(processes));
        System.out.printf("Average Response Time:   %.2f time units%n", AvgResponseTime(processes));
        System.out.println("=========================================================================\n");
    }
}