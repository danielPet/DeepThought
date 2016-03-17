import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;




public class EnemyShip extends Sprite
{
    private double health;
    private double shields;
    private Image[] imageArray;
    private Image[] explosionImageArray;
    private double velocity;
    private double angle;

    private boolean destroyed;
    private double destructionCounter;

    private boolean underAttack;
    private boolean shieldsRaised;
    private int attackCounter;

    private double prevDistance;
    private double angleIncrement;

    EnemyShip() {
        //System.out.println("creating enemy");
        health = 100.0;
        shields = 100.0;
        if ((Math.random() > 0.5)) {
            velocity = 40.0;
        } else {
            velocity = 20.0;
        }
        angle = Math.random() * 6.28;
        destroyed = false;
        destructionCounter = 0;

        underAttack = false;
        shieldsRaised = false;
        attackCounter = 0;

        //angle = 0;
        angleIncrement = 0.05;
        prevDistance = 1000.0;

        //imageArray = new Image[1];
        imageArray = new Image[32];
        for (int i = 0; i < 32; i++)
            imageArray[i] = new Image( "images/warbird/warbird_" + i + ".png");

    }


    public double getHealth() {
        return health;
    }

    public double getShields() {
        return shields;
    }

    public void takeDamage(double d) {
        underAttack = true;
        attackCounter = 0;
        if (shields >= d) {
            shields -= d;
        } else {
            shields = 0;
            health -= (d-shields);
        }
        
    }

    public boolean isDestroyed() {return destroyed;}
    
    public void setVelocity(double x) {
        velocity = x;
    }

    public double getVelocity() {
        return velocity;
    }

    @Override
    public double getVelocityX() {
        return (velocity*Math.cos(angle));
    }
    @Override
    public double getVelocityY() {
        return (velocity*Math.sin(angle));
    }

    public void addVelocity(double x) {
        velocity += x;
        if (velocity > 50.0) {
            velocity = 50.0;
        } else if (velocity < -10.0) {
            velocity = -10.0;
        }
    }

    public void addAngle(double a) {

        //angle += a;
        if ((angle+a) < 0) {
            angle += a + 6.28;
        } else if ((angle+a) > 6.28) {
            angle += a - 6.28;
        } else {
            angle += a;
        }
        //System.out.println(angle);
    }

    public void setAngle(double a) {
        angle = a;
    }

    public double getAngle() {
        return (angle);
    }

    public double getAngleX() {
        //System.out.println("cos: " + Math.cos(angle));
        return (Math.cos(angle));
    }
    public double getAngleY() {
        //System.out.println("sin: " + Math.sin(angle));
        return (Math.sin(angle));
    }

    public int angleToIndex() {
        //Double double_value = Math.floor(angle*5.0931).intValue();
        return ((Double)Math.floor(angle*5.0931)).intValue();
    }

    public double distance(double x1, double y1, double x2, double y2) {
        return (Math.sqrt(((x1-x2)*(x1-x2))+((y1-y2)*(y1-y2))));
    }

    public void update(double time, double sx, double sy) {

        // Checks if destroyed
        if ((health <= 0)&&(destructionCounter < 50)) {
            destructionCounter = destructionCounter+1;
            setPosition(getX(), getY());
            if (Math.random() < 0.5) {
                setImage("images/blast_0.png");
            } else {
                setImage("images/blast_1.png");
            }
        } else if (destructionCounter == 50) {
            destroyed = true;
        } else {
            setImage(imageArray[angleToIndex()]);
            setPosition(getX()+(getVelocityX() * time), getY()-(getVelocityY() * time));
        }


        // Checks if taking damage
        if ((underAttack) && (attackCounter<100)) {
            attackCounter += 1;
            shieldsRaised = true;
        } else if (attackCounter>=100) {
            attackCounter = 0;
            underAttack = false;
            shieldsRaised = false;
        }



        // Checks if shields are raised
        if ((shieldsRaised) && (shields > 0)) { /// NEED TO DEACTIVATE SHIELDS
            if (shields < 5.0) {
                shieldsRaised = false;
            } else {
                shields -= 0.1;
            }
        }  else if ((shields < 100.0) && (!underAttack)) {
            shields += 0.5;
        }
        
    }

    @Override
    public Rectangle2D getBoundary() {return new Rectangle2D(getX(),getY(),20,20);}
    @Override
    public boolean intersects(Sprite s) {return s.getBoundary().intersects(this.getBoundary());}
    
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(getImage(), getX(), getY());
        if ((shields>0) &&(shieldsRaised) && (health > 0)) {
            Image shield = new Image("images/shield.png");
            gc.drawImage(shield, getX()-5, getY()-5);
        }

        // Health and shields
        gc.setFill( Color.rgb(18,195,0) );
        gc.fillRect(getX(),getY()-3, getHealth()/5,2); // divide by 5
        gc.setFill( Color.rgb(0,174,255) );
        gc.fillRect(getX(),getY()-6, getShields()/5,2);
    }

}