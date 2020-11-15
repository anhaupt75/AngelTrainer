package anglerquiz.capture_questions;

import java.util.ArrayList;
import java.util.List;

public class ResponseParser {
	
	private String getForm(String pageContent)
	{
		String result=pageContent.substring(pageContent.indexOf("<form"));
		result=result.substring(0,result.indexOf("</form>"));
		return result;
	}
	private List<String> getTRs(String originalForm)
	{
		List<String> result=new ArrayList<String>();
		String form=originalForm;
		do
		{
			String tr=form.substring(form.indexOf("<tr"));
			tr=tr.substring(0,tr.indexOf("</tr>"));
			result.add(tr);
			form=form.substring(form.indexOf("</tr>")+"</tr>".length());
		} while (form.indexOf("<tr")!=-1);
		return result;
	}
	private String getQuestion(String tr)
	{
		String result=tr.substring(tr.indexOf("<b>")+3);
		result=result.substring(0,result.indexOf("</b>"));
		return result;
	}
	private String getAnswer(String tr)
	{
		String result=tr.substring(tr.lastIndexOf("<input"));
		result=result.substring(result.indexOf(">")+1);
		result=result.substring(0,result.indexOf("</td>"));
		return result;
	}
	String getRightAnswerName(String trQuestion)
	{
		String queryToken="'richtig' value='";
		String result=trQuestion.substring(trQuestion.indexOf(queryToken)+queryToken.length());
		result=result.substring(0,result.indexOf("'"));
		return result;
	}
	String getAnswerName(String trAnswer)
	{
		String queryToken="value='";
		String result=trAnswer.substring(trAnswer.lastIndexOf(queryToken)+queryToken.length());
		result=result.substring(0,result.indexOf("'"));
		return result;
	}
	private int getRightAnswer(String trQuestion,String answer0,String answer1,String answer2) throws ParsingException
	{
		String rightAnswerName=getRightAnswerName(trQuestion);
//		String nameOfAnswer0=getAnswerName(answer0);
//		String nameOfAnswer1=getAnswerName(answer1);
//		String nameOfAnswer2=getAnswerName(answer2);
		if (rightAnswerName.equals(getAnswerName(answer0))) return 0;
		if (rightAnswerName.equals(getAnswerName(answer1))) return 1;
		if (rightAnswerName.equals(getAnswerName(answer2))) return 2;
		throw new ParsingException();
//		return -1;
	}
	String getUrlLocation(String form)
	{
		String queryToken="action='";
		String result=form.substring(form.lastIndexOf(queryToken)+queryToken.length());
		result=result.substring(0,result.indexOf("'"));
		return result;
	}
	
	Question parse(String pageContent) throws ParsingException
	{
		Question response = new Question();
		String form=getForm(pageContent);
		List<String> trs=getTRs(form);
		response.setQuestion(getQuestion(trs.get(0)).trim());
		response.setAnswer0(getAnswer(trs.get(2)).trim());
		response.setAnswer1(getAnswer(trs.get(3)).trim());
		response.setAnswer2(getAnswer(trs.get(4)).trim());
		response.setNextPage(getUrlLocation(form));
		int rightAnswerId=getRightAnswer(trs.get(0), trs.get(2), trs.get(3), trs.get(4));
		response.setRightAnswer(rightAnswerId);
		return response;
	}
}
