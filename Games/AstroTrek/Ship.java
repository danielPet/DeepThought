import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;

import javafx.scene.paint.Color;

public class Ship extends Sprite
{
 
    //private double phaserPower;
    private Phaser phaser;


    private double torpedoCharge;

    private double health;
    private double shields;

    private double velocity;
    private double angle;
    private Image[] imageArray;


    private boolean destroyed;
    private double destructionCounter;
        private Image[] explosionImageArray;

    private Integer sceneNumber = 0;

    private boolean underAttack;
    private boolean shieldsRaised;
    private int attackCounter;


    public Ship() {
        velocity = 0;
        angle = 0;
        health = 100.0;
        shields = 100.0;

        phaser = new Phaser(getX(), getY());

        destroyed = false;
        destructionCounter = 0;

        underAttack = false;
        shieldsRaised = false;
        attackCounter = 0;


        torpedoCharge = 60.0;

        imageArray = new Image[32];
        for (int i = 0; i < 32; i++)
            imageArray[i] = new Image( "images/ship/ship_" + i + ".png");

    }

    public Integer getSceneNumber() {
        return sceneNumber;
    }
    // public Integer setScene(Integer sceneCounter, Integer maxScene, double resolutionX, double resolutionY) {
    //     if ((getX() >= resolutionX) && (sceneCounter != maxScene)){
    //         sceneNumber =sceneCounter + 1;
            
    //     } else if ((getX() < resolutionX) && (sceneCounter != 0)) {
    //         sceneNumber= sceneCounter -1;
    //     } else {
    //         sceneNumber = sceneCounter;
    //     }
    //     return sceneNumber;
    // }

    public void reset() {
        velocity = 0;
        angle = 0;
        health = 100.0;
        shields = 100.0;

        //phaser = new Phaser(getX(), getY());

        destroyed = false;
        destructionCounter = 0;

        underAttack = false;
        shieldsRaised = false;
        attackCounter = 0;


        torpedoCharge = 60.0;

        setPosition(400,400);
    }

    public Phaser getPhaser() {
        return phaser;
    }

    public double getPhaserPower() {return phaser.getPower();}

    public void fireTorpedo() {

        torpedoCharge -= 10.0;
    }

    public double getTorpedoCharge() {
        return torpedoCharge;
    }


    // public void takeDamage(double damage) {
    //     health -= damage;
    // }

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

    public double getHealth() {
        return health;
    }
    public double getShields() {
        return shields;
    }



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
        
        if (velocity > 60.0) {
            velocity = 60.0; // altered
        } else if (velocity < -10.0) {
            velocity = -10.0;
        } else if (velocity+x<=60.0) {
            velocity += x;
        }
    }

    public void setAngle(double a) {
        angle = a;
    }

    public double getAngle() {
        //System.out.println( angle);
        return (angle);
    }
    public void addAngle(double a) {

        angle += a;
        if (angle < 0) {
            angle = 6.28;
        } else if (angle > 6.28) {
            angle = 0;
        }
        
    }



    public double getAngleX() {
        System.out.println("cos: " + Math.cos(angle));
        return (Math.cos(angle));
    }
    public double getAngleY() {
        System.out.println("sin: " + Math.sin(angle));
        return (Math.sin(angle));
    }

    public int angleToIndex() {
        //Double double_value = Math.floor(angle*5.0931).intValue();
        return ((Double)Math.floor(angle*5.0931)).intValue();
    }



    @Override
    public void update(double time)
    {

        //setPosition(getX()+(getVelocityX() * time), getY()-(getVelocityY() * time));

        // Boundary detection
        if ((getY()>0)&&(getY()<692)) { // use variable
            setPosition(getX()+(getVelocityX() * time), getY()-(getVelocityY() * time));
        } else if (getY()<0) {
            setPosition(getX()+(getVelocityX() * time), getY()+1);
        } else if (getY()>620) { /// not sure why not working
            setPosition(getX()+(getVelocityX() * time), getY()-5);
        }

        if ((getX()<0)&&(sceneNumber>0)) { // use variable
            sceneNumber -= 1;
            setPosition(1345, getY()-(getVelocityY() * time));
        } else if ((getX()>1351)&&(sceneNumber<=1)) { /////////////////////change for different scenes
            sceneNumber += 1;
            setPosition(0, getY()-(getVelocityY() * time));
        } else if (getX()<0) {
            setPosition(getX()+4, getY()-(getVelocityY() * time));
        } else if (getX()>1350){
            setPosition(getX()-10, getY()-(getVelocityY() * time));
        }


        setImage(imageArray[angleToIndex()]);

        //phaser.update(time);

        // Torpedo Mechanics
        if (torpedoCharge < 60.0) {
            torpedoCharge += 0.1;
        } else if (torpedoCharge >= 60.0) {
            torpedoCharge = 60.0;
        }


        // Checks if destroyed
        if ((health <= 0)&&(destructionCounter < 50)) {
            destructionCounter = destructionCounter+1;
            setPosition(getX(), getY());
            setVelocity(0,0);
            if (Math.random() < 0.5) {
                setImage("images/blast_0.png");
            } else {
                setImage("images/blast_1.png");
            }
        } else if (destructionCounter == 50) {
            setPosition(200,200);////////////////////////////////////
            destroyed = true;
            reset();
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
        if ((shieldsRaised) && (shields > 0)) {
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
    public void render(GraphicsContext gc) {
        gc.drawImage(getImage(), getX(), getY());

        if ((shields>0) &&(shieldsRaised) && (health > 0)) {
            Image shield = new Image("images/shield.png");
            gc.drawImage(shield, getX()-5, getY()-5);
        }
        gc.setFill( Color.GREEN );
    }
}