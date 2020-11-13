import java.util.HashSet;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.random;

/*

Система гексагональных координат: 3 координатные оси, третья, z, может быть вычислена из x и у.
Координаты отсчитываются от центра. Центр карты - пересечение линий, соединяющий середины противоположных сторон.
Рядов в карте с сторонами a и b - a + b - 1
       3:3                    5:2                  5:3
            Y
    | | | |0|                | | | Y             -4| | |
   | | | |0| |              | | |0|             -3| | |1|
  | | | |0| | |            | | |0| |           -2| | |1| |
 |0|0|0|0|0|0|0| X        |0|0|0|0|0| X       -1| | |1| | |
  | | |0| | |1|          | | |0| | | |       |1|1|1|1|1|1|1|
   | |0| | |2|            | |0| | | |         | -1|1| | | |
    |0|1|2|3|                                  -1|1|2|3|4|

Шестиугольная карта может быть с равными и неравными краями. Карта с равными краями - просто шестиугольник из шестиугольников.
Карта с неравными краями имеет три пары краев, один из которых больший, а второй меньший. При этом такая карта
имеет центральный гекс с координатами 0,0 в том случае, если разница между размерами краев кратна 3. Карта с равными краями
- вырожденный случай с разницей равной нулю, 0%3 = 0, центр есть всегда.

Центральный гекс такой карты с большой границей а и меньшей b находится на a - 2/3(a-b) ряду от большой стороны.
Соответственно, координата Y в первой клетке первого ряда будет
    Y = a - 2/3(a-b) - 1
А координата X
    X= (a + b - 1) - (a - 2/3(a-b)) =>
    X = b + 2/3(a-b) - 1

В общем случае с такими условиями (при наличии центрального гекса его координаты 0:0, без него - нулевые координаты отсутствуют)
координата крайнего ряда большой стороны будет
    (а + 2b)/3
а крайнего ряда малой стороны
    (b + 2a)/3

В случае, когда разница длин краев не делится на 3, центр карты будет в точке соединения трех гексов с координатами 1:1,
-1:1, 1:-1, в этом случае на карте нет нулевых линий, за -1 идет 1.
 */

public class TripleMap extends BattleMap{


    class Hex extends BattleMap.Hex{
        private int z;
        public int getZ() {
            return z;
        }

        Hex (int x,int y){
            super(x,y);
            this.z = -x - y;
            if (x > bC) System.out.println("x > "+bC);
            if (x < -fC) System.out.println("x < -"+fC);
            if (y > bC) System.out.println("y > "+bC);
            if (y < -fC) System.out.println("y < -"+fC);
            if (z > bC) System.out.println("z > "+bC);
            if (z < -fC) System.out.println("z < -"+fC);

        }
        @Override
        public String toString(){
            return "x: " + getX() + ", y: " + getY() + ", z: " + getZ();
        }
    }

    HashSet<Hex> hexes;

    int fB;   // faceBoard - number of hexes in side at which player sits
    int bB;   // backBoard - number of hexes in far side from the player
    int fC;   // faceToCenter - number of rows from player's side to center
    int bC;   // backToCenter - number of rows from far side to center
    boolean skipZeroRow;// true if map has central hex, false if center is between hexes
    int numberOfHexes;  // number of map's hexes for random

    TripleMap(int fB, int bB) {

        this.fB = fB;
        this.bB = bB;
        fC = (fB + 2*bB)/3 - 1;
        bC = (bB + 2*fB)/3 - 1;
        hexes = new HashSet<>();

        numberOfHexes = (int)(pow(fB + bB - 1, 2) - (pow(fB,2) - fB + pow(bB,2) - bB)/2);

        if ((fB - bB) % 3 == 0) skipZeroRow = false;
        else{
            System.out.println("Если (fB - bB)%3 != 0 - карта не имеет центрального гекса, ничего не работает");
            skipZeroRow = true;
        }

        System.out.println("faceBoard: \t" + fB);
        System.out.println("backBoard: \t" + bB);
        System.out.println("faceToCenter:\t" + fC);
        System.out.println("backToCenter:\t" + bC);
        System.out.println("skipZeroRow:\t" + skipZeroRow);
        System.out.println("numberOfHexes:\t" + numberOfHexes);
    }

