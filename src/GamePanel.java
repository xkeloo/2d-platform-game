import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GamePanel extends JPanel implements ActionListener {

    Player player;
    Rectangle start;
    Rectangle end;
    int[][] map = new int[Settings.mapWidth][Settings.mapHeight];
    ArrayList<Enemy> enemies = new ArrayList<>();

    Timer gameTimer;
    ArrayList<Wall> walls = new ArrayList<>();
    int deathCount;
    int numberOfLevel;

    JLabel levelLabel;
    JLabel deathLabel;



    public GamePanel() {

        numberOfLevel = 1;
        deathCount = 0;
        newLevel();

        this.setLayout(null);

        levelLabel = new JLabel("Poziom " + numberOfLevel);
        levelLabel.setFont(new Font("Calibri", Font.PLAIN, 30));
        levelLabel.setBounds(840, 100, 140, 50);

        deathLabel = new JLabel("Liczba prób: " + deathCount);
        deathLabel.setFont(new Font("Calibri", Font.PLAIN, 20));
        deathLabel.setBounds(840, 200, 140, 50);

        this.add(deathLabel);
        this.add(levelLabel);
        numberOfLevel = 1;
        deathCount = 0;
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask(){

            @Override
            public void run() {

                player.set();
                for(Enemy enemy : enemies) {
                    enemy.set();
                }
                repaint();

                if(player.proceedToNextLevel()) {
                    newLevel();
                    numberOfLevel++;
                    levelLabel.setText("Poziom " + numberOfLevel);
                }
            }

        }, 0 , 17);

    }

    public void newLevel() {

        Settings.chance += 0.01;
        Settings.enemiesCount += 2;
        Settings.enemyMaxSpeed += 1;

        walls.clear();
        enemies.clear();

        this.map = makeWalls();

        spawnPlayer();
        spawnEnemies(Settings.enemiesCount);
    }

    public int[][] makeWalls() {


        map = CellularAutomata.generate(map, Settings.chance, Settings.deathLimit, Settings.birthLimit, Settings.loops);

        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[0].length; y++) {
                if(map[x][y] == 1)
                    walls.add(new Wall(x*Settings.tileSize, y*Settings.tileSize));
            }
        }
        return map;
    }

    public void paint (Graphics g) {
        super.paint(g);

        Graphics2D gtd = (Graphics2D) g;
        gtd.setColor(Color.ORANGE);
        gtd.fillRect(start.x, start.y, start.width, start.height);
        gtd.setColor(Color.GREEN);
        gtd.fillRect(end.x, end.y, end.width, end.height);

        player.draw(gtd);
        for(Enemy enemy : enemies)
            enemy.draw(gtd);
        for(Wall wall:walls) wall.draw(gtd);

    }

    public void spawnEnemies(int num) {
        for (int i = 0; i < num; i++) {

            int x, y;
            do {
                x = Settings.rand(0, Settings.mapWidth - 1);
                y = Settings.rand(0, Settings.mapHeight - 1);
            } while (map[x][y] != 0);

            x*=Settings.tileSize;
            y*=Settings.tileSize;

            int type = Settings.rand(0, 2);


            enemies.add(new Enemy(x, y, this, type));
        }
    }

    public void spawnPlayer() {
        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[0].length; y++) {
                if(map[x][y] == 0 && map[x+1][y] == 0 && map[x][y+1] == 0 && map[x+1][y+1] == 0) {
                    for(int _x = map.length - 1; _x >= 0 ; _x--) {
                        for(int _y = map[0].length - 1; _y >= 0; _y--) {
                            if(map[_x][_y] == 0 && map[_x+1][_y] == 0 && map[_x][_y+1] == 0 && map[_x+1][_y+1] == 0) {
                                start = new Rectangle(x*Settings.tileSize, y*Settings.tileSize, Settings.tileSize*2, Settings.tileSize*2);
                                end  = new Rectangle(_x*Settings.tileSize, _y*Settings.tileSize, Settings.tileSize*2, Settings.tileSize*2);
                                player = new Player(x*Settings.tileSize, y*Settings.tileSize, this);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }



    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == 'w') player.keyUp = true;
        if(e.getKeyChar() == 's') player.keyDown = true;
        if(e.getKeyChar() == 'a') player.keyLeft = true;
        if(e.getKeyChar() == 'd') player.keyRight = true;
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyChar() == 'w') player.keyUp = false;
        if(e.getKeyChar() == 's') player.keyDown = false;
        if(e.getKeyChar() == 'a') player.keyLeft = false;
        if(e.getKeyChar() == 'd') player.keyRight = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
