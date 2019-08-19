package net.ahven.ahvenpeli;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.ahven.ahvenpeli.config.Quiz;

public class Main extends Application {
	private Quiz quiz;
	private StackPane root;
	private Pane welcomeScreenPane;

	@Override
	public void start(Stage primaryStage) throws IOException {
		quiz = Quiz.fromFile(new File("conf/config.json"));
		root = new StackPane();

		primaryStage.setTitle("Ahven Peli");

		root.getStyleClass().add("ahven-root");
		Util.initBackground(quiz, root);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeScreen.fxml"));
		loader.setControllerFactory((c) -> new WelcomeScreenController(quiz, this::startGame));
		welcomeScreenPane = loader.load();
		root.getChildren().add(welcomeScreenPane);
		
		Scene scene = new Scene(root, 800, 600, Color.WHITE);
		scene.getStylesheets().add("/net/ahven/ahvenpeli/style.css");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest((t) -> {
		        Platform.exit();
		        System.exit(0);
		    });
	}

	private void startGame(Game game) {
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

	public static void main(String[] args) {
		launch(args);
	}

}
