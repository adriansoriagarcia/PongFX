package es.adriansoriagarcia.pongfx;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
//import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;


/**
 * JavaFX App
 */
public class App extends Application {
    
    int ballCenterX = 10;
    int ballCurrentSpeedX = 3;
    int ballCenterY = 30;
    int ballCurrentSpeedY = 3;
    final int SCENE_TAM_X = 600;
    final int SCENE_TAM_Y = 400;
    final int STICK_WIDTH = 7;
    final int STICK_HEIGHT = 50;
    int stickPosY = (SCENE_TAM_Y - STICK_HEIGHT)/2;
    int stickCurrentSpeed = 0;
    final int TEXT_SIZE = 24;
    //Puntuación actual
    int score;
    //Puntuación máxima
    int highScore;
    Text textScore;
    Pane root = new Pane ();
    Circle circleBall;
    Rectangle rectStick;
   
    private void calculateBallSpeed(int collisionZone) {
        switch(collisionZone) {
        case 0:
            //No hay colisión
            break;
        case 1:
            //Hay colisión esquina superior
            ballCurrentSpeedX = -3;
            ballCurrentSpeedY = -6;
            break;    
        case 2:
            //Hay colisión lado superior
            ballCurrentSpeedX = -3;
            ballCurrentSpeedY = -3;
            break;     
        case 3:
            //Hay colisión lado inferior
            ballCurrentSpeedX = -3;
            ballCurrentSpeedY = 3;
            break; 
        case 4:
            //Hay colisión esquina inferior
            ballCurrentSpeedX = -3;
            ballCurrentSpeedY = 6;
            break;     
        }
    }
    
    private void resetGame() {
            //REINICIAR LA PARTIDA
            score = 0;
            textScore.setText(String.valueOf(score));
            ballCenterX = 10;
            ballCurrentSpeedY = 3;
            //Posición inicial aleatorio para la bola en el eje Y
            Random random = new Random();
            ballCenterY = random.nextInt(SCENE_TAM_Y);
    }
    
    private void drawNet(int portionHeight, int portionWidth, int portionSpacing){
        //Dibujo de la red
        for(int i=0; i<SCENE_TAM_Y; i+=portionSpacing) {
            Line line = new Line(SCENE_TAM_X/2, i, SCENE_TAM_X/2, i+portionHeight);
            line.setStroke(Color.WHITE);
            line.setStrokeWidth(portionWidth);
            root.getChildren().add(line);
        }
    }
    
