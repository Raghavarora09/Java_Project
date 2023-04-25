import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import java.io.File;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicPlayer extends Application {
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Button playPauseButton;
    private Button stopButton;
    private Button shuffleButton;
    private Button nextButton;
    private Button previousButton;
    private Label timeElapsedLabel;
    private Label currentSongLabel;
    private List<File> playlist;
    private int currentSongIndex;
    private boolean shuffleMode;

    @Override
    public void start(Stage stage) {
        // window
        stage.setWidth(1450);
        stage.setHeight(950);
        // Create UI controls
        playPauseButton = new Button("Play");
        stopButton = new Button("Reset");
        shuffleButton = new Button("Shuffle");
        nextButton = new Button("Next");
        previousButton = new Button("Previous");
        timeElapsedLabel = new Label("00:00");
        timeElapsedLabel.setFont(new Font(22));
        timeElapsedLabel.setStyle("-fx-text-fill: red;");

        // Set button widths
        playPauseButton.setPrefWidth(100);
        playPauseButton.setStyle("-fx-background-color: #3CB371; -fx-text-fill: white;");
        stopButton.setPrefWidth(100);
        stopButton.setStyle("-fx-background-color: #7e3e9e; -fx-text-fill: white;");
        shuffleButton.setPrefWidth(100);
        shuffleButton.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white;");
        nextButton.setPrefWidth(100);
        nextButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
        previousButton.setPrefWidth(100);
        previousButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");

        // Create button box
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(previousButton, playPauseButton, stopButton, shuffleButton, nextButton,
                timeElapsedLabel);
        buttonBox.setPadding(new Insets(10, 0, 50, 0));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Load the image
        Image backgroundImage = new Image("file:/// #ADD THE BACKGROUND IMAGE PATH HERE# ");

        // Create a BackgroundImage object with fixed size
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true,
                false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, backgroundSize);

        // Set the background of the BorderPane
        BorderPane layout = new BorderPane();
        layout.setBackground(new Background(background));

        // Set the size of the BorderPane
        double width = 800;
        double height = 1200;
        layout.setPrefSize(width, height);

        // Add the button box to the bottom of the BorderPane
        layout.setBottom(buttonBox);

        // Create playlist
        playlist = getMp3FilesFromFolder(" #ADD THE PATH TO THE LOCAL MP3 FILES# ");
        if (playlist.isEmpty()) {
            System.err.println("No MP3 files found in folder");
            return;
        }

        // Create media player for first song
        currentSongIndex = 0;
        Media media = new Media(playlist.get(currentSongIndex).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);
        // Create current song label
        currentSongLabel = new Label(playlist.get(currentSongIndex).getName());
        currentSongLabel.setFont(new Font(30));
        currentSongLabel.setStyle("-fx-text-fill: white;");

        // Add media view and current song label to layout
        layout.setCenter(mediaView);
        layout.setTop(currentSongLabel);

        // Set event handlers for buttons
        playPauseButton.setOnAction(event -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseButton.setText("Play");
            } else {
                mediaPlayer.play();
                playPauseButton.setText("Pause");
            }
        });

        // Update elapsed time label
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            int elapsedSeconds = (int) newTime.toSeconds();
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            timeElapsedLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });

        stopButton.setOnAction(event -> {
            mediaPlayer.stop();
            mediaPlayer.seek(mediaPlayer.getStartTime());
            playPauseButton.setText("Play");
        });

        shuffleButton.setOnAction(event -> {
            if (shuffleMode) {
                shuffleMode = false;
                shuffleButton.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white;");
            } else {
                shuffleMode = true;
                shuffleButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white;");
            }
        });

        nextButton.setOnAction(event -> {
            if (shuffleMode) {
                currentSongIndex = new Random().nextInt(playlist.size());
            } else {
                currentSongIndex++;
                if (currentSongIndex >= playlist.size()) {
                    currentSongIndex = 0;
                }
            }
            playSelectedSong();
        });

        previousButton.setOnAction(event -> {
            if (shuffleMode) {
                currentSongIndex = new Random().nextInt(playlist.size());
            } else {
                currentSongIndex--;
                if (currentSongIndex < 0) {
                    currentSongIndex = playlist.size() - 1;
                }
            }
            playSelectedSong();
        });

        // Set scene and show stage
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setTitle("Music Player");
        stage.show();
    }

    private List<File> getMp3FilesFromFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<File> mp3Files = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".mp3")) {
                mp3Files.add(file);
            }
        }
        return mp3Files;
    }

    private void playSelectedSong() {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        Media media = new Media(playlist.get(currentSongIndex).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        currentSongLabel.setText(playlist.get(currentSongIndex).getName());
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            int elapsedSeconds = (int) newTime.toSeconds();
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            timeElapsedLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });
        playPauseButton.setText("Pause");
        mediaPlayer.play();

        mediaPlayer.setOnEndOfMedia(() -> {
            playNextSong();
        });
    }

    private void playNextSong() {
        if (shuffleMode) {
            currentSongIndex = new Random().nextInt(playlist.size());
        } else {
            currentSongIndex++;
            if (currentSongIndex >= playlist.size()) {
                currentSongIndex = 0;
            }
        }
        playSelectedSong();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
