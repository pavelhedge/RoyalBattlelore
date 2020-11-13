import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Math.abs;
import static java.lang.Math.random;




public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        TripleMap map = new TripleMap(10, 4);
        map.addHex(0,0);
        map.checkHex(0,0);
        map.printHexes();
        map.drawMapC();
        map.drawMap();

    }
}
