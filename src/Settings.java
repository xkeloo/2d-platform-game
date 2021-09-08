public class Settings {
    public static int mapWidth = 100;
    public static int mapHeight = 80;
    public static int tileSize = 8;

    //CellualarAutomata data
    public static double chance = 0.45;
    public static int deathLimit = 4;
    public static int birthLimit = 4;
    public static int loops = 10;

    public static int playerMaxSpeed = 2;
    public static int enemyMaxSpeed = 3;
    public static int enemiesCount = 5;

    public static int rand(int min, int max)
    {
        return (int) (Math.random() *(max - min + 1) + min);
    }
}
