import java.awt.*;

public class Enemy {
    GamePanel panel;
    int x;
    int y;
    int width;
    int height;

    double xspeed;
    double yspeed;
    int counter;
    int direction;
    boolean moving;
    int type;

    Rectangle hitBox;


    public Enemy(int x, int y, GamePanel panel, int type) {
        this.panel = panel;
        this.x = x;
        this.y = y;

        this.width = Settings.tileSize;
        this.height = Settings.tileSize;

        this.counter = 0;
        this.moving = false;

        this.xspeed = 0;
        this.yspeed = 0;

        this.type = type;

        this.hitBox = new Rectangle(x, y, this.width, this.height);
    }

    public void typeMovement() {
        switch (this.type) {
            case 0:
                if(counter == 0) moving = false;

                if(!moving) {
                    counter = Settings.rand(1,10);
                    direction = Settings.rand(0, 3);
                    moving = true;
                } else {
                    switch (direction) {
                        case 0:
                            xspeed++;
                            break;
                        case 1:
                            xspeed--;
                            break;
                        case 2:
                            yspeed--;
                            break;
                        case 3:
                            yspeed++;
                            break;
                    }

                }
                counter--;
                break;
                //horizontal
            case 1:
                if(this.direction == 1)
                    xspeed++;
                else
                    xspeed--;
                hitBox.x += xspeed;
                for(Wall wall:panel.walls) {
                    if(hitBox.intersects(wall.hitBox)) {
                        direction++;
                        direction %= 2;
                    }
                }
                break;
                //vertical
            case 2:
                if(this.direction == 3)
                    yspeed++;
                else
                    yspeed--;
                hitBox.y += yspeed;
                for(Wall wall:panel.walls) {
                    if(hitBox.intersects(wall.hitBox)) {
                        direction = (direction == 3) ? 4 : 3;
                    }
                }
                break;

        }

    }

    public void set() {
        typeMovement();

        if(xspeed > 0 && xspeed < 0.75) xspeed = 0;
        if(xspeed < 0 && xspeed > -0.75) xspeed = 0;

        if(xspeed > Settings.enemyMaxSpeed) xspeed = Settings.enemyMaxSpeed;
        if(xspeed < -Settings.enemyMaxSpeed) xspeed = -Settings.enemyMaxSpeed;

        if(yspeed > 0 && yspeed < 0.75) yspeed = 0;
        if(yspeed < 0 && yspeed > -0.75) yspeed = 0;

        if(yspeed > Settings.enemyMaxSpeed) yspeed = Settings.enemyMaxSpeed;
        if(yspeed < -Settings.enemyMaxSpeed) yspeed = -Settings.enemyMaxSpeed;

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

        x += xspeed;
        y += yspeed;

        hitBox.x = x;
        hitBox.y = y;
    }

    public void draw(Graphics2D gtd) {
        gtd.setColor(Color.RED);
        gtd.fillRect(x, y, width, height);
    }
}
