import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MusicPlayer extends Application {
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Button playPauseButton;
    private Button stopButton;
    private Button shuffleButton;
    private Label timeElapsedLabel;
    private Label currentSongLabel;
    private List<File> playlist;
    private int currentSongIndex;
    private boolean shuffleMode;
    private ToggleGroup shuffleGroup = new ToggleGroup();

    @Override
    public void start(Stage stage) {
        // Create UI controls
        playPauseButton = new Button("Play");
        stopButton = new Button("Reset");
        shuffleButton = new Button("Shuffle");
        timeElapsedLabel = new Label("00:00");
        timeElapsedLabel.setFont(new Font(20));

        // Set button widths
        playPauseButton.setPrefWidth(100);
        playPauseButton.setStyle("-fx-background-color: #3CB371; -fx-text-fill: white;");
        stopButton.setPrefWidth(100);
        stopButton.setStyle("-fx-background-color: #DC143C; -fx-text-fill: white;");
        shuffleButton.setPrefWidth(100);
        shuffleButton.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white;");

        // Create button box
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(playPauseButton, stopButton, shuffleButton, timeElapsedLabel);
        buttonBox.setPadding(new Insets(10, 0, 50, 0));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Create layout
        BorderPane layout = new BorderPane();
        layout.setBottom(buttonBox);
        layout.setStyle("-fx-background-color: #02A3FA;");

        // Create playlist
        playlist = getMp3FilesFromFolder("E:/programs/Java Project/audio files");
        if (playlist.isEmpty()) {
            System.err.println("No MP3 files found in folder");
            return;
        }

        // Create media player for first song
        currentSongIndex = 0;
        Media media = new Media(playlist.get(currentSongIndex).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);
        mediaView = new MediaView(mediaPlayer);
        layout.setCenter(mediaView);

        // name of current audio file
        HBox topBox = new HBox();
        currentSongLabel = new Label(playlist.get(currentSongIndex).getName());
        currentSongLabel.setFont(new Font(50)); // Increase font size
        topBox.getChildren().add(currentSongLabel);
        layout.setTop(topBox);

        // Center label in HBox
        HBox.setHgrow(currentSongLabel, javafx.scene.layout.Priority.ALWAYS);
        topBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Update elapsed time label
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            int elapsedSeconds = (int) newTime.toSeconds();
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            timeElapsedLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });

        // Set button event handlers
        playPauseButton.setOnAction(e -> handlePlayPause());
        stopButton.setOnAction(e -> handleStop());
        shuffleButton.setOnAction(e -> handleShuffle());

        // Set window properties
        Scene scene = new Scene(layout, 800, 1000);
        stage.setTitle("Music Player");
        stage.setScene(scene);
        stage.show();

        // Add a listener to detect when the song has ended
        mediaPlayer.setOnEndOfMedia(() -> {
            if (shuffleMode) {
                handleShuffle();
            } else {
                handleNext();
            }
        });
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

    private void handlePlayPause() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("Play");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("Pause");
        }
    }

    private void handleStop() {
        mediaPlayer.stop();
        mediaPlayer.seek(mediaPlayer.getStartTime());
        playPauseButton.setText("Play");
    }

    private void handleNext() {
        currentSongIndex++;
        if (currentSongIndex >= playlist.size()) {
            currentSongIndex = 0;
        }
        Media media = new Media(playlist.get(currentSongIndex).toURI().toString());
        mediaPlayer.stop();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            int elapsedSeconds = (int) newTime.toSeconds();
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            timeElapsedLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });
        mediaPlayer.setOnEndOfMedia(() -> handleNext());
        currentSongLabel.setText(playlist.get(currentSongIndex).getName());
    }

    private void handleShuffle() {
        if (shuffleMode) {
            shuffleMode = false;
            shuffleButton.setText("Shuffle");
        } else {
            shuffleMode = true;
            shuffleButton.setText("Unshuffle");
            Collections.shuffle(playlist, new Random());
            currentSongIndex = 0;
            Media media = new Media(playlist.get(currentSongIndex).toURI().toString());
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
