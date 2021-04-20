import java.util.Random;

/**
 * Object representation of a car
 *
 * @author ctremblay
 */
public class Car extends Thread {

    /** An enum to determine which direction car is travelling */
    public enum Travelling{
        NORTHBOUND,
        SOUTHBOUND
    }

    /** Initial wait time */
    private static final int COMMUTE_TIME = 500;

    /** Lower bound wait time */
    private static final int LOWER_WAIT = 500;

    /** Upper bound wait time */
    private static final int UPPER_WAIT = 1500;

    /** The bridge that the car is trying to pass */
    private Bridge bridge;

    /** The id of the car */
    private int id;

    /** Which direction the car is going */
    private Travelling travelling;

    /** The random number generator */
    private Random rand;

    /**
     * Create an instrance of a Car with a known ID and bridge
     * @param id unique
     * @param bridge the shared resource
     */
    public Car( int id, Travelling travelling,Bridge bridge){
        this.bridge = bridge;
        this.id = id;
        this.travelling = travelling;
        this.rand = new Random(Erie.SEED); //get seed from driver program
    }

    @Override
    public void run() {
        // wait for bridge to open
        /*while(!bridge.isSimulationReady()){
            try{
                wait();
            } catch (Exception ignored){}
        }*/

        // delay before entering bridge
        int commuteTime = rand.nextInt(Car.COMMUTE_TIME);
        try {
            sleep(commuteTime);
        } catch (InterruptedException e){
            System.err.println(e.getMessage());
        }

        // enter bridge
        if(this.travelling == Travelling.NORTHBOUND)
            this.bridge.crossToNorth(this.id);
        else
            this.bridge.crossToSouth(this.id);

        // wait while on bridge
        do {
            int bridgeTime = rand.nextInt(UPPER_WAIT - LOWER_WAIT) + LOWER_WAIT;
            try {
                sleep(bridgeTime);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        } while(!bridge.isOnFront(id));

        // exit bridge
        if(this.travelling == Travelling.NORTHBOUND)
            this.bridge.reachedTheNorth(this.id);
        else
            this.bridge.reachedTheSouth(this.id);
    }
}
