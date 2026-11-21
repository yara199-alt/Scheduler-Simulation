package Scheduler;
import java.util.List;

public class Aging {
    public static void aging(List<CPUprocess> tier2Processes, int currtime){
        for(CPUprocess process : tier2Processes){
            int waitingTime = currtime - process.Tier2EnterTime;
            if (waitingTime > 0) {
                int agingTime = waitingTime / 4;
                process.priority=Math.max(1, process.priority - agingTime);
            }
        }
    }
}
