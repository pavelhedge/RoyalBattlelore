import java.util.HashSet;
import java.util.Scanner;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.random;
import static java.lang.Math.ceil;

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

Математика для подсчета случайных гексов:
    Формула для счета суммы чисел от N до 1: N*(N+1)/2, от N до M включительно: N*(N+1)/2 - (M-1)*M/2
Определение числа гексов (range) в правильном шестиугольнике (a = fB == bB):
длина наибольшего (среднего) ряда клеток в шестиугольнике: b = 2*a - 1
Для вычисления площади нужно взять два раза сумму от b (от среднего ряда в обе стороны число клеток уменьшается),
вычесть из нее b, т.к. средний ряд только один и вычесть два раза сумму от a-1.
    2*(b*(b+1)/2) - b - 2*((a-1)*a/2) => 3*a*(a - 1) + 1
Определение нулевого гекса в правильном шестиугольнике: соответственно, т.к. фигура симметрична, будет
    3*a*(a-1)/2

Определение числа гексов для карты с fB != bB:

Длина наибольшего ряда карты: mR = fB + bB - 1
Тогда по тому же принципу число гексов карты (range) будет равно двум суммам от mR минус mR минус
сумма от bB-1 минус сумму от fB-1
    2*(mR+1)*mR/2 - mR - bB*(bB-1)/2 - fB(fB-1)/2 =>
    2*(fB + bB -1)*(fB + bB -1) - (bB*bB - bB + fB*fB - fB)/2
Т.к. карта неправильной формы, положение центрального гекса с координатами 0:0 и среднего гекса наибольшего ряда
будут отличаться.
Положение центрального гекса (0:0), если он есть на карте (если (fB - bB)%3 == 0), в range можно найти от положения
центрального гекса в правильном шестиугольнике. Найти сторону правильного шестиугольника в
        hB = fB -(fB - bB)*2/3, если fB > bB
Тогда номер центрального гекса карты будет равен сумме центрального гекса шестиугольника со стороной hB и части карты,
которая выдается за его пределы:
        3*hB*(hB - 1)/2 + (hB*(hB - 1) - bB*(bB-1))/2 =>
        2*hB*(hB - 1) - bB*(bB - 1)/2


 */

public class TripleMap extends BattleMap{

    enum Zone {BIGGER, INTERSECTING, SMALLER}

    Zone zone = Zone.BIGGER;

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