    private int getStickCollisionZone(Circle ball, Rectangle stick){
        if(Shape.intersect(ball, stick).getBoundsInLocal().isEmpty()) {
            return 0;
        } else {
            double offsetBallStick = ball.getCenterY()- stick.getY();
            if(offsetBallStick < stick.getHeight() * 0.1) {
                return 1;
            } else if(offsetBallStick < stick.getHeight() / 2) {
                return 2;
            } else if(offsetBallStick >= stick.getHeight() / 2 && offsetBallStick < stick.getHeight() * 0.9) {
                return 3;
            } else {
                return 4;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        
        textScore = new Text();
        drawNet(10,4,30);
        
        
        //Pane root = new Pane ();
        Scene scene = new Scene(root, SCENE_TAM_X, SCENE_TAM_Y, Color.BLACK);
        primaryStage.setTitle("PongFX");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //LAYOUTS PARA MOSTRAR PUNTUACIONES
        //Layout principal
        HBox paneScores = new HBox();
        paneScores.setTranslateY(20);
        paneScores.setMinWidth(SCENE_TAM_X);
        paneScores.setAlignment(Pos.CENTER);
        paneScores.setSpacing(100);
        root.getChildren().add(paneScores);
        //Layout para puntuación actual
        HBox paneCurrentScore = new HBox();
        paneCurrentScore.setSpacing(10);
        paneScores.getChildren().add(paneCurrentScore);
        //Layout para puntuación máxima
        HBox paneHighScore = new HBox();
        paneHighScore.setSpacing(10);
        paneScores.getChildren().add(paneHighScore);
        //Texto de etiqueta para la puntuación
        Text texTitleScore = new Text("0");
        texTitleScore.setFont(Font.font(TEXT_SIZE));
        texTitleScore.setFill(Color.WHITE);
        //Texto para la puntuación
        //Text textScore = new Text("0");
        textScore.setFont(Font.font(TEXT_SIZE));
        textScore.setFill(Color.WHITE);
        //Texto de etiqueta para la puntuación máxima
        Text textTitleHighScore = new Text("Max.Score");
        textTitleHighScore.setFont(Font.font(TEXT_SIZE));
        textTitleHighScore.setFill(Color.WHITE);
        //Texto para la puntuación máxima
        Text textHighScore = new Text("0");
        textHighScore.setFont(Font.font(TEXT_SIZE));
        textHighScore.setFill(Color.WHITE);
        //Añadir los textos a los layouts reservados para ellos
        paneCurrentScore.getChildren().add(texTitleScore);
        paneCurrentScore.getChildren().add(textScore);
        paneHighScore.getChildren().add(textTitleHighScore);
        paneHighScore.getChildren().add(textHighScore);
        
        //Creación de la bola
        circleBall = new Circle(ballCenterX,ballCenterY,7,Color.WHITE);
        root.getChildren().add(circleBall);
        
        //Creación de la pala
        rectStick = new Rectangle(SCENE_TAM_X*0.9,stickPosY,STICK_WIDTH,STICK_HEIGHT);
        rectStick.setFill(Color.WHITE);
        root.getChildren().add(rectStick);
        
        Timeline animationBall = new Timeline(    
            new KeyFrame(Duration.seconds(0.017),(ActionEvent ae) -> {
                circleBall.setCenterX(ballCenterX);
                ballCenterX+=ballCurrentSpeedX;
                //Comprobar si la bola ha tocado el lado derecho
                if(ballCenterX >= SCENE_TAM_X) {
                    //Comprobar si existe una puntuación más alta
                    if (score > highScore) {
                        //Cambiar nueva puntuación más alta
                        highScore = score;
                        textHighScore.setText(String.valueOf(highScore));
                    }
                    this.resetGame();
                }
                if(ballCenterX <= 0) {
                    ballCurrentSpeedX = 3;
                }
                circleBall.setCenterY(ballCenterY);
                ballCenterY+=ballCurrentSpeedY;
                if(ballCenterY >= 400) {
                    ballCurrentSpeedY = -3;
                }
                if(ballCenterY <= 0) {
                    ballCurrentSpeedY = 3;
                }
                //Actualizar posicion de pala
                stickPosY += stickCurrentSpeed;
                if(stickPosY < 0) {
                    //No sobrepasar el borde superior de la ventana
                    stickPosY=0;
                } else {
                    //No sobrepasar el borde inferior de la ventana
                    if(stickPosY > SCENE_TAM_Y - STICK_HEIGHT){
                        stickPosY = SCENE_TAM_Y - STICK_HEIGHT;
                    }
                }
                //Mover rectangulo de la pala a posicion actual
                rectStick.setY(stickPosY);
                
                //Detectar zona de colisión de bola con la pala 
                int collisionZone = getStickCollisionZone(circleBall, rectStick);
                
                //Cambiar velocidad de la bola
                calculateBallSpeed(collisionZone);
                
                Shape shapeColision = Shape.intersect(circleBall, rectStick);
                boolean colisionVacia = shapeColision.getBoundsInLocal().isEmpty();
                if(colisionVacia == false && ballCurrentSpeedX > 0) {
                    //Colision detectada. Mover bola hacia la izquierda
                    ballCurrentSpeedX = -3;
                    //Incrementar puntuación actual
                    score++;
                    textScore.setText(String.valueOf(score));
                }
            })
        );
          
        animationBall.setCycleCount(Timeline.INDEFINITE);
        animationBall.play();
        
        scene.setOnKeyPressed((KeyEvent event) -> {
            switch(event.getCode()){
                case UP:
                    //Pulsa tecla hacia arriba
                    stickCurrentSpeed = -6;
                    break;
                case DOWN:
                    //Pulsa tecla hacia arriba
                    stickCurrentSpeed = 6;
                    break;    
            }
        });
        
        scene.setOnKeyReleased((KeyEvent event) -> {
            stickCurrentSpeed = 0;
        }); 
    }

    public static void main(String[] args) {
        launch();
    }

}