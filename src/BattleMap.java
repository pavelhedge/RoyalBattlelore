import java.util.HashMap;
import java.util.HashSet;


abstract class BattleMap {

    HashSet<Hex> hexes;

    BattleMap(){
        hexes = new HashSet<>();
    }

    abstract class Hex {
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        protected int x;
        protected int y;

        Hex(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public abstract int hashCode();

        @Override
        public boolean equals(Object obj){
            if (obj == null) return false;
            if (!(obj instanceof Hex)) return false;
            Hex hex = (Hex)obj;
            return (this.x == hex.x && this.y == hex.y);
        }
    }
}