        @Override
        public int hashCode() {
            return x*xFactor + y;
        }
    }

    HashSet<Hex> hexes;

    int zS;
    Hex zC = new Hex(0,0);
    int range;          // number of available map's hexes for random

    int fB;             // faceBoard - number of hexes in side at which player sits
    int bB;             // backBoard - number of hexes in far side from the player
    int fC;             // faceToCenter - number of rows from player's side to center
    int bC;             // backToCenter - number of rows from far side to center
    boolean skipZeroRow;// true if map has central hex, false if center is between hexes
    int xFactor;        // factor for X in overriden hashCode function in Hex. Next prime number from bigger border

    TripleMap(int fB, int bB) {

        this.fB = fB;
        this.bB = bB;
        fC = (fB + 2*bB)/3 - 1;
        bC = (bB + 2*fB)/3 - 1;
        hexes = new HashSet<>();

        range = (int)(pow(fB + bB - 1, 2) - (pow(fB,2) - fB + pow(bB,2) - bB)/2);

        // Посчитать множитель для вычисления хэша гексов
        xFactor = fB > bB? fB : bB;
        zS = xFactor;
        while (true){
            boolean isPrime = true;
            for (int i = 2; i < xFactor/2; i++) {
                if (xFactor % i == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime == false) xFactor++;
            else break;
        }

        if ((fB - bB) % 3 == 0) skipZeroRow = false;
        else{
            System.out.println("Если (fB - bB)%3 != 0 - НЕТ ЦЕНТРАЛЬНОГО ГЕКСА, ТАК НЕ РАБОТАЕТ");
            skipZeroRow = true;
        }

        System.out.println("faceBoard: \t" + fB);
        System.out.println("backBoard: \t" + bB);
        System.out.println("faceToCenter:\t" + fC);
        System.out.println("backToCenter:\t" + bC);
        System.out.println("skipZeroRow:\t" + skipZeroRow);
        System.out.println("range of hexes:\t" + range);
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

    void drawHex(Hex hex){
        if (hexes.contains(hex)) System.out.print("X");
        else if(hex.y == 0 && hex.x == 0) System.out.print("0");
        else if (!checkZone(hex)) System.out.print("-");
        else if (hex.x == 0 || hex.y == 0 || hex.z == 0) System.out.print(" ");
        else System.out.print(" ");
    }

    void draw() {
        if(skipZeroRow){
            System.out.println("Если (fB - bB)%3 != 0 - НЕТ ЦЕНТРАЛЬНОГО ГЕКСА, ТАК НЕ РАБОТАЕТ");

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
            for (int i = 0; i < fB + 4; i++) System.out.print(" ");
            for (int i = -fC; i <= bC; i++) System.out.printf("%2d", i);
            System.out.println();
            for (int i = bC; i > bC - fB; i--) {
                System.out.printf("%3d ", i);
                for (int j = bC - fB; j < i - 1; j++) System.out.print(" ");
                for (int j = -fC; j < -fC + bB + bC - i; j++) {
                    System.out.print("|");
                    Hex hex = new Hex(i,j);
                    drawHex(hex);
                }
                System.out.println("|");
            }
            for (int i = bC - fB; i >= -fC; i--) {
                System.out.printf("%3d ", i);
                for (int j = bC - fB; j > i - 1; j--) System.out.print(" ");
                for (int j = -fC + bC - fB - i + 1; j < bC + 1; j++){
                    System.out.print("|");
                    Hex hex = new Hex(i,j);
                    drawHex(hex);
                }
                System.out.println("|");
            }
        }
    }

    boolean checkZone(Hex hex){
        if (hex.x < zC.x - zS + 1 || hex.x > zC.x + zS - 1
                || hex.y < zC.y - zS + 1 || hex.y > zC.y + zS - 1
                || hex.z < zC.z - zS + 1 || hex.z > zC.z + zS - 1 )
        {
            return false;
        }else return true;
    }

    void moveZone(int moveX, int moveY, int moveSize){
        zC.x += moveX;
        zC.y += moveY;
        zC.z += -moveX -moveY;
        zS += moveSize;

        if (zC.x + zS >= bB && zC.x - zS <= fB
                && zC.x + zS >= bB && zC.x - zS <= fB
                && zC.x + zS >= bB && zC.x - zS <= fB) zone = Zone.BIGGER;
        else if (zC.x + zS < bB && zC.x - zS > fB
                && zC.x + zS < bB && zC.x - zS > fB
                && zC.x + zS < bB && zC.x - zS > fB) zone = Zone.SMALLER;
        else zone = Zone.INTERSECTING;
    }

    void changeZone(int moveX, int moveY, int moveSize){
        zC.x = moveX;
        zC.y = moveY;
        zC.z = -moveX -moveY;
        zS = moveSize;

        if (zC.x + zS - 1 >= bC && zC.x - zS + 1<= -fC
                && zC.y + zS - 1 >= bC && zC.y - zS + 1 <= -fC
                && zC.z + zS - 1 >= bC && zC.z - zS + 1 <= -fC)
        {
            zone = Zone.BIGGER;
            range = (int)(pow(fB + bB - 1, 2) - (pow(fB,2) - fB + pow(bB,2) - bB)/2);
        }
        else if (zC.x + zS - 1 <= bC && zC.x - zS + 1 >= -fC
                && zC.y + zS - 1 <= bC && zC.y - zS + 1 >= -fC
                && zC.z + zS - 1 <= bC && zC.z - zS + 1 >= -fC)
        {
            zone = Zone.SMALLER;
            range = 3*zS*(zS - 1) + 1;
        }
        else {
            zone = Zone.INTERSECTING;
            range = 0;
            System.out.println("No realization for zone = Zone.INTERSECTION");
        }

    }

    void addRandomHex(int distance) {
        /*
        Для равномерной генерации нужно выбирать не один из координат, а затем вторую, т.к. число клеток в рядо может быть
        разным. Нужен выбор одной из всего числа клеток. Получаем номер клетки, дальше надо рассчитать ее координаты.
        */


        while (true) {
            int x;
            int y;
            int hexNumber;

            if (zone == Zone.BIGGER) {
                int halfHex = (fB + bB - 1) * (fB + bB) / 2 - bB * (bB - 1) / 2;
                //hexNumber = (int)ceil(random()*range - halfHex);
                hexNumber = new Scanner(System.in).nextInt();
                if (hexNumber < 0) {
                    int rowLength = - fB - bB + 1;
                    y = hexNumber;
                    x = bC - fB + 1;

                    while (y < rowLength) {
                        x++;
                        y -= rowLength++;
                    }
                    y += bC - x - (fB - bB)/3 + 1;
                } else {
                    int rowLength = fB + bB - 2;
                    y = hexNumber;
                    x = bC - fB;
                    while (y >= rowLength) {
                        x--;
                        y -= rowLength--;
                    }
                    y += - fC - x - (fB - bB) / 3;
                }
            } else if (zone == Zone.SMALLER){

                hexNumber = ((int)ceil(random()*range - range/2 - 1));
                int rowLength = 2*zS - 1;
                int zoneY = abs(hexNumber) + zS;
                int zoneX = 0;
                while (zoneY > rowLength){
                    zoneX++;
                    zoneY -= rowLength--;
                }
                zoneY += zoneX - zS;
                x = ((hexNumber > 0)? zoneX : -zoneX) + zC.x;
                y = ((hexNumber > 0)? -zoneY : zoneY) + zC.y;

            } else { // if (zone = Zone.INTERSECTING)
                hexNumber = 0;
                x = 0;
                y = 0;
            }

            boolean isGood = true;
            for (BattleMap.Hex hex : hexes) {
                if (false) { // check if distance from other hexes is fine
                    isGood = true;
                    break;
                }
            }
            if (isGood == true)
            {
                hexes.add(new Hex(x,y));
                System.out.printf("hex#%d - %d:%d\n", hexNumber, x,y );
                break;
            }
        }
    }
}