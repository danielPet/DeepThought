import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

public class Destructable extends Sprite
{
    private double health;


    public Destructable() {
        health = 100.0;
    }

        public void takeDamage(double d) {
            health -= d;
        }
    public double getHealth() {
        return health;
    }

    @Override
    public void update(double time) {
        setPosition(getX()+(getVelocityX() * time), getY()-(getVelocityY() * time));
    }

        @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(getImage(), getX(), getY());
        if (health < 100.0){
            gc.setFill( Color.rgb(18,195,0) );
            gc.fillRect(getX(),getY()-3, getHealth()/5,2); // divide by 5
        }
        

    }
}