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
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import net.ahven.ahvenpeli.config.Quiz;

public class StartScreenController {
	private final Quiz quiz;
	private final Consumer<Game> abortGameOp;
	private final Consumer<Game> startGameOp;
	private final Consumer<Game> gameCompletedOp;

	private final List<HiscoreEntry> hiscoreEntries = new ArrayList<>();

	private final String locale;

	@FXML
	private GridPane hiscorePane;

	@FXML
	private Label instructionLabel;

	@FXML
	private TextField nameField;

	@FXML
	private Text nameFieldPlaceholder;

	@FXML
	private Button startButton;

	@FXML
	private Pane keyboardPane;

	@FXML
	private Button stopButton;

	public StartScreenController(Quiz quiz, String locale, Consumer<Game> abortGameOp, Consumer<Game> startGameOp,
			Consumer<Game> gameCompletedOp) {
		this.quiz = quiz;
		this.locale = locale;
		this.abortGameOp = abortGameOp;
		this.startGameOp = startGameOp;
		this.gameCompletedOp = gameCompletedOp;
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
		nameFieldPlaceholder.getStyleClass().add("name-placeholder");
		nameFieldPlaceholder.visibleProperty().bind(nameField.textProperty().isEmpty());
		nameFieldPlaceholder.setText(quiz.getEnterNameTextByLocale(locale));
		nameField.textProperty().addListener((a, o, n) -> {
			if (n.length() > 20) {
				nameField.setText(o);
			}
		});
		ObjectProperty<Node> kbNode = new SimpleObjectProperty<>(nameField);
		VirtualKeyboard keyboard = new VirtualKeyboard(kbNode);
		keyboardPane.getChildren().add(keyboard.view());

		instructionLabel.setText(quiz.getInstructionTextByLocale(locale)
				.replace("${questions}", Integer.toString(quiz.getQuestionsPerGame()))
				.replace("${time}", Integer.toString(quiz.getTimePerQuestion()))
				.replace("${score}", Integer.toString(quiz.getScorePerCorrectAnswer()))
				.replace("${bonus}", Integer.toString(quiz.getScoreMaximumTimeBonus())));
		instructionLabel.setWrapText(true);
		instructionLabel.setTextAlignment(TextAlignment.CENTER);
		startButton.setText(quiz.getStartGameByLocale(locale));
		startButton.addEventHandler(ActionEvent.ACTION, (e) -> startGame());
		stopButton.setGraphic(new ImageView(Util.loadImageFromPath(quiz.getStopImagePath())));
		stopButton.setOnAction((e) -> abortGameOp.accept(null));
	}

	private void startGame() {
		startGameOp.accept(new Game(quiz, locale, nameField.getText(), abortGameOp, gameCompletedOp));
	}
}
