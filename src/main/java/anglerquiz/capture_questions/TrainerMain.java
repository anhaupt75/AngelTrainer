package anglerquiz.capture_questions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class TrainerMain {

	/**
	 * @param args
	 */
	final JLabel lblSection = new JLabel("Wissensgebiet");
	JLabel lblQuestion = new JLabel("Frage");
	JRadioButton rbAnswer0 = new JRadioButton("Antwort 1");
	JRadioButton rbAnswer1 = new JRadioButton("Antwort 2");
	JRadioButton rbAnswer2 = new JRadioButton("Antwort 3");
	
	ButtonGroup rbGroup=new ButtonGroup();
	JComboBox cmbSection;
	JButton btnOk = new JButton("ok");
	JCheckBox cbScramble = new JCheckBox("Zufällige Fragen",true);
	String currentSection=null;
	QuestionReader reader=new QuestionReader();
	
	int currentQuestionNumber=0;
	
	Map<String,List<Question>> scrambledSections;
	Map<String,List<Question>> originalSections;
	File captureFile;
	
	Map<String,List<Question>> getSections()
	{
		return cbScramble.isSelected()?scrambledSections:originalSections;
	}
	
	private InputStream getCaptureFileInputStream(File captureFile) throws FileNotFoundException
	{
		return captureFile!=null?new FileInputStream(captureFile):getQuestionInputStream();
	}
    
	public TrainerMain(File captureFile) throws FileNotFoundException, Exception {
		this.captureFile=captureFile;
		InputStream captureFileIn=getCaptureFileInputStream(captureFile);
		scrambledSections=reader.readQuestionFromResource(captureFileIn);
		captureFileIn=getCaptureFileInputStream(captureFile);
		reader.reorderSections(scrambledSections);
		originalSections=reader.readQuestionFromResource(captureFileIn);
	}
	void updateGui()
	{
		Question currentQuestion=getSections().get(currentSection).get(currentQuestionNumber);
		lblQuestion.setText(Integer.toString(currentQuestionNumber+1)+". "+currentQuestion.question);
		rbAnswer0.setText(currentQuestion.answer0);
		rbAnswer1.setText(currentQuestion.answer1);
		rbAnswer2.setText(currentQuestion.answer2);
	}
	void addActionListeners()
	{
		final ActionListener resetQuestionsActionListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentQuestionNumber=0;
				currentSection=(String)cmbSection.getSelectedItem();
				rbGroup.clearSelection();
				reader.reorderSections(scrambledSections);
				updateGui();
			}
		};
		
		cmbSection.addActionListener(resetQuestionsActionListener);
		cbScramble.addActionListener(resetQuestionsActionListener);

		btnOk.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				boolean somethingSelected=rbAnswer0.isSelected() || rbAnswer1.isSelected() || rbAnswer2.isSelected();
				if (somethingSelected)
				{
					Question currentQuestion=getSections().get(currentSection).get(currentQuestionNumber);
					boolean answerCorrect=
							currentQuestion.rightAnswer==0 && rbAnswer0.isSelected() ||	
							currentQuestion.rightAnswer==1 && rbAnswer1.isSelected() ||
							currentQuestion.rightAnswer==2 && rbAnswer2.isSelected();
					if (answerCorrect)
					{
						if (getSections().get(currentSection).size()>(currentQuestionNumber+1))
						{
							currentQuestionNumber++;
							rbGroup.clearSelection();
							updateGui();
						}
						else
						{
							resetQuestionsActionListener.actionPerformed(e);
						}
					}
					else
					{
						String error=" (falsch)";
						rbAnswer0.setText(currentQuestion.answer0+(rbAnswer0.isSelected()?error:""));
						rbAnswer1.setText(currentQuestion.answer1+(rbAnswer1.isSelected()?error:""));
						rbAnswer2.setText(currentQuestion.answer2+(rbAnswer2.isSelected()?error:""));
					}
				}
				
			}
		});
		
	}
	
	InputStream getQuestionInputStream() throws FileNotFoundException
	{
		InputStream in2 = this.getClass().getResourceAsStream("/" + "angeltrainer.xml"); 
//		return new FileInputStream("angeltrainer.xml");
		return in2;
	}
	void showGui()
	{
		JFrame frame = new JFrame("AngelTrainer"+(captureFile!=null?" "+captureFile.getName():""));
	    frame.getContentPane().setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    cmbSection=new JComboBox(new Vector<String>(getSections().keySet()));
	    currentSection=(String)cmbSection.getSelectedItem();
	    rbGroup.add(rbAnswer0);
	    rbGroup.add(rbAnswer1);
	    rbGroup.add(rbAnswer2);
	    
	    c.insets=new Insets(10,10,10,10);
	    c.gridx=0;
	    c.gridy=0;
	    frame.getContentPane().add(lblSection,c);
	    
	    c.gridx=1;
	    frame.getContentPane().add(cmbSection,c);
	    c.gridx=2;
	    frame.getContentPane().add(cbScramble,c);
	    
	    c.gridx=0;
	    c.gridwidth=3;
	    c.gridy=1;
	    c.anchor=GridBagConstraints.WEST;
	    frame.getContentPane().add(lblQuestion,c);
	    c.gridy=2;
	    frame.getContentPane().add(rbAnswer0,c);
	    c.gridy=3;
	    frame.getContentPane().add(rbAnswer1,c);
	    c.gridy=4;
	    frame.getContentPane().add(rbAnswer2,c);
	    c.gridy=5;
	    c.anchor=GridBagConstraints.CENTER;
	    frame.getContentPane().add(btnOk,c);
	    
	    
	    addActionListeners();
	    updateGui();
	    
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(800, 600);
	    frame.setVisible(true);
	}
	
	public static void main(String[] args) throws Exception 
	{
		File saveFile=null;
		if (args.length==1 && args[0].equals("--help"))
		{
			System.out.println("usage: angeltrainer [--capture]");
			System.exit(0);
		}
		else if (args.length==1 && args[0].equals("--capture"))
		{
			new CaptureQuestionsMain().run(Utils.getNextFreeSaveFile());
			System.exit(0);
		}
		else if (args.length==1 )
		{
			saveFile=new File(args[0]);
		}
		else
		{
			List<File> captureFiles=Utils.getCapturedFiles();
			saveFile=captureFiles.size()>0?captureFiles.get(0):null;
		}
		new TrainerMain(saveFile).showGui();
	}

}
