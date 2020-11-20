import static java.lang.Math.abs;


public class Main {
    public static void main(String[] args) {
        TripleMap map = new TripleMap(10, 4);
        System.out.println("xFactor: " + map.xFactor);
        map.draw();
        while(true) {
            map.addRandomHex(2);
            map.draw();
            map.printHexes();
            System.out.println();

            System.out.println("zC: " + map.zC.getX() + ":" + map.zC.getY() + ":" + map.zC.getZ());
        }

       /* while(true) {
            Scanner scan = new Scanner(System.in);
        }*/

    }
}
