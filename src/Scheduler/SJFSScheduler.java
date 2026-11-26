package Scheduler;
import java.util.ArrayList;
import java.util.List;

public class SJFSScheduler {
    private static class Segment {
        String processName;
        int startTime;
        int endTime;

        Segment(String processName, int startTime, int endTime) {
            this.processName = processName;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public void simulate(List<CPUprocess> processes, int cs) {
        if (processes == null || processes.isEmpty()) {
            System.out.println("No processes to schedule.");
            return;
        }
        if (cs < 0) {
            throw new IllegalArgumentException("Context switching time cannot be negative.");
        }
        for (CPUprocess p : processes) {
            if (p == null || p.arrivalTime < 0 || p.burstTime <= 0) {
                throw new IllegalArgumentException("Invalid process data.");
            }
            p.remainingTime = p.burstTime;
            p.firstStartTime = -1;
            p.finishTime = -1;
        }

        int time = 0;
        int completed = 0;
        CPUprocess lastProcess = null;
        List<Segment> timeline = new ArrayList<>();

        while (completed < processes.size()) {
            CPUprocess selected = findShortestProcess(processes, time, lastProcess);
            if (selected == null) {
                addSegment(timeline, "IDLE", time, time + 1);
                time++;
                lastProcess = null;
                continue;
            }
            if (lastProcess != null && selected != lastProcess && cs > 0) {
                addSegment(timeline, "CS", time, time + cs);
                time += cs;
                selected = findShortestProcess(processes, time, lastProcess);
            }
            if (selected.firstStartTime == -1) {
                selected.firstStartTime = time;
            }

            int startTime = time;
            selected.remainingTime--;
            time++;
            addSegment(timeline, selected.id, startTime, time);

            if (selected.remainingTime == 0) {
                selected.finishTime = time;
                completed++;
            }
            lastProcess = selected;
        }
        printGanttChart(timeline);
        Metrics.printSummaryTable(processes);
    }
    private CPUprocess findShortestProcess(List<CPUprocess> processes, int time,
            CPUprocess currentProcess) {
        CPUprocess shortest = null;
        for (CPUprocess p : processes) {
            if (p.arrivalTime <= time && p.remainingTime > 0) {
                if (shortest == null
                        || p.remainingTime < shortest.remainingTime) {
                    shortest = p;
                }
            }
        }
        if (shortest == null) {
            return null;
        }
        if (currentProcess != null
                && currentProcess.arrivalTime <= time
                && currentProcess.remainingTime > 0
                && currentProcess.remainingTime == shortest.remainingTime) {
            return currentProcess;
        }
        return shortest;
    }
    private void addSegment(List<Segment> timeline, String processName
            , int startTime, int endTime) {
        if (!timeline.isEmpty()) {
            Segment last = timeline.get(timeline.size() - 1);
            if (last.processName.equals(processName)
                    && last.endTime == startTime) {
                last.endTime = endTime;
                return;
            }
        }
        timeline.add(new Segment(processName, startTime, endTime));
    }
    private void printGanttChart(List<Segment> timeline) {
        StringBuilder chart = new StringBuilder();
        chart.append("\nPreemptive SJF (SRTF) Gantt Chart:\n");
        for (Segment segment : timeline) {
            chart.append(segment.processName)
                    .append(" [")
                    .append(segment.startTime)
                    .append("-")
                    .append(segment.endTime)
                    .append("]  ");
        }
        System.out.println(chart);
    }
}
