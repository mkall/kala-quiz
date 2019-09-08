package net.ahven.ahvenpeli.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.Gson;

import net.ahven.ahvenpeli.Util;

public class Quiz {
	private String title;
	private List<String> welcomeText;
	private List<String> startGameText;
	private List<String> congratulationsText;
	private List<String> finalScoreText;
	private List<Language> languages;

	private int questionsPerGame;
	private int timePerQuestion;
	private int revealAnswerTime;
	private int scorePerCorrectAnswer;
	private int scoreMaximumTimeBonus;
	private String backgroundImagePath;
	private String correctAnswerSoundPath;
	private String incorrectAnswerSoundPath;

	private List<Question> questions;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getWelcomeText() {
		return welcomeText;
	}

	public String getWelcomeTextByLocale(String locale) {
		return Util.getByLocale(welcomeText, locale);
	}

	public void setWelcomeText(List<String> welcomeText) {
		this.welcomeText = welcomeText;
	}

	public List<String> getStartGameText() {
		return startGameText;
	}

	public String getStartGameByLocale(String locale) {
		return Util.getByLocale(startGameText, locale);
	}

	public void setStartGameText(List<String> startGameText) {
		this.startGameText = startGameText;
	}

	public List<String> getCongratulationsText() {
		return congratulationsText;
	}

	public String getCongratulationsTextByLocale(String locale) {
		return Util.getByLocale(congratulationsText, locale);
	}

	public void setCongratulationsText(List<String> congratulationsText) {
		this.congratulationsText = congratulationsText;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	public int getQuestionsPerGame() {
		return questionsPerGame;
	}

	public void setQuestionsPerGame(int questionsPerGame) {
		this.questionsPerGame = questionsPerGame;
	}

	/**
	 * In seconds
	 */
	public int getTimePerQuestion() {
		return timePerQuestion;
	}

	/**
	 * In seconds
	 */
	public void setTimePerQuestion(int timePerQuestion) {
		this.timePerQuestion = timePerQuestion;
	}

	/**
	 * In seconds
	 */
	public int getRevealAnswerTime() {
		return revealAnswerTime;
	}

	/**
	 * In seconds
	 */
	public void setRevealAnswerTime(int revealAnswerTime) {
		this.revealAnswerTime = revealAnswerTime;
	}

	public int getScorePerCorrectAnswer() {
		return scorePerCorrectAnswer;
	}

	public void setScorePerCorrectAnswer(int scorePerCorrectAnswer) {
		this.scorePerCorrectAnswer = scorePerCorrectAnswer;
	}

	public int getScoreMaximumTimeBonus() {
		return scoreMaximumTimeBonus;
	}

	public void setScoreMaximumTimeBonus(int scoreMaximumTimeBonus) {
		this.scoreMaximumTimeBonus = scoreMaximumTimeBonus;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public String getBackgroundImagePath() {
		return backgroundImagePath;
	}

	public void setBackgroundImagePath(String backgroundImagePath) {
		this.backgroundImagePath = backgroundImagePath;
	}

	public String getCorrectAnswerSoundPath() {
		return correctAnswerSoundPath;
	}

	public void setCorrectAnswerSoundPath(String correctAnswerSoundPath) {
		this.correctAnswerSoundPath = correctAnswerSoundPath;
	}

	public String getIncorrectAnswerSoundPath() {
		return incorrectAnswerSoundPath;
	}

	public void setIncorrectAnswerSoundPath(String incorrectAnswerSoundPath) {
		this.incorrectAnswerSoundPath = incorrectAnswerSoundPath;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}


	@Override
	public String toString() {
		return "Quiz [title=" + title + ", welcomeText=" + welcomeText + ", languages=" + languages
				+ ", timePerQuestion=" + timePerQuestion + ", backgroundImagePath=" + backgroundImagePath
				+ ", questions=" + questions + "]";
	}

	/**
	 * Reads the quiz configuration file and validates it.
	 * 
	 * @throws IOException
	 * @throws RuntimeException
	 */
	public static Quiz fromFile(File file) throws IOException {
		try (InputStream is = new FileInputStream(file); InputStreamReader isr = new InputStreamReader(is, "UTF-8")) {
			Quiz quiz = new Gson().fromJson(isr, Quiz.class);
			if (quiz.getTitle() == null) {
				throw new RuntimeException("No title defined");
			}
			if (quiz.getLanguages() == null || quiz.getLanguages().isEmpty()) {
				throw new RuntimeException("No languages defined");
			}
			if (quiz.getWelcomeText() == null) {
				throw new RuntimeException("No welcome text defined");
			}
			Util.hasLocalizationFor(quiz.getWelcomeText(), quiz.getLanguages());
			if (quiz.getStartGameText() == null) {
				throw new RuntimeException("No start game text defined");
			}
			Util.hasLocalizationFor(quiz.getStartGameText(), quiz.getLanguages());

			if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
				throw new RuntimeException("No questions defined");
			}
			if (quiz.getBackgroundImagePath() != null && !Util.hasFileInPath(quiz.getBackgroundImagePath())) {
				throw new RuntimeException("Quiz background image not found: " + quiz.getBackgroundImagePath());
			}
			if (quiz.getCorrectAnswerSoundPath() != null && !Util.hasFileInPath(quiz.getCorrectAnswerSoundPath())) {
				throw new RuntimeException("Correct answer sound refers to a path that does not exist: "
						+ quiz.getCorrectAnswerSoundPath());
			}
			if (quiz.getIncorrectAnswerSoundPath() != null && !Util.hasFileInPath(quiz.getIncorrectAnswerSoundPath())) {
				throw new RuntimeException("Incorrect answer sound refers to a path that does not exist: "
						+ quiz.getIncorrectAnswerSoundPath());
			}

			for (Question q : quiz.getQuestions()) {
				if (q.getText() == null && q.getImagePath() == null) {
					throw new RuntimeException("Question has neither text nor image");
				}
				if (q.getText() != null) {
					Util.hasLocalizationFor(q.getText(), quiz.getLanguages());
				}
				if (q.getImagePath() != null && !Util.hasFileInPath(q.getImagePath())) {
					throw new RuntimeException("Question refers to an image that does not exist: " + q.getImagePath());
				}
				if (q.getOptions() == null || q.getOptions().isEmpty()) {
					throw new RuntimeException("Found a question with no options");
				}
				for (Option o : q.getOptions()) {
					if (o.getText() == null && o.getImagePath() == null) {
						throw new RuntimeException("Option has neither text nor image");
					}
					if (o.getText() != null) {
						Util.hasLocalizationFor(o.getText(), quiz.getLanguages());
					}
					if (o.getImagePath() != null && !Util.hasFileInPath(o.getImagePath())) {
						throw new RuntimeException(
								"Question option refers to an image that does not exist: " + q.getImagePath());
					}
				}
			}
			return quiz;
		}
	}
}
