package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Game extends Application implements Serializable
{
    public static Label scorelabel;
    public static ArrayList<Integer> store_obstacles,store_events;
    public int counter,score = 0;
    public static Stage primaryStage;
    public static Player ball;
    public static boolean chance;
    public static Rectangle rectangle;
    public static GameState gameState;
    private final int gridwidth = 500, gridheight = 800;
    public static int ticks,ymotion,firstcount = 0;
    public static Group root;
    public static LinearGradient linearGradient;
    public static IntegerStringConverter str;
    public static Timeline timer1,timer2;
    public static Scene scene;
    public static int X,Y;
    public static boolean update = true;
    public static GameState state;
    public static transient ObstacleHandler obstacleHandler;
    public static DropShadow dropShadow;
    static double orgSceneX,orgSceneY,orgTranslateX,orgTranslateY;
    public static Sound sound,jump_sound;
    public static int rootposition;
    public static int distance;
    Bounds boundsInScene;
    public void setScore(int score)
    {
        this.score = score;
    }
    public int getScore()
    {
        return this.score;
    }
    void Jump()
    {
        if(ymotion>0)
        {
            ymotion=0;
        }
        ymotion=ymotion-10;
    }
    @Override
    public void start(Stage window) throws IOException, URISyntaxException, LineUnavailableException, UnsupportedAudioFileException {
        primaryStage = window;
        chance = true;
        rootposition = 0;
        distance = 0;
        primaryStage.setTitle("Color Switch");
        FileInputStream icon = new FileInputStream(new File("").getAbsolutePath().toString() + "/src/sample/images/color_switch.png");
        Image iconimg = new Image(icon, (double)500, 125, true, true);
        primaryStage.getIcons().add(iconimg);
        primaryStage.setHeight(gridheight);
        primaryStage.setWidth(gridwidth);
        primaryStage.setResizable(true);
        obstacleHandler = new ObstacleHandler();
        gameState = new GameState();
        root = new Group();
        int y_axis = 0;
        rectangle = new Rectangle();
        rectangle.setX(0);
        rectangle.setY(0);
        rectangle.setHeight(800);
        rectangle.setWidth(800);
        y_axis -= 800;
        try {
            rectangle.setFill(new ImagePattern(new Image(new FileInputStream(new File("").getAbsolutePath().toString() + "/src/sample/images/gameplay.gif"))));
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        root.getChildren().add(rectangle);


        Stop[] stops = new Stop[] { new Stop(0, Color.MAROON.darker().darker().darker().darker().darker()), new Stop(1, Color.MIDNIGHTBLUE.darker().darker().darker().darker())};
        LinearGradient linear = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        root.minHeight(10000);
        state = new GameState();

        DropShadow ds1 = new DropShadow();
        ds1.setOffsetY(4.0f);
        ds1.setOffsetX(4.0f);
        ds1.setColor(Color.GREY);
        scorelabel = new Label("0");
        scorelabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 50));
        scorelabel.setTextFill(Color.WHITE);
        scorelabel.setLayoutX(10);
        scorelabel.setLayoutY(20);
        root.getChildren().addAll(scorelabel);

        DropShadow ds2 = new DropShadow();
        ds1.setOffsetY(4.0f);
        ds1.setOffsetX(4.0f);
        ds1.setColor(Color.BLACK);
        Colour colour = new Colour();
        ball = new Player(colour,500,800);
        ball.setEffect(ds1);

        FileInputStream hand = new FileInputStream(new File("").getAbsolutePath().toString() + "/src/sample/images/hand1.png");
        Image handimg = new Image(hand, (double)500, 125, true, true);
        ImageView imageView = new ImageView(handimg);
        imageView.setX(218);
        imageView.setY(618);
        root.getChildren().add(imageView);
        FileInputStream text = new FileInputStream(new File("").getAbsolutePath().toString() + "/src/sample/images/text.gif");
        Image textimg = new Image(text, (double)500, 125, true, true);
        ImageView textView = new ImageView(textimg);
        textView.setX(20);
        textView.setY(350);
        root.getChildren().add(textView);

        ymotion = 0;
        str = new IntegerStringConverter();


        linearGradient = new LinearGradient(0.0, 0.0, 1.0, 0.0, true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.GREY),
                new Stop(1.0, Color.BLACK));
        root.getChildren().add(ball);
        timer1 = new Timeline();
        timer1.setCycleCount(Animation.INDEFINITE);
        timer2 = new Timeline();
        timer2.setCycleCount(Animation.INDEFINITE);
        StartMenu startMenu = new StartMenu();

        action_play_classic_mode();
        action_play_death_mode();
        scene = new Scene(root);
        startMenu.driver();
        scene.setFill(linear);

        primaryStage.setTitle("COLOR SWITCH");
        if(update)
        {
            startMenu.introSetter();
        }
        primaryStage.setHeight(gridheight);
        primaryStage.setWidth(gridwidth);
        primaryStage.show();
    }
    public void action_play_classic_mode()
    {
        KeyFrame kf = new KeyFrame(Duration.millis(20), e ->
        {
            obstacleHandler.rotator();
            if(firstcount!=0)
            {
                ticks++;
                if(ticks%2==0&&ymotion<15)
                {
                    ymotion=ymotion+2;
                }
                X=X-2;
                int y=(int)ball.player.getCenterY()+ymotion;
                distance+=ymotion;
                ball.player.setCenterY(y);
            }
            scene.setOnMouseClicked(k ->
            {
                if(timer1.getStatus() != Animation.Status.PAUSED)
                {
                    Jump();
                    sound.jumpingSound();
                    firstcount++;
                }
                else
                {
                    ymotion = 2;
                }
            });
            for (int i=0; i<obstacleHandler.obstacles.size(); i++)
            {
                boolean checker = obstacleHandler.obstacles.get(i).collision(ball);
                if(obstacleHandler.obstacles.get(i).getColourPalette() && checker)
                {
                    try {
                        store_events.remove((Integer) i);
                    }
                    catch (Exception exp){}
                    root.getChildren().remove(obstacleHandler.obstacles.get(i));
                }
                else if(obstacleHandler.obstacles.get(i).getStar() && checker)
                {
                    String scr = scorelabel.getText();
                    int points = Integer.parseInt(scr);
                    points++;
                    scorelabel.setText(points + "");
                    try {
                        store_events.remove(store_events.indexOf(i));
                    }
                    catch (Exception exp){
                        exp.printStackTrace();
                    }
                    root.getChildren().remove(obstacleHandler.obstacles.get(i));
                }
                else if(checker)
                {
                    EventHandler<MouseEvent> eventHandler1 = new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent e)
                        {
                            firstcount = 0;
                            ymotion = 0;
                            root.getChildren().remove(root.getChildren().size()-1);
                            root.getChildren().remove(root.getChildren().size()-1);
                            for(int i=0; i<root.getChildren().size(); i++)
                            {
                                root.getChildren().get(i).setEffect(null);
                            }
                            timer1.play();
                        }
                    };
                    EventHandler<MouseEvent> eventHandler2 = new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent e)
                        {
                            root.getChildren().clear();
                            timer1.stop();
                            sound.sound_stop(1);
                            sound.gameOverSound();
                            GameOver gameOver = new GameOver();
                            try {
                                gameOver.endGame(true);
                            }
                            catch (IOException exp) {
                                exp.printStackTrace();
                            }
                        }
                    };
                    if(Integer.parseInt(scorelabel.getText()) > 3 && chance)
                    {
                        GaussianBlur blur2 = new GaussianBlur();
                        timer1.pause();
                        chance = false;
                        blur2.setRadius(50);
                        for(int j=0; j<root.getChildren().size(); j++)
                        {
                            root.getChildren().get(j).setEffect(blur2);
                        }
                        FileInputStream revive = null;
                        FileInputStream kill = null;
                        try {
                            revive = new FileInputStream(new File("").getAbsolutePath().toString() + "/src/sample/images/revive.png");
                            kill = new FileInputStream(new File("").getAbsolutePath().toString() + "/src/sample/images/kill.png");
                        } catch (Exception exp) {}
                        Image pimg = new Image(revive,150,110,true,true);
                        ImageView imageView1 = new ImageView(pimg);
                        Button pbutton = new Button("",imageView1);
                        pbutton.setStyle("-fx-background-color: transparent;");
                        pbutton.setShape(new Circle(0.1,Color.BLACK));
                        pbutton.setLayoutY(scorelabel.getBoundsInParent().getMaxY() + 150);
                        pbutton.setLayoutX(70);

                        Image simg = new Image(kill,150,110,true,true);
                        ImageView imageView2 = new ImageView(simg);
                        Button sbutton = new Button("",imageView2);
                        sbutton.setStyle("-fx-background-color: transparent;");
                        sbutton.setShape(new Circle(0.1,Color.BLACK));
                        sbutton.setLayoutY(scorelabel.getBoundsInParent().getMaxY() + 150);
                        sbutton.setLayoutX(300);

                        ymotion = 2;
                        firstcount = 0;
                        pbutton.addEventHandler(MouseEvent.MOUSE_CLICKED,eventHandler1);
                        sbutton.addEventHandler(MouseEvent.MOUSE_CLICKED,eventHandler2);
                        root.getChildren().add(sbutton);
                        root.getChildren().add(pbutton);
                        try {
                            store_events.remove((Integer) i);
                        }
                        catch (Exception exp){}
                        obstacleHandler.obstacles.get(i).darken();
                    }
                    else
                    {
                        root.getChildren().clear();
                        timer1.stop();
                        sound.sound_stop(1);
                        sound.gameOverSound();
                        GameOver gameOver = new GameOver();
                        try
                        {
                            gameOver.endGame(true);
                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }
        });
        KeyFrame kf2 = new KeyFrame(Duration.millis(20),e->
        {
            boundsInScene = ball.localToScene(ball.getBoundsInLocal());
            distance = (int)ball.player.getCenterY();
            if(boundsInScene.getMaxY() <= 400 && counter == 0)
            {
                counter++;
            }
            if(boundsInScene.getMinY() <= 300 && counter > 0)
            {
                root.setTranslateY(root.getTranslateY()+4);
                rectangle.setTranslateY(rectangle.getTranslateY()-4);
                rootposition++;
                scorelabel.setTranslateY(scorelabel.getTranslateY()-4);
                counter++;
            }
        });

        timer1.getKeyFrames().addAll(kf,kf2);
    }
    public void action_play_death_mode()
    {
        KeyFrame kf = new KeyFrame(Duration.millis(20),e ->
        {
            obstacleHandler.rotator();
            scene.setOnMousePressed(k->
            {
                try
                {
                    orgSceneX = k.getSceneX();
                    orgTranslateX = ((Circle)(k.getSource())).getTranslateX();
                }
                catch (Exception exp){}
            });
            scene.setOnMouseDragged(k->
            {
                double offsetX = k.getSceneX() - orgSceneX;
                double newTranslateX = 0;
                newTranslateX = orgTranslateX + offsetX;
                if(boundsInScene.getMaxX() < 490 && boundsInScene.getMaxX() > 50 && newTranslateX < 360)
                {
                    ball.setTranslateX(newTranslateX);
                }
                else if(boundsInScene.getMinX() >= 200 && newTranslateX <= 180)
                {
                    ball.setTranslateX(newTranslateX);
                }
                else if(boundsInScene.getMinX() <= 50 && newTranslateX >= -250 && newTranslateX < 200)
                {
                    ball.setTranslateX(newTranslateX);
                }
            });
            for (int i=0; i<obstacleHandler.obstacles.size(); i++)
            {
                boolean checker = obstacleHandler.obstacles.get(i).collision(ball);
                if(obstacleHandler.obstacles.get(i).getColourPalette() && checker)
                {
                    //root.getChildren().remove(obstacleHandler.obstacles.get(i));
                }
                else if(obstacleHandler.obstacles.get(i).getStar() && checker)
                {
                    String scr = scorelabel.getText();
                    int points = Integer.parseInt(scr);
                    points++;
                    scorelabel.setText(points + "");
                    try {
                        store_events.remove(store_events.indexOf(i));
                    }
                    catch (Exception exp){}
                    root.getChildren().remove(obstacleHandler.obstacles.get(i));
                    obstacleHandler.obstacles.remove(i);
                }
                else if(checker)
                {
                    EventHandler<MouseEvent> eventHandler1 = new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent e)
                        {
                            firstcount = 0;
                            timer2.play();
                            for(int i=0; i<root.getChildren().size(); i++)
                            {
                                root.getChildren().get(i).setEffect(null);
                            }
                            root.getChildren().remove(root.getChildren().size()-1);
                            root.getChildren().remove(root.getChildren().size()-1);
                        }
                    };
                    EventHandler<MouseEvent> eventHandler2 = new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent e)
                        {
                            root.getChildren().clear();
                            timer2.stop();
                            sound.sound_stop(1);
                            sound.gameOverSound();
                            GameOver gameOver = new GameOver();
                            try {
                                gameOver.endGame(true);
                            }
                            catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    };
                    if(Integer.parseInt(scorelabel.getText()) > 3 && chance)
                    {
                        GaussianBlur blur2 = new GaussianBlur();
                        timer2.pause();
                        chance = false;
                        blur2.setRadius(50);
                        for(int j=0; j<root.getChildren().size(); j++)
                        {
                            root.getChildren().get(j).setEffect(blur2);
                        }
                        FileInputStream revive = null;
                        FileInputStream kill = null;
                        try {
                            revive = new FileInputStream(new File("").getAbsolutePath().toString() + "/src/sample/images/revive.png");
                            kill = new FileInputStream(new File("").getAbsolutePath().toString() + "/src/sample/images/kill.png");
                        } catch (Exception exp) {}
                        Image pimg = new Image(revive,150,110,true,true);
                        ImageView imageView1 = new ImageView(pimg);
                        Button pbutton = new Button("",imageView1);
                        pbutton.setStyle("-fx-background-color: transparent;");
                        pbutton.setShape(new Circle(0.1,Color.BLACK));
                        pbutton.setLayoutY(scorelabel.getBoundsInParent().getMaxY() + 150);
                        pbutton.setLayoutX(70);
                        root.getChildren().add(pbutton);

                        Image simg = new Image(kill,150,110,true,true);
                        ImageView imageView2 = new ImageView(simg);
                        Button sbutton = new Button("",imageView2);
                        sbutton.setStyle("-fx-background-color: transparent;");
                        sbutton.setShape(new Circle(0.1,Color.BLACK));
                        sbutton.setLayoutY(scorelabel.getBoundsInParent().getMaxY() + 150);
                        sbutton.setLayoutX(300);
                        root.getChildren().add(sbutton);
                        ymotion = 2;
                        firstcount = 0;
                        pbutton.addEventHandler(MouseEvent.MOUSE_CLICKED,eventHandler1);
                        sbutton.addEventHandler(MouseEvent.MOUSE_CLICKED,eventHandler2);
                        // FOR RED ONLY
                        try {
                            store_events.remove((Integer) i);
                        }
                        catch (Exception exp){}
                        obstacleHandler.obstacles.get(i).darken();
                        obstacleHandler.obstacles.remove(i);
                    }
                    else
                    {
                        root.getChildren().clear();
                        timer2.stop();
                        sound.sound_stop(1);
                        sound.gameOverSound();
                        GameOver gameOver = new GameOver();
                        try {
                            gameOver.endGame(true);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                }
            }

        });

        KeyFrame kf2 = new KeyFrame(Duration.millis(20),e->
        {

            boundsInScene = ball.localToScene(ball.getBoundsInLocal());
            if(boundsInScene.getMaxY() <= 400 && counter == 0)
            {
                counter++;
            }
            ball.setTranslateY(ball.getTranslateY()-4);
            rectangle.setTranslateY(rectangle.getTranslateY()-4);
            root.setTranslateY(root.getTranslateY()+4);
            scorelabel.setTranslateY(scorelabel.getTranslateY()-4);
            counter++;
        });

        timer2.getKeyFrames().addAll(kf,kf2);
    }
}
