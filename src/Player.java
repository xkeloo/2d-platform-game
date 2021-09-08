import java.awt.*;

public class Player {
    GamePanel panel;
    int x;
    int y;
    int width;
    int height;

    int startX;
    int startY;

    boolean won;

    double xspeed;
    double yspeed;

    Rectangle hitBox;

    boolean keyUp;
    boolean keyDown;
    boolean keyLeft;
    boolean keyRight;

    public Player(int x, int y, GamePanel panel) {
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;

        this.width = Settings.tileSize;
        this.height = Settings.tileSize;
        this.won = false;


        this.hitBox = new Rectangle(x, y, this.width, this.height);
    }

    public void set() {
        if(keyLeft && keyRight || !keyLeft && !keyRight ) xspeed *=0.8;
        else if (keyLeft && !keyRight) xspeed--;
        else if (keyRight && !keyLeft) xspeed++;

        if(keyUp && keyDown || !keyUp && !keyDown ) yspeed *=0.8;
        else if (keyUp && !keyDown) yspeed--;
        else if (keyDown && !keyUp) yspeed++;


        if(xspeed > 0 && xspeed < 0.75) xspeed = 0;
        if(xspeed < 0 && xspeed > -0.75) xspeed = 0;

        if(xspeed > Settings.playerMaxSpeed) xspeed = Settings.playerMaxSpeed;
        if(xspeed < -Settings.playerMaxSpeed) xspeed = -Settings.playerMaxSpeed;

        if(yspeed > 0 && yspeed < 0.75) yspeed = 0;
        if(yspeed < 0 && yspeed > -0.75) yspeed = 0;

        if(yspeed > Settings.playerMaxSpeed) yspeed = Settings.playerMaxSpeed;
        if(yspeed < -Settings.playerMaxSpeed) yspeed = -Settings.playerMaxSpeed;

        //horizontal collision
        hitBox.x += xspeed;
        for(Wall wall:panel.walls) {
            if(hitBox.intersects(wall.hitBox)) {
                hitBox.x -= xspeed;
                while(!wall.hitBox.intersects(hitBox)) hitBox.x += Math.signum(xspeed);
                hitBox.x -= Math.signum(xspeed);
                xspeed = 0;
                x = hitBox.x;
            }
        }

        //vertical collision
        for(Wall wall:panel.walls) {
            if(hitBox.intersects(wall.hitBox)) {
                hitBox.y -= yspeed;
                while(!wall.hitBox.intersects(hitBox)) hitBox.y += Math.signum(yspeed);
                hitBox.y -= Math.signum(yspeed);
                yspeed = 0;
                y = hitBox.y;
            }
        }

        //horizontal collision
        hitBox.x += xspeed;
        for(Enemy e:panel.enemies) {
            if(hitBox.intersects(e.hitBox)) {
                x = startX;
                y = startY;
                xspeed = 0;
                yspeed = 0;
                panel.deathCount++;
                panel.deathLabel.setText("Liczba prób: " + panel.deathCount/2);
            }
        }

        //vertical collision
        hitBox.y += xspeed;
        for(Enemy e:panel.enemies) {
            if(hitBox.intersects(e.hitBox)) {
                x = startX;
                y = startY;
                xspeed = 0;
                yspeed = 0;
                panel.deathCount++;
                panel.deathLabel.setText("Liczba prób: " + panel.deathCount/2);
            }
        }

        hitBox.x += xspeed;
        if(hitBox.intersects(panel.end)) {
            xspeed = 0;
            yspeed = 0;
            this.won = true;

        }

        hitBox.y += yspeed;
        if(hitBox.intersects(panel.end)) {
            xspeed = 0;
            yspeed = 0;

            this.won = true;

        }


        x += xspeed;
        y += yspeed;

        hitBox.x = x;
        hitBox.y = y;
    }

    public void draw(Graphics2D gtd) {
        gtd.setColor(Color.BLUE);
        gtd.fillRect(x, y, width, height);
    }

    public boolean proceedToNextLevel() {
        boolean x = this.won;
        if(x) {
            this.won = false;
            return true;
        }
        return false;
    }
}
