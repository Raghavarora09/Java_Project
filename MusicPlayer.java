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
    private List<File> playlist;
    private int currentSongIndex;
    private boolean shuffleMode;

    @Override
    public void start(Stage stage) {
        // Create UI controls
        playPauseButton = new Button("Play");
        stopButton = new Button("Stop");
        shuffleButton = new Button("Shuffle");
        timeElapsedLabel = new Label("00:00");
        timeElapsedLabel.setFont(new Font(20));

        // Set button widths
        playPauseButton.setPrefWidth(100);
        stopButton.setPrefWidth(100);
        shuffleButton.setPrefWidth(100);

        // Create button box
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(playPauseButton, stopButton, shuffleButton, timeElapsedLabel);
        buttonBox.setPadding(new Insets(10, 0, 50, 0));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Create layout
        BorderPane layout = new BorderPane();
        layout.setBottom(buttonBox);

        // Create playlist
        playlist = getMp3FilesFromFolder("C:\\mp3_folder");
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

        Collections.shuffle(mp3Files);
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

    private void handleShuffle() {
        if (!shuffleMode) {
            shuffleMode = true;
            shuffleButton.setStyle("-fx-background-color: lightblue;");
        }

        // Get random song index
        int randomIndex = new Random().nextInt(playlist.size());

        // If current song is last in playlist, go back to beginning
        if (currentSongIndex == playlist.size() - 1) {
            currentSongIndex = 0;
        } else {
            currentSongIndex++;
        }

        // Set media player to play next song in playlist
        Media media = new Media(playlist.get(currentSongIndex).toURI().toString());
        mediaPlayer.stop();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaView.setMediaPlayer(mediaPlayer);

        // Update button and label text
        playPauseButton.setText("Pause");
        timeElapsedLabel.setText("00:00");
    }

    public static void main(String[] args) {
        launch(args);
    }
}