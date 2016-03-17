import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;

public class Torpedo extends Sprite
{
    private double initialPositionX;
    private double initialPositionY;    
    private double targetPositionX;
    private double targetPositionY;

    private double velocityX;
    private double velocityY;
    private double positionX;
    private double positionY;

    private double norm;


    public Torpedo() {
        this.setImage("images/torpedo_0.png");
    }

    public void animateTorpedo() {
        if (Math.random() < 0.5) {
            setImage("images/torpedo_0.png");
        } else {
            setImage("images/torpedo_1.png");
        }
    }

    public void setInitialPosition(double x, double y)
    {
        initialPositionX = x;
        initialPositionY = y;
    }

    public void setTargetPosition(double x, double y)
    {
        targetPositionX = x;
        targetPositionY = y;
    }

    public double getInitialX() {
        return initialPositionX;
    }
    public double getInitialY() {
        return initialPositionY;
    }

    public double getTargetX() {
        return targetPositionX;
    }
    public double getTargetY() {
        return targetPositionY;
    }


    

    //@Override
    public void setVelocity()
    {
        double x = 120*((getTargetX() - getInitialX())/getNorm());
        double y = 120*((getTargetY() - getInitialY())/getNorm());
        this.setVelocity(x, y);
        this.animateTorpedo();
    }

    public double getNorm() {
        double y = getInitialY() - getTargetY();
        double x = getInitialX() - getTargetX();
        double n = Math.sqrt(y*y+x*x);
        return n;
    }
}