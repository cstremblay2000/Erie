/**
 * A driver class that simulates cars crossing a bridge
 * Initializes bridge, list of cars, and starts car threads.
 *
 * @author ctremblay
 */
public class Erie {

    /** Constant for usage message*/
    private static final String USAGE_MESSAGE = "Usage: java Erie number-of-cars";

    /** Constant for random number seed */
    static final int SEED = 31;

    /** Car creation message */
    private static final String CAR_CREATION_MESSAGE = "Creating Car Driver %d\n";

    /** South constant */
    private static final String SOUTH = "South";

    /** North constant */
    private static final String NORTH = "North";

    /** Bridge constant */
    private static final String BRIDGE = "Bridge";

    /** Left arrow */
    public static final String LEFT_ARROW = "<==";

    /** Right arrow */
    public static final String RIGHT_ARROW = "==>";

    /** Header string */
    public static final String MESSAGE = "%-29s%-15s%s\n";

    /**
     * The driver program
     * @param args program arguments
     */
    public static void main(String[] args) {

        // check that there is an argument
        if( args.length < 1 ){
            printUsageMessage();
            System.exit(1);
        }

        // check that argument is an integer
        int numCars = 0;
        try{
            numCars = Integer.parseInt(args[0]);
        } catch (Exception e){
            //System.err.println(e.getMessage());
            printUsageMessage();
            System.exit(1);
        }

        // Initialize array of cars
        Car[] cars = new Car[numCars];
        Bridge bridge = new Bridge(cars);

        // create array of cars and start them
        for(int i = 0; i < numCars; i++ ){
            Car.Travelling direction = (i%2!=0) ? Car.Travelling.SOUTHBOUND : Car.Travelling.NORTHBOUND;
            System.out.printf(Erie.CAR_CREATION_MESSAGE, i);
            cars[i] = new Car(i, direction, bridge);
            cars[i].start();
        }
        bridge.beginSimulation();

        System.out.printf(MESSAGE, SOUTH,BRIDGE+" "+RIGHT_ARROW, NORTH);

        // join the threads
        for( Car car : cars ) {
            try {
                car.join();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

        System.out.println("simulation finished");
    }

    /**
     * Prints out usage message
     */
    public static void printUsageMessage(){
        System.out.println(USAGE_MESSAGE);
    }
}
