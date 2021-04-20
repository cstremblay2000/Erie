import java.lang.reflect.Method;
import java.util.*;

/**
 *
 */
public class Bridge {

    /** Queue of cars going north bound */
    private List<Integer> northBound;

    /** Queue of cars going south bound */
    private List<Integer> southBound;

    /** list of cars actually on bridge */
    private List<Integer> bridge;

    /** max number of cars allowed on the bridge */
    public final int MAX_CARS = 3;

    /** Keep track of number of cars crossed */
    private int carsCrossed;

    /** keep track of whose turn it is */
    List<Integer> turn;

    /** Current direction */
    private Car.Travelling direction;

    /** The array of cars indexed by id*/
    private Car[] cars;

    /** check if simulation is ready */
    private boolean isReady;

    /**
     * Create a new bridge object
     * @param cars the list of car threads
     */
    public Bridge(Car[] cars){
        this.turn = new ArrayList<>();
        this.northBound = new LinkedList<>();
        this.southBound = new LinkedList<>();
        this.bridge = new LinkedList<>();
        this.cars = cars;
        this.direction = null;
        this.carsCrossed = 0;
        this.isReady = false;
    }

    /**
     * Check to see if a car is on front based on it's id
     *
     * @param id the cars id
     * @return true if at front, false if not
     */
    public boolean isOnFront(int id){
        if(bridge.isEmpty())
            return false;
        else
            return bridge.get(0) == id;
    }

    /**
     * Begin the simulation
     */
    public synchronized void beginSimulation(){
        isReady = true;
        this.notifyAll();
    }

    /**
     * Check if simulation is ready
     * @return true if ready, false if not
     */
    public boolean isSimulationReady(){
        return isReady;
    }

    /**
     * Car enters waiting line for the north.
     * Allow it to try and get onto bridge based on crossing criteria
     * @param id car trying to cross
     */
    public synchronized void crossToNorth(int id ){
        // add car to line and print state
        northBound.add( id );
        turn.add( id );

        while(
                bridge.size() >= MAX_CARS ||
                (!southBound.isEmpty() && direction == Car.Travelling.SOUTHBOUND) ||
                (!southBound.isEmpty() && direction == Car.Travelling.NORTHBOUND && !bridge.isEmpty()) ||
                id != northBound.get(0) ||
                direction == Car.Travelling.SOUTHBOUND
        ){
            try{
                wait();
            } catch (Exception ignored){;}
        }


        // try to get onto bridge
        /*while( bridge.size() >= MAX_CARS || // bridge is full
                id != turn.get(0) || // won't cut in front of person
                id != northBound.get(0) || // no place jumping
                direction == Car.Travelling.SOUTHBOUND ||
                southBound.size() == 0
            ){ // bridge hasn't switch directions yet
            try {
                wait();
            } catch (Exception ignored){}
        }*/

        // got onto bridge
        bridge.add(id);
        direction = Car.Travelling.NORTHBOUND;

        // take out of line and print state
        northBound.remove(0);
        turn.remove(0);
        System.out.println(this);
    }

    /**
     * Car enters waiting line for the south.
     * Allow it to try and get onto bridge based on crossing criteria
     * @param id car trying to cross
     */
    public synchronized void crossToSouth( int id ){
        // add car to line and print state
        southBound.add( id );
        turn.add(id);

        // Take care of first car
        while(
                bridge.size() >= MAX_CARS ||
                (!northBound.isEmpty() && direction == Car.Travelling.NORTHBOUND) ||
                (!northBound.isEmpty() && direction == Car.Travelling.SOUTHBOUND && !bridge.isEmpty()) ||
                id != southBound.get(0) ||
                direction == Car.Travelling.NORTHBOUND
        ){
            try{
                wait();
            } catch (Exception ignored){;}
        }

        // try to get onto bridge
        /*while( bridge.size() >= MAX_CARS || // bridge is full
                id != turn.get(0) || // won't cut in front of person
                id != southBound.get(0) || // no place jumping
                direction == Car.Travelling.NORTHBOUND ||
                northBound.size() != 0
                ) { // bridge hasn't switched directions yet
            try {

                wait();
            } catch (Exception ignored) {}
        }*/


        // got onto bridge
        bridge.add(id);
        direction = Car.Travelling.SOUTHBOUND;

        // if third car crossed in this direction, switch direction
        southBound.remove(0);
        turn.remove(0);
        System.out.println(this);
        notifyAll();
    }

    /**
     * Car has successfully crossed to the north
     * @param id id of car the successfully crossed
     */
    public synchronized void reachedTheNorth( int id ){
        // take car off bridge
        bridge.remove(0);

        if(bridge.isEmpty() && !southBound.isEmpty())
            direction = Car.Travelling.SOUTHBOUND;
        else if(bridge.isEmpty())
            direction = null;

        System.out.println(this);
        notifyAll();
    }

    /**
     * Car has successfully crossed to the south
     * @param id id of car the successfully crossed
     */
    public synchronized void reachedTheSouth( int id ){
        // take car off
        bridge.remove(0);

        if(bridge.isEmpty() && !northBound.isEmpty())
            direction = Car.Travelling.NORTHBOUND;
        else if(bridge.isEmpty())
            direction = null;

        System.out.println(this);
        notifyAll();
    }

    /**
     * Print the bridge in a pretty way
     * @return the nicely formatted string
     */
    @Override
    public String toString(){
        // create string of north line
        String bridgeStr;

        if(this.direction == Car.Travelling.SOUTHBOUND){
            List<Integer> bridgeRev = new ArrayList<>(bridge);
            Collections.reverse(bridgeRev);
            bridgeStr = bridgeRev.toString().replace("["," ");
            bridgeStr = bridgeStr.replace("]"," ");
            bridgeStr = Erie.RIGHT_ARROW + bridgeStr;
        } else {
            bridgeStr = bridge.toString().replace("["," ");
            bridgeStr = bridgeStr.replace("]"," ");
            bridgeStr = Erie.LEFT_ARROW + bridgeStr;
        }

        bridgeStr = bridgeStr.replace(",", "");

        // create south bound list
        List<Integer> southBoundRev = new ArrayList<>(southBound);
        Collections.reverse(southBoundRev);
        String southBoundStr = southBoundRev.toString();
        int len = southBoundStr.length();
        if(len > 23) {
            int start = len - 23;
            southBoundStr = southBoundStr.substring(start, len);
        }

        // Create north bound list
        String northBoundStr = northBound.toString();
        len = northBoundStr.length();
        if(len > 28){
            northBoundStr = northBoundStr.substring(0, 28);
        }
        /** Prototype for  */
        String BRIDGE_MESSAGE = "%-26s   %-15s%-30s";
        return String.format(BRIDGE_MESSAGE, "S: "+southBoundStr, bridgeStr, northBoundStr+" :N" );
    }
}
