package net.ahven.ahvenpeli;

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import net.ahven.ahvenpeli.config.Language;
import net.ahven.ahvenpeli.config.Quiz;

public class WelcomeScreenController {
	private final Quiz quiz;
	private final Consumer<Game> startGameOp;

	@FXML
	private Label welcomeLabel;

	@FXML
	private TextField nameField;

	@FXML
	private Button startButton;

	@FXML
	private FlowPane langPane;

	private String currLocale;

	public WelcomeScreenController(Quiz quiz, Consumer<Game> startGameOp) {
		this.quiz = quiz;
		this.startGameOp = startGameOp;

		currLocale = quiz.getLanguages().get(0).getLocale();
	}

	@FXML
	public void initialize() {
		assignLocale();

		for (Language lang : quiz.getLanguages()) {
			Button langButton = new Button();
			Image langImg = Util.loadImageFromPath(lang.getImagePath());
			langButton.setGraphic(new ImageView(langImg));
			langButton.setOnAction((e) -> {
				currLocale = lang.getLocale();
				assignLocale();
			});
			langPane.getChildren().add(langButton);
		}

		startButton.addEventHandler(ActionEvent.ACTION, (e) -> startGame());
	}

	private void startGame() {
		startGameOp.accept(new Game(quiz, currLocale, nameField.getText()));
	}

	private void assignLocale() {
		welcomeLabel.setText(quiz.getWelcomeTextByLocale(currLocale));
		startButton.setText(quiz.getStartGameByLocale(currLocale));
	}
}