    void addHex(int x, int y){
        hexes.add(new Hex(x,y));
    }

    void printHexes(){
        int i = 1;
        for (Hex hex:hexes) {
            System.out.println("" + i++ + ": " + hex);
        }
    }

    void checkHex(int x, int y){
        if (hexes.contains(new Hex(x,y))) System.out.print("Contains "+x+":"+y);
        else System.out.print("No hex " + x + ":"+y);
    }

    void drawMap() {
        for (int i = 0; i < fB; i++) {
            for (int j = 0; j < fB - i; j++) System.out.print(" ");
            for (int j = 0; j < bB + i; j++) System.out.print("| ");
            System.out.println("|");
        }
        for (int i = 2; i <= bB; i++) {
            for (int j = 0; j < i; j++) System.out.print(" ");
            for (int j = 0; j < fB + bB - i; j++) System.out.print("| ");
            System.out.println("|");
        }
    }

     void drawMapC() {


        if(skipZeroRow){
            System.out.println("Если (fB - bB)%3 != 0 - карта не имеет центрального гекса, ничего не работает");

            /*for (int i = -bC; i < -bC + fB; i++) {
                for (int j = 0; j < fB - bC - i; j++) System.out.print(" ");
                for (int j = -fC; j < -fC + bB + bC + i; j++) System.out.print("|" + ((i==0 || j == 0)?"0":" "));
                System.out.println("|");
            }

            System.out.println();

            for (int i = -bC + fB; i < fC; i++) {
                for (int j = 0; j < i + 1; j++) System.out.print(" ");
                for (int j = - fC + i + 1; j < bC; j++) System.out.print("|" + ((i==0 || j == 0)?"0":" "));
                System.out.println("|");
            }*/
        }else {
            for (int i = bC; i > bC - fB; i--) {
                for (int j = bC - fB; j < i - 1; j++) System.out.print(" ");
                for (int j = -fC; j < -fC + bB + bC - i; j++) {
                    System.out.print("|");
                    if (hexes.contains(new Hex(i,j))) System.out.print("X");
                    if (i==0 || j == 0) System.out.print("0");
                    else System.out.print(" ");
                }
                System.out.println("|");
            }

            //System.out.println();

            for (int i = bC - fB; i >= -fC; i--) {
                for (int j = bC - fB; j > i - 1; j--) System.out.print(" ");
                for (int j = -fC + bC - fB - i + 1; j < bC + 1; j++){
                    System.out.print("|");
                    if (hexes.contains(new Hex(i,j))) System.out.print("X");
                    if (i==0 || j == 0) System.out.print("0");
                    else System.out.print(" ");
                }
                System.out.println("|");
            }
        }
    }

    Hex getRandomHex(int distance) {
        /*
        Для равномерной генерации нужно выбирать не один из координат, а затем вторую, т.к. число клеток в рядо может быть
        разным. Нужен выбор одной из всего числа клеток. Получаем номер клетки, дальше надо рассчитать ее координаты.
        */

        while (true) {
            int hexNumber = (int)(random() * numberOfHexes);
            int halfHex = (fB + bB - 1)*(fB + bB)/2 - fB*(fB-1)/2;
            int rowLength = fB+bB-2;
            int x;
            int y;
            if (hexNumber < halfHex){
                x = halfHex - hexNumber;
                y = bC - fB + 1;

                while (x > bB){
                    y++;
                    x-= rowLength--;
                }
            }else{
                x = hexNumber - halfHex;
                y = bC - fB + 1;
                while (x > fB){
                    y--;
                    x-= rowLength--;
                }
            }
            return new Hex(x,y);
            /*for (BattleMap.Hex hex : hexes) {
                if (abs(x - hex.getX()) <= distance && abs(y - hex.getY()) <= distance) {
                    return new Hex(x, y);
                }
            }*/
        }
    }
}
