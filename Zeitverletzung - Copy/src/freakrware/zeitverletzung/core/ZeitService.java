package freakrware.zeitverletzung.core;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import freakrware.privat.standards.java.core.Standards_interface_java;

public class ZeitService implements Standards_interface_java{
	
		public int M3 = 0;
		public Date ezeit;
		public Date azeit;
		public Date vzeit;
		public Date mzeit;
		public DateFormat df = new SimpleDateFormat("dd.MM.yyyy",Locale.GERMAN);
		public DateFormat tf = new SimpleDateFormat("HH:mm:ss",Locale.GERMAN);
		public DateFormat dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",Locale.GERMAN);
		public String vertrag;
		public String vesperzeit;
		public String mittagszeit;
		public String pause_früh;
		public String pause_mittag;
		public Date[] ezeitminus= new Date[10];
		
		Setup setup = new Setup(this);
		SysTray st = new SysTray(this);
		public String is = setup.get_setup(Setup.VERTRAG);
		
	public void Zeitstart(String[] args) {
		
		final String endedatum= args[0];
		final String endezeit= args[1];	
		
		st.start();
		
		setup.defaults();
		

		try {
			
			ezeit = dtf.parse(endedatum+" "+endezeit);
			
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		System.out.println(ezeit);//Mon Jul 05 00:00:00 IST 2010
				
		set_times();
		
		final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	    service.scheduleWithFixedDelay(new Runnable()
	      {
	        @Override
	        public void run()
	        {
	        	final String text = regelberechnung();
	        	if(text != null){
	        		Thread dialoganzeige = new Thread(new Runnable(){
			            @Override
			            public void run() {
			                
			                showdialog("--** "+text+" Stunden Arbeitszeit erreicht! **--");
			            }
			        });
	        	dialoganzeige.start();
	        	}	
	        }
	      }, 0, 1, TimeUnit.MILLISECONDS);
		

	}

	

	private void set_times() {
		
		String aktuellesdatum = df.format(new Date());
		vertrag = setup.get_setup(Setup.VERTRAG);	
		vesperzeit = setup.get_setup(Setup.FRÜHSTÜCK);
		mittagszeit = setup.get_setup(Setup.MITTAG);
		pause_früh = setup.get_setup(Setup.PAUSENZEIT_FRÜHSTÜCK);
		pause_mittag = setup.get_setup(Setup.PAUSENZEIT_MITTAG);
		
		try {
			vzeit = dtf.parse(aktuellesdatum+" "+vesperzeit);
			mzeit = dtf.parse(aktuellesdatum+" "+mittagszeit);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
				
		for(int x = 0;x<10;x++){
			ezeitminus[x]= new Date(ezeit.getTime()-1000*60*60*(9-x));
			
			if(ezeitminus[x].after(vzeit)){
				Date tzeit = new Date(ezeitminus[x].getTime()+(1000*60*(Integer.parseInt(pause_früh))));
				ezeitminus[x].setTime(tzeit.getTime());
			}
			if(ezeitminus[x].after(mzeit)){
				Date tzeit = new Date(ezeitminus[x].getTime()+(1000*60*(Integer.parseInt(pause_mittag))));
				ezeitminus[x].setTime(tzeit.getTime());
			}
		}
		
		//Date tzeit = new Date(ezeit.getTime()+(1000*60*(Integer.parseInt(pause_früh)+Integer.parseInt(pause_mittag))));
		//ezeit.setTime(tzeit.getTime());
	}



	public void showdialog(String text){
				
				DateFormat tf = new SimpleDateFormat("HH:mm:ss");
				Date stdreachedtime = new Date(new Date().getTime()-(new Date().getTime()-ezeitminus[M3-1].getTime()));
				String sizex = setup.get_setup(Setup.WINDOW_SIZE_X);
				String sizey = setup.get_setup(Setup.WINDOW_SIZE_Y);
				
				JFrame f = new JFrame("Restzeit");
	    		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    		f.setAlwaysOnTop(true);
	    		JDialog dialog = new JDialog(f, "JDialog", true);
	    	    dialog.setTitle("10 Stunden Regel");
	    	    dialog.setSize(Integer.parseInt(sizex),Integer.parseInt(sizey));
	    	    dialog.setLocation(400, 350);
	    	    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    	    JLabel label = new JLabel("Text");
	    	    label.setText(text + " : " + String.valueOf(tf.format(stdreachedtime)));
	    	    dialog.add(label,BorderLayout.CENTER);
	    	    label.setHorizontalAlignment(JLabel.CENTER);
	    	    label.setVerticalAlignment(JLabel.CENTER);
	    	     
	    	    int textsize = Integer.parseInt(sizex) / 27;
	    	    float xtextsize = (float) textsize;
				label.setFont(label.getFont().deriveFont(xtextsize));
	    	    if(text.contains("--** 7 Stunden Arbeitszeit erreicht! **--") || text.contains("--** 8 Stunden Arbeitszeit erreicht! **--")){
	    	    	dialog.getContentPane().setBackground(Color.yellow);
	    	    }
	    	    if(text.contains("--** 9 Stunden Arbeitszeit erreicht! **--") || text.contains("--** 10 Stunden Arbeitszeit erreicht! **--")){
	    	    	dialog.getContentPane().setBackground(Color.red);
	    	    }
	    	    dialog.requestFocus();
	    	    dialog.toFront();
	    	    dialog.setVisible(true);
	    	    dialog.dispose();
	    		f.dispose();
		
		
		
	}

	 public String regelberechnung() {
		
		
		String aktuellezeit = tf.format(new Date());
		String aktuellesdatum = df.format(new Date());
		
		try {
			azeit = dtf.parse(aktuellesdatum+" "+aktuellezeit);
					
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		
		
		if(M3 == 10){
			System.exit(0);
		}
		
		if(azeit.getTime() > ezeit.getTime() && M3 < 10){
			M3=10;
			st.update(M3);
			return "10";	
		}
		if(azeit.getTime() > ezeitminus[8].getTime() && M3 < 9){
			M3=9;
			st.update(M3);
			return "9";	
		}
		if(azeit.getTime() > ezeitminus[7].getTime() && M3 < 8){
			M3=8;
			st.update(M3);
			return "8";	
		}
		if(azeit.getTime() > ezeitminus[6].getTime() && M3 < 7){
			M3=7;
			st.update(M3);
			return "7";	
		}
		if(azeit.getTime() > ezeitminus[5].getTime() && M3 < 6){
			M3=6;
			st.update(M3);
			return "6";	
		}
		if(azeit.getTime() > ezeitminus[4].getTime() && M3 < 5){
			M3=5;
			st.update(M3);
			return "5";	
		}
		if(azeit.getTime() > ezeitminus[3].getTime() && M3 < 4){
			M3=4;
			st.update(M3);
			return "4";	
		}
		if(azeit.getTime() > ezeitminus[2].getTime() && M3 < 3){
			M3=3;
			st.update(M3);
			return "3";	
		}
		if(azeit.getTime() > ezeitminus[1].getTime() && M3 < 2){
			M3=2;
			st.update(M3);
			return "2";	
		}
		if(azeit.getTime() > ezeitminus[0].getTime() && M3 < 1){
			M3=1;
			st.update(M3);
			return "1";	
		}	
		st.update(M3);
		return null;
	}



	public void showsetup(){
				
				
				String vertrag = setup.get_setup(Setup.VERTRAG);
				final JFrame f = new JFrame("Einstellungen");
	    		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    		f.setAlwaysOnTop(true);
	    		JPanel panel = new JPanel();
	    		final JDialog dialog = new JDialog(f, "JDialog", true);
	    	    dialog.setTitle("Einstellungen");
	    	    dialog.setSize(280,220);
	    	    dialog.setLocation(400, 200);
	    	    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    	    final JLabel frühstückfrage = new JLabel("Frühstück :      ");
	            final JLabel frühstückwert = new JLabel(setup.get_setup(Setup.FRÜHSTÜCK)+" Uhr                              ");
	            final JLabel mittagfrage = new JLabel("Mittag   :           ");
	            final JLabel mittagwert = new JLabel(setup.get_setup(Setup.MITTAG)+" Uhr                             ");
	            final JLabel früh_pause_frage = new JLabel("Frühstückszeit :      ");
	            final JLabel früh_pause_wert = new JLabel(setup.get_setup(Setup.PAUSENZEIT_FRÜHSTÜCK)+" min                               ");
	            final JLabel mittag_pause_frage = new JLabel("Mittagszeit :              ");
	            final JLabel mittag_pause_wert = new JLabel(setup.get_setup(Setup.PAUSENZEIT_MITTAG)+" min                              ");
	            final JLabel anzeige = new JLabel("Anzeige :");
	            final JLabel ysize = new JLabel("Size Y");
	            final JLabel xsize = new JLabel("Size X");     
	            final TextField sizexvalue = new TextField(setup.get_setup(Setup.WINDOW_SIZE_X), 4);
	            final TextField sizeyvalue = new TextField(setup.get_setup(Setup.WINDOW_SIZE_Y), 4);
	            JLabel vertragfrage = new JLabel("Vertragsart :");
	            panel.add(vertragfrage);
	            String comboBoxListevertrag[] = {Setup.GLEITZEIT,Setup.WECHSELSCHICHT,Setup.SCHICHTMODEL};
	    	    JComboBox<String> vertragbox = new JComboBox<String>(comboBoxListevertrag);
	    	    vertragbox.setSelectedItem(vertrag);
	    	    vertragbox.addItemListener(new ItemListener() {
	    	    	

					@Override 
	    	        public void itemStateChanged(ItemEvent event) {
	    	    		if (event.getStateChange() == ItemEvent.SELECTED) {
	    	    	          Object item = event.getItem();
	    	    	          is = (String) item;
	    	    	          switch(is){
	    	    	          case Setup.GLEITZEIT:
	    	    	        	  frühstückwert.setText(Setup.FRÜHSTÜCK_GLEITZEIT+" Uhr                              ");
	    	    	        	  mittagwert.setText(Setup.MITTAG_GLEITZEIT+" Uhr                             ");
	    	    	        	  früh_pause_wert.setText(Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE+" min                               ");
	    	    	        	  mittag_pause_wert.setText(Setup.PAUSENZEIT_MITTAG_GLEITZEIT+" min                              ");
	    	    	        	  break;
	    	    	          case Setup.WECHSELSCHICHT:
	    	    	        	  frühstückwert.setText(setup.check_schicht(Setup.FRÜHSTÜCK)+" Uhr                              ");
	    	    	        	  mittagwert.setText(setup.check_schicht(Setup.MITTAG)+" Uhr                             ");
	    	    	        	  früh_pause_wert.setText(Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE+" min                               ");
	    	    	        	  mittag_pause_wert.setText(setup.check_schicht_pausenzeit(Setup.MITTAG)+" min                              ");
	    	    	        	  break;
	    	    	          case Setup.SCHICHTMODEL:
	    	    	        	  frühstückwert.setText(setup.check_schicht(Setup.FRÜHSTÜCK)+" Uhr                              ");
	    	    	        	  mittagwert.setText(setup.check_schicht(Setup.MITTAG)+" Uhr                             ");
	    	    	        	  früh_pause_wert.setText(Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE+" min                               ");
	    	    	        	  mittag_pause_wert.setText(setup.check_schicht_pausenzeit(Setup.MITTAG)+" min                              ");
	    	    	        	  break;
	    	    	          default:
	    	    	        	  break;
	    	    	          }
	    	        
	    	    		}
	    	    	}	
	    	    });
	    	    panel.add(vertragbox);
	    	    panel.add(frühstückfrage);
	    	    panel.add(frühstückwert);
	    	    panel.add(mittagfrage);
	    	    panel.add(mittagwert);
	    	    panel.add(früh_pause_frage);
	    	    panel.add(früh_pause_wert);
	    	    panel.add(mittag_pause_frage);
	    	    panel.add(mittag_pause_wert);
	    	    panel.add(anzeige);
	    	    panel.add(xsize);
	    	    panel.add(sizexvalue);
	    	    panel.add(ysize);
	    	    panel.add(sizeyvalue);
	    	    JButton okbutton = new JButton("OK");
	            okbutton.addActionListener(new ActionListener() {
	            	@Override 
	                public void actionPerformed(ActionEvent e)
	                {
	            		String sizex = sizexvalue.getText();
	    	        	String sizey = sizeyvalue.getText(); 
	    	        	try {
							Integer.parseInt(sizex);
							Integer.parseInt(sizey);
							setup.set_setup(Setup.WINDOW_SIZE_X, sizex);
							setup.set_setup(Setup.WINDOW_SIZE_Y, sizey);
							switch(is){
		  	    	          case Setup.GLEITZEIT:
		  	    	        	  setup.set_setup(Setup.VERTRAG, Setup.GLEITZEIT);
		  	    	        	  setup.set_setup(Setup.FRÜHSTÜCK, Setup.FRÜHSTÜCK_GLEITZEIT);
		  	    	        	  setup.set_setup(Setup.MITTAG, Setup.MITTAG_GLEITZEIT);
		  	    	        	  setup.set_setup(Setup.PAUSENZEIT_FRÜHSTÜCK, Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE);
		  	    	        	  setup.set_setup(Setup.PAUSENZEIT_MITTAG, Setup.PAUSENZEIT_MITTAG_GLEITZEIT);
		  	    	        	 break;
		  	    	          case Setup.WECHSELSCHICHT:
		  	    	        	  setup.set_setup(Setup.VERTRAG, Setup.WECHSELSCHICHT);
		  	    	        	  setup.set_setup(Setup.FRÜHSTÜCK, setup.check_schicht(Setup.FRÜHSTÜCK));
		  	    	        	  setup.set_setup(Setup.MITTAG, setup.check_schicht(Setup.MITTAG));
		  	    	        	  setup.set_setup(Setup.PAUSENZEIT_FRÜHSTÜCK, Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE);
		  	    	        	  setup.set_setup(Setup.PAUSENZEIT_MITTAG, setup.check_schicht_pausenzeit(Setup.MITTAG));
		  	    	        	  break;
		  	    	          case Setup.SCHICHTMODEL:
		  	    	        	  setup.set_setup(Setup.VERTRAG, Setup.SCHICHTMODEL);
		  	    	        	  setup.set_setup(Setup.FRÜHSTÜCK, setup.check_schicht(Setup.FRÜHSTÜCK));
		  	    	        	  setup.set_setup(Setup.MITTAG, setup.check_schicht(Setup.MITTAG));
		  	    	        	  setup.set_setup(Setup.PAUSENZEIT_FRÜHSTÜCK, Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE);
		  	    	        	  setup.set_setup(Setup.PAUSENZEIT_MITTAG, setup.check_schicht_pausenzeit(Setup.MITTAG));
		  	    	        	  break;
		  	    	          default:
		  	    	        	  break;
		  	    	          }
			            		dialog.dispose();
			    	    		f.dispose();
			    	    		M3 = 0;
			    	    		set_times();
							
						} 
	    	        	catch (NumberFormatException e1) 
						{
							jstandard.messagedialog(f, "Bitte die Größe in ganzen Zahlen eingeben!!");
						}
	    	        		
						
	            		
	            		
	                }
	            });      
	            panel.add(okbutton);
	            
	            JButton abbutton = new JButton("ESC");
	            abbutton.addActionListener(new ActionListener() {
	            	@Override 
	                public void actionPerformed(ActionEvent e)
	                {
	                    //Execute when button is pressed
	                    System.out.println("Abbruch");
	                    dialog.dispose();
	    	    		f.dispose();
	                }
	            });  
	            panel.add(abbutton);
	            
	            JButton overbutton = new JButton("Submit");
	            overbutton.addActionListener(new ActionListener() {
	            	@Override 
	                public void actionPerformed(ActionEvent e)
	                {
	            		String sizex = sizexvalue.getText();
	    	        	String sizey = sizeyvalue.getText(); 
	    	        	try {
							Integer.parseInt(sizex);
							Integer.parseInt(sizey);
							setup.set_setup(Setup.WINDOW_SIZE_X, sizex);
							setup.set_setup(Setup.WINDOW_SIZE_Y, sizey);
	            		switch(is){
	  	    	          case Setup.GLEITZEIT:
	  	    	        	  setup.set_setup(Setup.VERTRAG, Setup.GLEITZEIT);
	  	    	        	  setup.set_setup(Setup.FRÜHSTÜCK, Setup.FRÜHSTÜCK_GLEITZEIT);
	  	    	        	  setup.set_setup(Setup.MITTAG, Setup.MITTAG_GLEITZEIT);
	  	    	        	  setup.set_setup(Setup.PAUSENZEIT_FRÜHSTÜCK, Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE);
	  	    	        	  setup.set_setup(Setup.PAUSENZEIT_MITTAG, Setup.PAUSENZEIT_MITTAG_GLEITZEIT);
	  	    	        	  break;
	  	    	          case Setup.WECHSELSCHICHT:
	  	    	        	  setup.set_setup(Setup.VERTRAG, Setup.WECHSELSCHICHT);
	  	    	        	  setup.set_setup(Setup.FRÜHSTÜCK, setup.check_schicht(Setup.FRÜHSTÜCK));
	  	    	        	  setup.set_setup(Setup.MITTAG, setup.check_schicht(Setup.MITTAG));
	  	    	        	  setup.set_setup(Setup.PAUSENZEIT_FRÜHSTÜCK, Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE);
	  	    	        	  setup.set_setup(Setup.PAUSENZEIT_MITTAG, setup.check_schicht_pausenzeit(Setup.MITTAG));
	  	    	        	  break;
	  	    	          case Setup.SCHICHTMODEL:
	  	    	        	  setup.set_setup(Setup.VERTRAG, Setup.SCHICHTMODEL);
	  	    	        	  setup.set_setup(Setup.FRÜHSTÜCK, setup.check_schicht(Setup.FRÜHSTÜCK));
	  	    	        	  setup.set_setup(Setup.MITTAG, setup.check_schicht(Setup.MITTAG));
	  	    	        	  setup.set_setup(Setup.PAUSENZEIT_FRÜHSTÜCK, Setup.PAUSENZEIT_FRÜHSTÜCK_ALLE);
	  	    	        	  setup.set_setup(Setup.PAUSENZEIT_MITTAG, setup.check_schicht_pausenzeit(Setup.MITTAG));
	  	    	        	  break;
	  	    	          default:
	  	    	        	  break;
	  	    	          }
	            		  M3 = 0;
	            		  set_times();
	    	        	} catch (NumberFormatException e1) {
	    	        		{
								jstandard.messagedialog(f, "Bitte die Größe in ganzen Zahlen eingeben!!");
							}
						}
	                }
	            });  
	            panel.add(overbutton);
	            
	    	    dialog.add(panel);
	    	    dialog.requestFocus();
	    	    dialog.toFront();
	    	    dialog.setVisible(true);
	    	    dialog.dispose();
	    		f.dispose();
	 }

}
