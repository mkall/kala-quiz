package net.ahven.ahvenpeli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.ahven.ahvenpeli.config.Language;
import net.ahven.ahvenpeli.config.Quiz;

public class WelcomeScreenController {
	private final Quiz quiz;
	private final Consumer<Game> startGameOp;
	private final Consumer<Game> gameCompletedOp;

	private final List<HiscoreEntry> hiscoreEntries = new ArrayList<>();

	@FXML
	private GridPane hiscorePane;

	@FXML
	private Label welcomeLabel;

	@FXML
	private TextField nameField;

	@FXML
	private Button startButton;

	@FXML
	private FlowPane langPane;

	@FXML
	private Pane keyboardPane;

	private String currLocale;

	public WelcomeScreenController(Quiz quiz, Consumer<Game> startGameOp, Consumer<Game> gameCompletedOp) {
		this.quiz = quiz;
		this.startGameOp = startGameOp;
		this.gameCompletedOp = gameCompletedOp;

		currLocale = quiz.getLanguages().get(0).getLocale();
	}

	public void addHiscoreEntry(HiscoreEntry hiscoreEntry) {
		int position = 0;
		for (int i = 0; i < hiscoreEntries.size(); i++) {
			if (hiscoreEntries.get(i).getScore() < hiscoreEntry.getScore()) {
				break;
			}
			position = i + 1;
		}
		hiscoreEntries.add(position, hiscoreEntry);
		if (hiscoreEntries.size() > 5) {
			hiscoreEntries.remove(5);
		}
		hiscorePane.getChildren().clear();
		int row = 0;
		for (int i = 0; i < hiscoreEntries.size(); i++) {
			Label positionLabel = new Label(Integer.toString((i + 1)) + ".");
			positionLabel.setPrefWidth(20);
			hiscorePane.add(positionLabel, 0, row);
			Label nameLabel = new Label(hiscoreEntries.get(i).getName());
			nameLabel.setAlignment(Pos.CENTER_LEFT);
			nameLabel.setPrefWidth(260.0);
			hiscorePane.add(nameLabel, 1, row);
			Label scoreLabel = new Label(Integer.toString(hiscoreEntries.get(i).getScore()));
			scoreLabel.setPrefWidth(80);
			scoreLabel.setAlignment(Pos.CENTER_RIGHT);

			hiscorePane.add(scoreLabel, 2, row++);
			hiscorePane.add(new Separator(Orientation.HORIZONTAL), 0, row++, 3, 1);
		}
	}

	public void reset() {
		nameField.setText("");
	}

	@FXML
	public void initialize() {
		startButton.disableProperty().bind(nameField.textProperty().isEmpty());
		nameField.setPrefWidth(200.0);
		nameField.textProperty().addListener((a, o, n) -> {
			if (n.length() > 20) {
				nameField.setText(o);
			}
		});
		ObjectProperty<Node> kbNode = new SimpleObjectProperty<>(nameField);
		VirtualKeyboard keyboard = new VirtualKeyboard(kbNode);
		keyboardPane.getChildren().add(keyboard.view());

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
		startGameOp.accept(new Game(quiz, currLocale, nameField.getText(), gameCompletedOp));
	}

	private void assignLocale() {
		welcomeLabel.setText(quiz.getWelcomeTextByLocale(currLocale));
		startButton.setText(quiz.getStartGameByLocale(currLocale));
	}
}
