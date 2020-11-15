package anglerquiz.capture_questions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CaptureQuestionsMain {

	/**
	 * @param args
	 */

	String getContentOfPage(URL pageUrl) throws IOException
	{
		StringBuilder result=new StringBuilder();
		BufferedReader reader=new BufferedReader(new InputStreamReader(pageUrl.openStream(),"windows-1252"));
		String line;
		while ((line=reader.readLine())!=null)
		{
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}
	
	void saveResponse(Map<String,List<Question>> allSections,File saveFile) throws IOException
	{
		FileWriter out = new FileWriter(saveFile,Charset.forName("UTF-8"));
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.write("<sections>\n");
		for (String sectionName:allSections.keySet())
		{
			List<Question> section=allSections.get(sectionName);
			out.write("\t<section name=\""+sectionName+"\">\n");
			for (Question question:section)
			{
				out.write("\t\t<item>\n");
				out.write("\t\t\t<question>"+question.getQuestion()+"</question>\n");	
				out.write("\t\t\t<answer0>"+question.getAnswer0()+"</answer0>\n");	
				out.write("\t\t\t<answer1>"+question.getAnswer1()+"</answer1>\n");	
				out.write("\t\t\t<answer2>"+question.getAnswer2()+"</answer2>\n");	
				out.write("\t\t\t<rightAnswer>"+question.getRightAnswer()+"</rightAnswer>\n");	
				out.write("\t\t</item>\n");
			}
			out.write("\t</section>\n");
		}
		out.write("</sections>\n");
		out.close();
	}
	
	List<Question> captureSection(String initialURL) throws Exception
	{
		URL pageToCaptureUrl=new URL(initialURL);
		ResponseParser parser=new ResponseParser();
		List<Question> allQuestions=new ArrayList<Question>();
		for (int i=0;i<100;++i)
		{
			String pageContent=getContentOfPage(pageToCaptureUrl);
			try {
				Question response=parser.parse(pageContent);
				System.out.println(response);
				String nextUrl=pageToCaptureUrl.getProtocol()+"://"+pageToCaptureUrl.getHost()+"/schule/"+response.getNextPage();
				System.out.println(nextUrl);
				pageToCaptureUrl=new URL(nextUrl);
				allQuestions.add(response);
			}
			catch (ParsingException ex) {
				break;
			}
		}
		return allQuestions;
	}
	void run(File saveFile) throws Exception
	{
		Map<String,List<Question>> result=new LinkedHashMap<String, List<Question>>();
		result.put("Allgemeine Fischkunde", captureSection("http://www.creutzburg.info/schule/index.php?modu=fischerei&ausw=1&thema=1"));
		result.put("Spezielle Fischkunde", captureSection("http://www.creutzburg.info/schule/index.php?modu=fischerei&ausw=1&thema=2"));
		result.put("Gewässerkunde", captureSection("http://www.creutzburg.info/schule/index.php?modu=fischerei&ausw=1&thema=3"));
		result.put("Natur-, Tier- und Umweltschutz", captureSection("http://www.creutzburg.info/schule/index.php?modu=fischerei&ausw=1&thema=4"));
		result.put("Gerätekunde", captureSection("http://www.creutzburg.info/schule/index.php?modu=fischerei&ausw=1&thema=5"));
		result.put("Gesetzeskunde", captureSection("http://www.creutzburg.info/schule/index.php?modu=fischerei&ausw=1&thema=6"));
		saveResponse(result,saveFile);
	}

	
	public static void main(String[] args) throws Exception{
		new CaptureQuestionsMain().run(new File("angeltrainer.xml"));
	}
}