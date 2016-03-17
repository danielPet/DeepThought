import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;

import javafx.scene.paint.Color;

public class Phaser extends Sprite
{
    private double initialX;
    private double initialY;  

    private double targetX;
    private double targetY; 
    private double terminalX;
    private double terminalY;

    private double angle;
    private boolean firing;  
    private double power;
    private double damage;

    

    public Phaser(double initX, double initY) {
        setInitial(initX, initY);
        damage = 1.0;
    }


    public void setInitial(double x, double y)
    {
        
            initialX = x+10;
            initialY = y+10;
        
    }

    public double getDamage() {
        return damage;
    }


    public double getAngle(double tX, double tY) {
        double x_diff = initialX+10 - tX;
        double y_diff = initialY+10 - tY;

        double a = Math.atan(y_diff/x_diff);
        if (x_diff > 0) {
            a += 3.14;
        }
        System.out.println(a);
        return a;
    }
    public void setTerminalPoint()
    {
        // targetX = x;
        //targetY = ;
        if (getNorm(targetX,targetY) < 100) {
            terminalX = targetX;
            terminalY = targetY;
        } else {
            terminalX = (initialX + 100 * Math.cos(getAngle(targetX,targetY)));
            terminalY = (initialY + 100 * Math.sin(getAngle(targetX,targetY)));

        }
    }
    public void setTarget(double x, double y) {
        targetX = x;
        targetY = y;
    }

    // public void setFixedTarget(double x, double y) {
    //     fixedTargetX = x;
    //     fixedTargetY = y;
    //     setTarget(x,y);
    // }

    public double getNorm(double tX, double tY) {
        double x = initialX+10.0 - tX; // Mid-ship to target
        double y = initialY+10.0 - tY;
        double n = Math.sqrt(y*y+x*x);
        return n;
    }


    public double getPhaserInitialX() {return initialX;}
    public double getPhaserInitialY() {return initialY;    }
    public void fire() {firing = true;}
    public void stop() {firing = false;}
    //public void fix() {fixed = true;}
    //public void unfix() {fixed = false;}
    public boolean isFiring() {return firing;}
    public double getPower() {return power;}

    public void update(double time, double x, double y) {
        // Phaser Mechanics
        setInitial(x,y);
        setTerminalPoint();

        if (firing) {
            
            if (power < 5.0) {
                stop();
            } else if (power > 0) {
                power -= 0.25;
            }
            
        } else {
            if (power < 100.0) {
                power += 0.5;
            }
        }
    }
    @Override
    public Rectangle2D getBoundary() {return new Rectangle2D(terminalX,terminalY,2,2);}
    @Override
    public boolean intersects(Sprite s) {return ((firing)&&s.getBoundary().intersects(this.getBoundary()));}
    
    @Override
    public void render(GraphicsContext gc) {
        //gc.drawImage(image, positionX, positionY);
        if (firing) {
            gc.setStroke(Color.ORANGE);
            gc.strokeLine(initialX, initialY, terminalX, terminalY);
        }
    }

}