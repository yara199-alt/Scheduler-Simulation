package Scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

public class Tier1Scheduler {
  private final int quantum;
  private final int  cs;
  private final Deque<CPUprocess> readyQueue = new ArrayDeque<>();
  private CPUprocess currP = null;
  private final  List<CPUprocess> processes;

  private String lastDispatchedId = null;
  private int usedQuantum = 0;
  private int nextArrivalIdx = 0;
  private final List<CPUprocess> demoted = new ArrayList<>();
  private final List<CPUprocess> finished = new ArrayList<>();

  public Tier1Scheduler(int quantum, int cs, List<CPUprocess> allProcesses) {
    if (quantum <= 0)
      throw new IllegalArgumentException("quantum must be > 0");
    this.quantum = quantum;
    this.cs = cs;
    this.processes = new ArrayList<>(allProcesses);
    this.processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
  }
  // add new arrivals in queue
  public void addArrivals(int currTime) {
    while (nextArrivalIdx < processes.size() &&
           processes.get(nextArrivalIdx).arrivalTime <= currTime) {
      readyQueue.addLast(processes.get(nextArrivalIdx));
      nextArrivalIdx++;
    }
  }

  private boolean needsCS = false;
  public boolean needsCS() { return needsCS; }
  public void clearCS() { needsCS = false; }

  // pull the next process from queue
  public void dispatch(int currTime) {
    if (currP != null)
      return;
    if (readyQueue.isEmpty())
      return;

    currP = readyQueue.pollFirst();
    usedQuantum = 0;
    // if there is no prcoess or if the queue has only one process, no need for cs
   needsCS = (cs > 0 && lastDispatchedId != null && !lastDispatchedId.equals(currP.id));
    lastDispatchedId = currP.id;
  }

  public enum State { IDLE, RUNNING, FINISHED, DEMOTED, CS }

  public State run(int currTime) {
    if (currP == null)
      return State.IDLE;

     if (currP.firstStartTime == -1)
      currP.firstStartTime = currTime;

    currP.remainingTime--;
    usedQuantum++;

    if (currP.remainingTime == 0) {
      currP.finishTime = currTime + 1;
      finished.add(currP);
      currP = null;
      usedQuantum = 0;
      return State.FINISHED;
    }

    if (usedQuantum == quantum) {
      currP.Tier2EnterTime = currTime + 1;
      demoted.add(currP);
      currP = null;
      usedQuantum = 0;
      return State.DEMOTED;
    }

    return State.RUNNING;
  }

  public State tick(int currTime) {
    addArrivals(currTime);
    if (currP == null) {
        if(readyQueue.isEmpty()) {
            lastDispatchedId = null;
            return State.IDLE;
        }

      dispatch(currTime);
      if (currP == null)
        return State.IDLE;
      if (needsCS())
        return State.CS;
    }
    return run(currTime);
  }

  public List<CPUprocess> Demoted() {
    List<CPUprocess> out = new ArrayList<>(demoted);
    demoted.clear();
    return out;
  }
  public List<CPUprocess> Finished() {
    List<CPUprocess> out = new ArrayList<>(finished);
    finished.clear();
    return out;
  }

  public boolean isIdle() { return currP == null && readyQueue.isEmpty(); }
  public boolean hasCurrentP() { return currP != null; }
  public CPUprocess getCurrentP() { return currP; }
  public int getQuantum() { return quantum; }
 
}
