package anglerquiz.capture_questions;

import java.net.URL;

public class Question {
	String question;
	String answer0;
	String answer1;
	String answer2;
	String nextPage;
	Integer rightAnswer;
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer1() {
		return answer1;
	}
	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}
	public String getAnswer2() {
		return answer2;
	}
	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}
	public String getAnswer0() {
		return answer0;
	}
	public void setAnswer0(String answer0) {
		this.answer0 = answer0;
	}
	public String getNextPage() {
		return nextPage;
	}
	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}
	public Integer getRightAnswer() {
		return rightAnswer;
	}
	public void setRightAnswer(Integer rightAnswer) {
		this.rightAnswer = rightAnswer;
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("question: "+getQuestion()+"\n");
		result.append("answer0: "+getAnswer0()+"\n");
		result.append("answer1: "+getAnswer1()+"\n");
		result.append("answer2: "+getAnswer2()+"\n");
		result.append("right: "+getRightAnswer()+"\n");
		result.append("nextPage: "+getNextPage());
					
		return result.toString();
	}
}
