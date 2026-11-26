package Scheduler;
import java.util.List;

public class SJFSScheduler {

    public void simulate(List<CPUprocess> processes, int cs) {

        if (processes == null || processes.isEmpty()) {
            System.out.println("No processes to schedule.");
            return;
        }

        if (cs < 0) {
            throw new IllegalArgumentException("CS cannot be negative.");
        }

        for (CPUprocess p : processes) {

            if (p.burstTime <= 0 || p.arrivalTime < 0) {
                throw new IllegalArgumentException("Invalid process data.");
            }

            p.remainingTime = p.burstTime;
            p.firstStartTime = -1;
            p.finishTime = -1;
        }

        int time = 0;
        int completed = 0;

        CPUprocess lastProcess = null;

        StringBuilder ganttChart = new StringBuilder();

        while (completed < processes.size()) {

            CPUprocess selected = findShortestProcess(
                    processes, time, lastProcess
            );

            if (selected == null) {

                ganttChart.append("IDLE [")
                        .append(time)
                        .append("-")
                        .append(time + 1)
                        .append("] ");

                time++;
                lastProcess = null;

                continue;
            }

            if (lastProcess != null && selected != lastProcess) {

                if (cs > 0) {

                    ganttChart.append("CS [")
                            .append(time)
                            .append("-")
                            .append(time + cs)
                            .append("] ");

                    time += cs;
                }

                selected = findShortestProcess(
                        processes, time, lastProcess
                );
            }

            if (selected.firstStartTime == -1) {
                selected.firstStartTime = time;
            }

            int start = time;

            selected.remainingTime--;

            time++;

            ganttChart.append(selected.id)
                    .append(" [")
                    .append(start)
                    .append("-")
                    .append(time)
                    .append("] ");

            if (selected.remainingTime == 0) {

                selected.finishTime = time;
                completed++;
            }

            lastProcess = selected;
        }

        System.out.println("\nSJF Scheduling:");
        System.out.println(ganttChart);

        printResults(processes);
    }

    private CPUprocess findShortestProcess(
            List<CPUprocess> processes,
            int time,
            CPUprocess currentProcess) {

        int minRemaining = Integer.MAX_VALUE;

        for (CPUprocess p : processes) {

            if (p.arrivalTime <= time && p.remainingTime > 0) {

                if (p.remainingTime < minRemaining) {
                    minRemaining = p.remainingTime;
                }
            }
        }

        if (minRemaining == Integer.MAX_VALUE) {
            return null;
        }

        if (currentProcess != null
                && currentProcess.arrivalTime <= time
                && currentProcess.remainingTime == minRemaining) {

            return currentProcess;
        }

        for (CPUprocess p : processes) {

            if (p.arrivalTime <= time
                    && p.remainingTime == minRemaining) {

                return p;
            }
        }

        return null;
    }

    private void printResults(List<CPUprocess> processes) {

        double totalWaiting = 0;
        double totalTurnaround = 0;
        double totalResponse = 0;

        System.out.println("\nProcess\tWaiting\tTurnaround\tResponse");

        for (CPUprocess p : processes) {

            int turnaroundTime = p.finishTime - p.arrivalTime;
            int waitingTime = turnaroundTime - p.burstTime;
            int responseTime = p.firstStartTime - p.arrivalTime;

            totalWaiting += waitingTime;
            totalTurnaround += turnaroundTime;
            totalResponse += responseTime;

            System.out.println(
                    p.id + "\t"
                            + waitingTime + "\t"
                            + turnaroundTime + "\t\t"
                            + responseTime
            );
        }

        System.out.println("\nAverage Waiting Time: "
                + totalWaiting / processes.size());

        System.out.println("Average Turnaround Time: "
                + totalTurnaround / processes.size());

        System.out.println("Average Response Time: "
                + totalResponse / processes.size());
    }
}
