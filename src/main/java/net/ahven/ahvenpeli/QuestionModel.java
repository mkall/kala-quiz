package net.ahven.ahvenpeli;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.ahven.ahvenpeli.config.Option;
import net.ahven.ahvenpeli.config.Question;


public class QuestionModel {
	private final Question question;
	private final List<OptionModel> options;
	
	private boolean canAnswer = true;
	
	public QuestionModel(Question question) {
		this.question = question;
		this.options = Collections.unmodifiableList(question.getOptions().stream().map((o) -> new OptionModel(o)).collect(Collectors.toList()));
	}
	
	public Question getQuestion() {
		return question;
	}
	
	/**
	 * Unmodifiable
	 */
	public List<OptionModel> getOptions() {
		return options;
	}
	
	public void optionSelected(Option option) {
		if (canAnswer) {
			canAnswer = false;
			options.stream().filter((o) -> o.getOption().equals(option)).findFirst().get().setSelected(true);
			revealAnswer();
		}
	}

	public boolean canAnswer() {
		return canAnswer;
	}

	public void setCanAnswer(boolean canAnswer) {
		this.canAnswer = canAnswer;
	}

	public void revealAnswer() {
		options.stream().filter((o) -> o.getOption().isCorrect()).findFirst().get().setBlink(true);
	}
}
