import java.util.HashMap;
import java.util.HashSet;


abstract class BattleMap {
    HashSet<Hex> hexes;

    BattleMap(){
        hexes = new HashSet<>();
    }

    class Hex {
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        private int x;
        private int y;

        Hex(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

