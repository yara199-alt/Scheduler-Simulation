

public class CPUprocess {

    String id;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int intialPriority;
    int priority;
    int firstStartTime = -1;
    int finishTime = -1;
    int Tier2EnterTime = -1;

    public CPUprocess(String id, int arrivalTime, int burstTime, int priority) {

        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.intialPriority = priority;
    }
}
