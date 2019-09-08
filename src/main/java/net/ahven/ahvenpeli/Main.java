package net.ahven.ahvenpeli;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.sound.sampled.Clip;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.ahven.ahvenpeli.config.Quiz;

public class Main extends Application {
	private Quiz quiz;
	private StackPane root;
	private Pane welcomeScreenPane;
	private WelcomeScreenController welcomeScreenController;

	private Optional<Clip> correctClip = Optional.empty();
	private Optional<Clip> incorrectClip = Optional.empty();

	@Override
	public void start(Stage primaryStage) throws IOException {
		quiz = Quiz.fromFile(new File(Util.confDir + "/config.json"));
		root = new StackPane();

		primaryStage.setTitle("Ahven Peli");
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setFullScreen(true);

		root.getStyleClass().add("ahven-root");
		Util.initBackground(quiz, root);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeScreen.fxml"));
		loader.setControllerFactory((c) -> new WelcomeScreenController(quiz, this::startGame, this::gameCompleted));
		welcomeScreenPane = loader.load();
		welcomeScreenController = loader.getController();
		loadWelcomeScreen();
		
		Scene scene = new Scene(root, 800, 600, Color.WHITE);
		scene.getStylesheets().add("/net/ahven/ahvenpeli/style.css");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest((t) -> {
		        Platform.exit();
		        System.exit(0);
		    });

		if (quiz.getCorrectAnswerSoundPath() != null) {
			correctClip = Optional.of(Util.loadAudioClip(quiz.getCorrectAnswerSoundPath()));
		}
		if (quiz.getIncorrectAnswerSoundPath() != null) {
			incorrectClip = Optional.of(Util.loadAudioClip(quiz.getIncorrectAnswerSoundPath()));
		}

		loadWelcomeScreen();
	}

	private void loadWelcomeScreen() {
		if (root.getChildren().size() > 1) {
			root.getChildren().remove(1);
		}
		welcomeScreenController.reset();
		root.getChildren().add(welcomeScreenPane);
	}

	private void startGame(Game game) {
		game.setCorrectClip(correctClip);
		game.setIncorrectClip(incorrectClip);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("GameScreen.fxml"));
		loader.setControllerFactory((c) -> new GameScreenController(game));
		Pane pane;
		try {
			pane = loader.load();
			root.getChildren().remove(1);
			root.getChildren().add(pane);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void gameCompleted(Game game) {
		welcomeScreenController.addHiscoreEntry(new HiscoreEntry(game.getPlayerName(), game.scoreProperty().get()));

		FXMLLoader loader = new FXMLLoader(getClass().getResource("CompletedScreen.fxml"));
		loader.setControllerFactory((c) -> new CompletedScreenController(quiz, game));
		Pane pane;
		try {
			pane = loader.load();
			root.getChildren().remove(1);
			root.getChildren().add(pane);
			new Thread(() -> {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				Platform.runLater(() -> loadWelcomeScreen());
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			Util.confDir = args[0];
		}
		launch(args);
	}

}
