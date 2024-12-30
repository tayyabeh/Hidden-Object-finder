package com.example.hiddenobjectsfinder;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class HelloApplication extends Application {
    private VBox mainContainer;
    private HBox objectListContainer;
    private List<String> hiddenObjects;
    private Stack<String> hintStack;
    private int foundObjects = 0;
    private int hintsUsed = 0;
    private int score = 100;
    private long startTime;

    private MediaPlayer mediaPlayer;

    private static final int TOTAL_OBJECTS = 5;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.mainContainer = new VBox(10);
        this.mainContainer.setAlignment(Pos.CENTER);
        createStartScreen(stage);
        Scene scene = new Scene(this.mainContainer, 1024, 600);
        stage.setTitle("Enhanced Hidden Object Finder");
        stage.setScene(scene);
        stage.show();
    }

    private void createStartScreen(Stage stage) {
        StackPane startContainer = new StackPane();

        ImageView backgroundImage = new ImageView(new Image("file:src/main/resources/com/example/hiddenobjectsfinder/start_background.jpg"));
        backgroundImage.setFitWidth(1024);
        backgroundImage.setFitHeight(580);

        VBox startScreen = new VBox(20);
        startScreen.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Enhanced Hidden Object Finder");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        titleLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        startButton.setOnAction(e -> {
            playBackgroundMusic();
            startGame(stage);
        });

        startScreen.getChildren().addAll(titleLabel, startButton);
        startContainer.getChildren().addAll(backgroundImage, startScreen);
        this.mainContainer.getChildren().add(startContainer);
    }

    private void startGame(Stage stage) {
        this.mainContainer.getChildren().clear();
        this.startTime = System.currentTimeMillis();

        StackPane gameContainer = new StackPane();
        gameContainer.setStyle("-fx-background-image: url('file:src/main/resources/com/example/hiddenobjectsfinder/background_image.jpg');" +
                "-fx-background-size: cover;");
        gameContainer.setPrefSize(1024, 580);

        this.hiddenObjects = new ArrayList<>(Arrays.asList("book", "glass bird", "mirror", "purse", "shoe"));
        this.hintStack = new Stack<>();
        this.hintStack.addAll(hiddenObjects);

        this.objectListContainer = new HBox(20);
        this.objectListContainer.setAlignment(Pos.CENTER);
        this.objectListContainer.setPadding(new Insets(10));
        this.objectListContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.8); " +
                        "-fx-padding: 15px; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px;"
        );

        updateObjectList();

        this.addHiddenObject(gameContainer, "book", "file:src/main/resources/com/example/hiddenobjectsfinder/hiddenobject_book.png", 0.45, 0.67, 0.3, 30);
        this.addHiddenObject(gameContainer, "glass bird", "file:src/main/resources/com/example/hiddenobjectsfinder/hiddenobject_glass_bird.png", 0.47, 0.40, 0.5, 10);
        this.addHiddenObject(gameContainer, "mirror", "file:src/main/resources/com/example/hiddenobjectsfinder/hiddenobject_mirror.png", 0.15, 0.60, 0.6, 10);
        this.addHiddenObject(gameContainer, "purse", "file:src/main/resources/com/example/hiddenobjectsfinder/hiddenobject_purse.png", 0.87, 0.60, 0.6, 10);
        this.addHiddenObject(gameContainer, "shoe", "file:src/main/resources/com/example/hiddenobjectsfinder/hiddenobject_shoe.png", 0.0, 0.88, 0.5, 0);

        ImageView hintIcon = new ImageView(new Image("file:src/main/resources/com/example/hiddenobjectsfinder/hint_bulb.png"));
        hintIcon.setFitWidth(50);
        hintIcon.setFitHeight(50);
        hintIcon.setTranslateX(420);
        hintIcon.setTranslateY(-260);
        hintIcon.setOnMouseClicked(e -> showHint(gameContainer));

        gameContainer.getChildren().add(hintIcon);

        this.mainContainer.getChildren().addAll(gameContainer, this.objectListContainer);

        setupObjectMovement(gameContainer);
    }

    private void updateObjectList() {
        this.objectListContainer.getChildren().clear();
        for (String object : hiddenObjects) {
            Label label = new Label(object);
            label.setStyle(
                    "-fx-font-size: 16px; " +
                            "-fx-padding: 8px 15px; " +
                            "-fx-background-color: white; " +
                            "-fx-border-color: black; " +
                            "-fx-border-width: 1px; " +
                            "-fx-border-radius: 5px;"
            );
            this.objectListContainer.getChildren().add(label);
        }
    }

    private void addHiddenObject(StackPane root, String objectId, String imagePath, double xPercentage, double yPercentage, double scale, double rotation) {
        Image objectImage = new Image(imagePath);
        ImageView objectView = new ImageView(objectImage);
        objectView.setTranslateX(xPercentage * 1000 - 400);
        objectView.setTranslateY(yPercentage * 600 - 300);
        objectView.setScaleX(scale);
        objectView.setScaleY(scale);
        objectView.setRotate(rotation);

        objectView.setOnMouseClicked(event -> {
            ++this.foundObjects;
            this.hiddenObjects.remove(objectId);
            this.hintStack.remove(objectId);
            updateObjectList();

            RotateTransition rotateOut = new RotateTransition(Duration.seconds(1.0), objectView);
            rotateOut.setByAngle(360);
            rotateOut.setOnFinished(e -> root.getChildren().remove(objectView));
            rotateOut.play();

            checkGameCompletion();
        });

        root.getChildren().add(objectView);
    }

    private void showHint(StackPane gameContainer) {
        if (!hintStack.isEmpty()) {
            String hint = hintStack.peek();
            hintsUsed++;
            score -= 10;

            Label hintLabel = new Label("Hint: Look for " + hint + "!");
            hintLabel.setStyle("-fx-background-color: rgba(255, 255, 0, 0.8); -fx-font-size: 18px; -fx-padding: 10px; -fx-border-radius: 5px;");
            hintLabel.setTranslateY(-250);
            gameContainer.getChildren().add(hintLabel);

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> gameContainer.getChildren().remove(hintLabel));
            pause.play();

            gameContainer.getChildren().stream()
                    .filter(node -> node instanceof ImageView)
                    .map(node -> (ImageView) node)
                    .filter(imageView -> {
                        String url = imageView.getImage().getUrl();
                        return url != null && url.contains(hint.replace(" ", "_"));
                    })
                    .findFirst()
                    .ifPresent(imageView -> {
                        ScaleTransition scale = new ScaleTransition(Duration.seconds(1.0), imageView);
                        scale.setToX(1.2);
                        scale.setToY(1.2);
                        scale.setAutoReverse(true);
                        scale.setCycleCount(4);
                        scale.play();
                    });
        }
    }

    private void setupObjectMovement(StackPane gameContainer) {
        gameContainer.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .map(node -> (ImageView) node)
                .forEach(imageView -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(10));
                    pause.setOnFinished(e -> {
                        TranslateTransition move = new TranslateTransition(Duration.seconds(0.5), imageView);
                        move.setByX(5);
                        move.setByY(5);
                        move.setAutoReverse(true);
                        move.setCycleCount(2);
                        move.play();
                    });
                    pause.play();
                });
    }

    private void checkGameCompletion() {
        if (hiddenObjects.isEmpty()) {
            long endTime = System.currentTimeMillis();
            long timeTaken = (endTime - startTime) / 1000;
            showGameCompleteScreen(timeTaken);
        }
    }

    private void showGameCompleteScreen(long timeTaken) {
        mainContainer.getChildren().clear();

        StackPane completionContainer = new StackPane();

        ImageView backgroundImage = new ImageView(new Image("file:src/main/resources/com/example/hiddenobjectsfinder/end_background.jpg"));
        backgroundImage.setFitWidth(1024);
        backgroundImage.setFitHeight(580);

        VBox completionScreen = new VBox(20);
        completionScreen.setAlignment(Pos.CENTER);
        completionScreen.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-padding: 20px; -fx-border-radius: 10px;");

        Label congratsLabel = new Label("Congratulations!");
        congratsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(Font.font("Arial", 20));

        Label timeLabel = new Label("Time taken: " + timeTaken + " seconds");
        Label starLabel = new Label("Stars: " + calculateStars());
        timeLabel.setFont(Font.font("Arial", 20));
        starLabel.setFont(Font.font("Arial", 20));

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        playAgainButton.setOnAction(e -> {
            this.foundObjects = 0;
            this.hintsUsed = 0;
            this.score = 100;
            startGame((Stage) this.mainContainer.getScene().getWindow());
        });

        Button endGameButton = new Button("End Game");
        endGameButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px; -fx-background-color: #f44336; -fx-text-fill: white;");
        endGameButton.setOnAction(e -> ((Stage) this.mainContainer.getScene().getWindow()).close());

        completionScreen.getChildren().addAll(congratsLabel, scoreLabel, timeLabel, starLabel, playAgainButton, endGameButton);
        completionContainer.getChildren().addAll(backgroundImage, completionScreen);
        mainContainer.getChildren().add(completionContainer);
    }

    private String calculateStars() {
        if (score >= 90) return "⭐⭐⭐⭐⭐";
        else if (score >= 80) return "⭐⭐⭐⭐";
        else if (score >= 70) return "⭐⭐⭐";
        else if (score >= 60) return "⭐⭐";
        else if (score >= 50) return "⭐⭐";
        else return "⭐";
    }

    private void playBackgroundMusic() {
        String musicFile = "src/main/resources/com/example/hiddenobjectsfinder/background_music.mp3";
        Media media = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }
}