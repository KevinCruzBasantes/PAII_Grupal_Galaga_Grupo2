package ec.edu.uce.Galaga.models;

import java.util.List;

public class Level {
    private int levelNumber;
    private List<Opponents> opponents;
    private boolean doubleShot;
    private int enemyDamage;

    public Level(int levelNumber, List<Opponents> opponents, boolean doubleShot, int enemyDamage) {
        this.levelNumber = levelNumber;
        this.opponents = opponents;
        this.doubleShot = doubleShot;
        this.enemyDamage = enemyDamage;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public List<Opponents> getOpponents() {
        return opponents;
    }

    public boolean isDoubleShot() {
        return doubleShot;
    }

    public int getEnemyDamage() {
        return enemyDamage;
    }
}