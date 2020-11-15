package anglerquiz.capture_questions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QuestionReader {
	Map<String,List<Question>> readQuestionFromResource(InputStream in) throws Exception
	{
		Map<String,List<Question>> result= new HashMap<String, List<Question>>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(in);
//		doc.getDocumentElement().normalize();
		 
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("section");
		System.out.println("-----------------------");
		System.out.println(nList.getLength());
 
		for (int temp = 0; temp < nList.getLength(); temp++) {
 
		   Node nNode = nList.item(temp);
		   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			   
			   String sectionName=getAttributeValue("name", (Element)nNode);
			   System.out.println("section name: "+sectionName);
			   List<Question> questions=new ArrayList<Question>();
			   result.put(sectionName, questions);
			   
			   for (Node nNode2=nNode.getFirstChild();nNode2!=null;nNode2=nNode2.getNextSibling())
			   {
				   if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
					   Element eElement = (Element) nNode2;
					   
					   Question question=new Question();
					   String questionStr=getTagValue("question", eElement);
					   // remove leading question number
					   questionStr=questionStr.substring(questionStr.indexOf(".")+1).trim();
					   question.setQuestion(questionStr);
					   question.setAnswer0(getTagValue("answer0", eElement));
					   question.setAnswer1(getTagValue("answer1", eElement));
					   question.setAnswer2(getTagValue("answer2", eElement));
					   question.setRightAnswer(Integer.parseInt(getTagValue("rightAnswer", eElement)));
					   questions.add(question);
				   }
										   		   
			   }
		   }
		}
		
		return result;
		
	}
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	 
	        Node nValue = (Node) nlList.item(0);
	 
		return nValue.getNodeValue();
	  }
	private static String getAttributeValue(String sAttribute, Element eElement) {
		return eElement.getAttribute(sAttribute);
	  }
	
	private Question scramble(Question originalQuestion)
	{
		Map<Integer,Integer> swapMap=new HashMap<Integer,Integer>();
		for (int i=0;i<3;++i)
		{
			while (true)
			{
				int ramdomPosition=(int)(Math.random()*3);
				if (!swapMap.containsValue(ramdomPosition))
				{
					swapMap.put(i, ramdomPosition);
					break;
				}
			}		
		}
		Question result=new Question();
		result.question=originalQuestion.question;
		if (swapMap.get(0)==0) result.answer0=originalQuestion.answer0;
		else if (swapMap.get(0)==1) result.answer1=originalQuestion.answer0;
		else if (swapMap.get(0)==2) result.answer2=originalQuestion.answer0;
		if (swapMap.get(1)==0) result.answer0=originalQuestion.answer1;
		else if (swapMap.get(1)==1) result.answer1=originalQuestion.answer1;
		else if (swapMap.get(1)==2) result.answer2=originalQuestion.answer1;
		if (swapMap.get(2)==0) result.answer0=originalQuestion.answer2;
		else if (swapMap.get(2)==1) result.answer1=originalQuestion.answer2;
		else if (swapMap.get(2)==2) result.answer2=originalQuestion.answer2;
		result.rightAnswer=swapMap.get(originalQuestion.getRightAnswer());
		return result;
	}
	public void reorderSections(Map<String,List<Question>> sections)
	{
		for (String sectionName:sections.keySet())
		{
			LinkedList<Question> originalQuestions=new LinkedList<Question>(sections.get(sectionName));
			ArrayList<Question> newQuestions=new ArrayList<Question>();
			while (!originalQuestions.isEmpty())
			{
				int indexToRemove=(int)(Math.random()*originalQuestions.size());
				newQuestions.add(scramble(originalQuestions.remove(indexToRemove)));
			}
			sections.put(sectionName, newQuestions);
		}
	}

}
