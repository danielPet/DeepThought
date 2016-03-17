import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.Iterator;


public class AstroTrek extends Application 
{

    boolean paused = false;
    Integer currentBG = 0;

    public static void main(String[] args) 
    {
        launch(args);
    }

    private boolean outOfBounds(double xPos, double yPos, Integer xRes, Integer yRes) {
        if ((xPos > 0) && (xPos<=xRes.doubleValue()) && (yPos > 0) && (yPos <= yRes.doubleValue())) {
            return false;
        } else {
            return true;
        }
    }



    @Override
    public void start(Stage theStage) 
    {
        theStage.setTitle( "Atom Trek" );

        Group root = new Group();
        Scene theScene = new Scene( root );
        theStage.setScene( theScene );

        Integer[] resolution = new Integer[2];
        resolution[0] = 1350; // Width = 1350
        resolution[1] = 692; // Length = 692

        Canvas canvas = new Canvas( resolution[0], resolution[1] );
        root.getChildren().add( canvas );

        ArrayList<String> input = new ArrayList<String>();


        // Key Event
        theScene.setOnKeyPressed(
            new EventHandler<KeyEvent>()
            {
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    if ( !input.contains(code) )
                        input.add( code );
                }
            });

        theScene.setOnKeyReleased(
            new EventHandler<KeyEvent>()
            {
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    input.remove( code );
                }
            });

        // SHIP INITIALIZED

        Ship ship = new Ship();
        ship.setAngle(1.57);
        ship.setPosition(resolution[0]/2, resolution[1]/2);
        Phaser p = ship.getPhaser();
        ArrayList<Torpedo> torpedoList = new ArrayList<Torpedo>();

        // Mouse Events

             
        theScene.setOnMousePressed(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent e) {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        p.setInitial(ship.getX(), ship.getY());
                        p.setTarget(e.getX(), e.getY());
                        p.fire();
                    }
                }
            });

        theScene.setOnMouseDragged(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent e) {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        p.setInitial(ship.getX(), ship.getY());
                        p.setTarget(e.getX(), e.getY());
                        p.fire();
                    }
                }
            });

        theScene.setOnMouseReleased(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent e) {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        p.stop();
                    }
                }
            });

        theScene.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent e) {
                    if ((e.getButton() == MouseButton.SECONDARY) && (ship.getTorpedoCharge() >= 10.0)) {
                        ship.fireTorpedo();
                        Torpedo torpedo = new Torpedo();
                        torpedo.setTargetPosition(e.getX(), e.getY());
                        torpedo.setPosition(ship.getX()+10, ship.getY()+10);
                        torpedo.setInitialPosition(ship.getX()+10, ship.getY()+10);
                        torpedoList.add(torpedo);
                    } else if (e.getButton() == MouseButton.MIDDLE) { // Mines

                    }
                }
            });




        GraphicsContext gc = canvas.getGraphicsContext2D();

        Font theFont = Font.font( "Helvetica", FontWeight.BOLD, 10 );
        gc.setFont( theFont );
        gc.setLineWidth(1);


        
        ArrayList<Destructable> asteroidList = new ArrayList<Destructable>();
        for (int i = 0; i < 15; i++)
        {
            Destructable asteroid = new Destructable();
            asteroid.setImage("images/asteroid_0.png");
            double px = resolution[0] * Math.random() + 50;
            double py = (resolution[1]-50) * Math.random() + 50;          
            asteroid.setPosition( px, py);
            asteroid.setVelocity(20,0);
            asteroidList.add( asteroid );

        }

        // All enemies
        ArrayList<EnemyShip> enemyList = new ArrayList<EnemyShip>();
        
        for (int i = 0; i < 10; i++)
        {
            EnemyShip enemy = new EnemyShip();
            double px = resolution[0]*Math.random();
            double py = resolution[1]*Math.random();          //* Math.random() + 50
            enemy.setPosition(px,py);
            enemyList.add( enemy ); // i, enemy



        }






        
        Image controlInterface = new Image( "images/controlInterface.png" );

        ArrayList<Image> backgroundList = new ArrayList<Image>();
        backgroundList.add(new Image("images/starfield.jpg"));
        backgroundList.add(new Image("images/indigo_galaxy.jpg"));
        backgroundList.add(new Image("images/purple_nebula.jpg"));
        // Image space1 = new Image( "images/starfield.jpg" );
        // backgrounds[0] = space;
        // Image space2 = new Image( "images/indigo_galaxy.jpg" );
        // backgrounds[0] = space2;
        // Image space3 = new Image( "images/purple_nebula.jpg" );
        // backgrounds[0] = space3;



        LongValue lastNanoTime = new LongValue( System.nanoTime() );
        IntValue dilithium = new IntValue(0);
        

        // Animation this step
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                // calculate time since last update.
                double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
                //System.out.println("time: " + elapsedTime);
                lastNanoTime.value = currentNanoTime;

                
                // game logic
                if (input.contains("A"))
                    ship.addAngle(0.05);
                if (input.contains("D"))
                    ship.addAngle(-0.05);
                if (input.contains("W"))
                    ship.addVelocity(1.0);
                if (input.contains("S"))
                    ship.addVelocity(-1.0);  
                // if (input.contains("ESC")) {
                //     paused = !paused; 
                //     System.out.println(paused);
                //     }
                          
                //updates begin/.////////////////////////////////////////////////////////////////////////////////////
                ship.update(elapsedTime);  
                //Phaser p = ship.getPhaser();
                p.update(elapsedTime, ship.getX(), ship.getY());




                // collision detection

                Iterator<Torpedo> torpedoIter = torpedoList.iterator();
                while (torpedoIter.hasNext()) {
                    Torpedo torpedo = torpedoIter.next();
                    torpedo.setVelocity();

                    Iterator<Destructable> asteroidIter = asteroidList.iterator();
                    while (asteroidIter.hasNext()) {
                        Destructable asteroid = asteroidIter.next();
                        if (torpedo.intersects(asteroid)) {
                            torpedoIter.remove();
                            asteroid.takeDamage(20);
                            //asteroidIter.remove();
                            //dilithium.value++;
                        }
                    }

                    Iterator<EnemyShip> enemyIter = enemyList.iterator();
                    while (enemyIter.hasNext()) {
                        EnemyShip enemy = enemyIter.next();
                        if (torpedo.intersects(enemy)) {
                            torpedoIter.remove();
                            enemy.takeDamage(30.0);
                        }
                    }

                    if (outOfBounds(torpedo.getX(), torpedo.getY(), resolution[0], resolution[1])) {
                        torpedoIter.remove();
                    }

                    torpedo.update(elapsedTime); // After to avoid updating removed torpedos
                }


                Iterator<Destructable> asteroidIter = asteroidList.iterator();
                while (asteroidIter.hasNext()) {
                    Destructable asteroid = asteroidIter.next();

                    if (asteroid.getHealth() <= 0) {
                        asteroidIter.remove();
                    }

                    if (ship.intersects(asteroid)) {
                        asteroidIter.remove();
                        ship.takeDamage(50.0);
                        dilithium.value++;
                    }

                    Iterator<EnemyShip> enemyIter = enemyList.iterator();
                    while (enemyIter.hasNext()) {
                        EnemyShip enemy = enemyIter.next();
                        if (asteroid.intersects(enemy)) {
                            asteroidIter.remove();
                            enemy.takeDamage(30.0);
                        }
                    }

                    // Phaser
                    if ((p.isFiring())&& (p.intersects(asteroid))) {
                        asteroid.takeDamage(2.0);
                        dilithium.value++;
                    }

                    if (outOfBounds(asteroid.getX(),asteroid.getY(),resolution[0], resolution[1])) {
                        asteroid.setPosition(0, asteroid.getY());
                    }

                    asteroid.update(elapsedTime);
                }


                Iterator<EnemyShip> enemyIter = enemyList.iterator();
                while ( enemyIter.hasNext() )
                {
                    EnemyShip enemy = enemyIter.next();
                    if (enemy.isDestroyed()) {
                        enemyIter.remove();
                    }


                    if ((p.isFiring())&&(p.intersects(enemy)))                    {
                        enemy.takeDamage(p.getDamage());
                    }

                    if (outOfBounds(enemy.getX(),enemy.getY(),resolution[0], resolution[1])) {
                        enemy.addAngle(3.14);
                    }

                    enemy.update(elapsedTime, ship.getX(), ship.getY());
                    
                }

                

                //updates end



                // render
                gc.clearRect(0, 0, resolution[0], resolution[1]);

                currentBG = ship.getSceneNumber();
                gc.drawImage(backgroundList.get(currentBG), 0, 0); // using bg array of images
                

 
                ship.render( gc );
                p.render(gc);

                for (Destructable asteroid : asteroidList ) {
                    asteroid.render( gc );
                }

                    
                for (EnemyShip enemy : enemyList ) {
                    enemy.render( gc );
                }

                for (Torpedo torpedo : torpedoList ) {
                    torpedo.render( gc );
                }

                    

                gc.drawImage(controlInterface, (resolution[0]/2)-366, resolution[1]-167); // CI
 
                gc.setFill(Color.WHITE);

                String dilithiumText = "DILITHUM: " + (100 * dilithium.value);
                gc.fillText( dilithiumText, 793, 670 );
                //gc.strokeText( dilithiumText, 360, 36 );


                String velText = "VELOCITY: " + (ship.getVelocity());
                gc.fillText( velText, 456, 610 );


                String phaserText = "PHASERS                                                          " + (ship.getPhaserPower());
                gc.fillText( phaserText, 745, 609 );

                Double numTorpedos = (Double)(ship.getTorpedoCharge());
                String torpedoText = "TORPEDO                                                           " + (numTorpedos.intValue()/10);
                gc.fillText( torpedoText, 744, 624 );

                String hText = "H";
                gc.fillText( hText, 332, 572 );

                String sText = "S";
                gc.fillText( sText, 357, 572 );
                // Health bar
                gc.setFill( Color.GRAY );
                gc.fillRoundRect(327,574, 20, 100, 10,10);
                gc.setFill( Color.rgb(18,195,0) );
                gc.fillRoundRect(327,674-ship.getHealth(), 20, ship.getHealth(), 10,10);




                gc.setFill( Color.GRAY );
                gc.fillRoundRect(352,574, 20, 100, 10,10);
                gc.setFill( Color.rgb(0,174,255) );
                gc.fillRoundRect(352,674-ship.getShields(), 20, ship.getShields(), 10,10);

                //gc.strokeText( velText, 360, 100 );
                // Velocity bar
                gc.setFill( Color.CYAN );
                for (int i = 0; i < 6; i += 1) {
                    if (ship.getVelocity() > (12.0*i)) {
                        gc.fillRoundRect(456 + 27*i,616, 27, 11, 10,10);
                    }
                    
                }



                // Phaser power
                gc.setFill( Color.ORANGE );
                gc.fillRoundRect(793,600, 1.5*ship.getPhaserPower(),11, 10,10);

                // Torpedos
                gc.setFill( Color.RED );
                for (int i = 0; i < 6; i += 1) {
                    if (ship.getTorpedoCharge() >= (10.0*i + 10.0)) {
                        gc.fillRoundRect(793 + 25*i,615, 25, 11, 10,10);
                    }
                    
                }
                //}

                
                
                    
                
            }
        }.start();

        theStage.show();
    }
}