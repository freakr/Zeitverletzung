package freakrware.zeitverletzung.core;
import java.text.ParseException;
import java.util.Date;
import java.util.prefs.Preferences;


public class Setup {

	public static final String VERTRAG = "Vertrag";
	public static final String FRÜHSTÜCK = "Frühstück";
	public static final String MITTAG = "Mittag";
	public static final String PAUSENZEIT_MITTAG = "Pausenzeit-Mittag";
	public static final String PAUSENZEIT_FRÜHSTÜCK = "Pausenzeit-Frühstück";
	
	public static final String GLEITZEIT = "Gleitzeit";
	public static final String WECHSELSCHICHT = "Wechselschicht";
	public static final String SCHICHTMODEL = "Schichtmodel";
	
	public static final String FRÜHSTÜCK_GLEITZEIT = "09:15:00"; 
	public static final String MITTAG_GLEITZEIT = "12:30:00";
	public static final String FRÜHSTÜCK_FRÜHSCHICHT = "08:30:00"; 
	public static final String MITTAG_FRÜHSCHICHT = "11:41:00";
	public static final String FRÜHSTÜCK_SPÄTSCHICHT = "17:51:00"; 
	public static final String MITTAG_SPÄTSCHICHT = "21:15:00";
	public static final String FRÜHSTÜCK_NACHTSCHICHT = "01:15:00"; 
	public static final String MITTAG_NACHTSCHICHT = "04:15:00";
	
	public static final String PAUSENZEIT_MITTAG_GLEITZEIT = "45";
	public static final String PAUSENZEIT_MITTAG_FRÜHSCHICHT = "21";
	public static final String PAUSENZEIT_MITTAG_SPÄTSCHICHT = "21";
	public static final String PAUSENZEIT_MITTAG_NACHTSCHICHT = "15";
	
	public static final String PAUSENZEIT_FRÜHSTÜCK_ALLE = "15";
	public static final String WINDOW_SIZE_X = "Window_Size_X";
	public static final String WINDOW_SIZE_Y = "Window_Size_Y";
	public static final String WINDOW_SIZE_X_DEFAULT = "800";
	public static final String WINDOW_SIZE_Y_DEFAULT = "120";
	
	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
	private ZeitService zeit;  
	
	public Setup(ZeitService zeitService) {
		this.zeit = zeitService;
	}
	public void set_setup(String id,String value) {
		prefs.put(id,value );
		
		}
	public String get_setup(String id) {
		return prefs.get(id,"");
		}
	
	public void defaults() {
		
		if(prefs.get(VERTRAG, "")==""){
			prefs.put(VERTRAG,GLEITZEIT);
		}
		if(prefs.get(FRÜHSTÜCK, "")==""){
			prefs.put(FRÜHSTÜCK,FRÜHSTÜCK_GLEITZEIT);
		}
		if(prefs.get(MITTAG, "")==""){
			prefs.put(MITTAG,MITTAG_GLEITZEIT);
		}
		if(prefs.get(PAUSENZEIT_MITTAG, "")==""){
			prefs.put(PAUSENZEIT_MITTAG,PAUSENZEIT_MITTAG_GLEITZEIT);
		}
		if(prefs.get(PAUSENZEIT_FRÜHSTÜCK, "")==""){
			prefs.put(PAUSENZEIT_FRÜHSTÜCK,PAUSENZEIT_FRÜHSTÜCK_ALLE);
		}
		if(prefs.get(WINDOW_SIZE_X, "")==""){
			prefs.put(WINDOW_SIZE_X,WINDOW_SIZE_X_DEFAULT);
		}
		if(prefs.get(WINDOW_SIZE_Y, "")==""){
			prefs.put(WINDOW_SIZE_Y,WINDOW_SIZE_Y_DEFAULT);
		}
		
	}
	public String check_schicht(String pause) {
		String back = "";
		String aktuellesdatum = zeit.df.format(zeit.ezeitminus[9]);
		Date frühbeforezeit = null;
		Date frühafterzeit = null;
		Date spätbeforezeit = null;
		Date spätafterzeit = null;
		Date nachtbeforezeit = null;
		Date nachtafterzeit = null;
		Date vergleich = null ;
		try {
			frühbeforezeit = zeit.dtf.parse(aktuellesdatum+" 00:00:00");
			frühafterzeit = zeit.dtf.parse(aktuellesdatum+" 12:30:00");
			spätbeforezeit = zeit.dtf.parse(aktuellesdatum+" 12:30:00");
			spätafterzeit = zeit.dtf.parse(aktuellesdatum+" 21:00:00");
			nachtbeforezeit = zeit.dtf.parse(aktuellesdatum+" 21:00:00");
			nachtafterzeit = zeit.dtf.parse(aktuellesdatum+" 23:59:00");
			vergleich = new Date(zeit.ezeitminus[9].getTime() - 1000*60*60*11);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch(pause){
		case FRÜHSTÜCK:
			if (vergleich.after(frühbeforezeit) && vergleich.before(frühafterzeit)){
				back = FRÜHSTÜCK_FRÜHSCHICHT;
			}
			if (vergleich.after(spätbeforezeit) && vergleich.before(spätafterzeit)){
				back = FRÜHSTÜCK_SPÄTSCHICHT;
				}
			if (vergleich.after(nachtbeforezeit) && vergleich.before(nachtafterzeit)){
				back = FRÜHSTÜCK_NACHTSCHICHT;
				}
			break;
		case MITTAG:
			if (vergleich.after(frühbeforezeit) && vergleich.before(frühafterzeit)){
				back = MITTAG_FRÜHSCHICHT;
			}
			if (vergleich.after(spätbeforezeit) && vergleich.before(spätafterzeit)){
				back = MITTAG_SPÄTSCHICHT;
				}
			if (vergleich.after(nachtbeforezeit) && vergleich.before(nachtafterzeit)){
				back = MITTAG_NACHTSCHICHT;
				}
			break;
		default:
			break;
		}
		
		
		return back;
	}
	public String check_schicht_pausenzeit(String pause) {
		String back = "";
		String aktuellesdatum = zeit.df.format(zeit.ezeitminus[9]);
		Date frühbeforezeit = null;
		Date frühafterzeit = null;
		Date spätbeforezeit = null;
		Date spätafterzeit = null;
		Date nachtbeforezeit = null;
		Date nachtafterzeit = null;
		Date vergleich = null ;
		try {
			frühbeforezeit = zeit.dtf.parse(aktuellesdatum+" 00:00:00");
			frühafterzeit = zeit.dtf.parse(aktuellesdatum+" 12:30:00");
			spätbeforezeit = zeit.dtf.parse(aktuellesdatum+" 12:30:00");
			spätafterzeit = zeit.dtf.parse(aktuellesdatum+" 21:00:00");
			nachtbeforezeit = zeit.dtf.parse(aktuellesdatum+" 21:00:00");
			nachtafterzeit = zeit.dtf.parse(aktuellesdatum+" 23:59:00");
			vergleich = new Date(zeit.ezeitminus[9].getTime() - 1000*60*60*11);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch(pause){
		case FRÜHSTÜCK:
			if (vergleich.after(frühbeforezeit) && vergleich.before(frühafterzeit)){
				back = PAUSENZEIT_FRÜHSTÜCK_ALLE;
			}
			if (vergleich.after(spätbeforezeit) && vergleich.before(spätafterzeit)){
				back = PAUSENZEIT_FRÜHSTÜCK_ALLE;
				}
			if (vergleich.after(nachtbeforezeit) && vergleich.before(nachtafterzeit)){
				back = PAUSENZEIT_FRÜHSTÜCK_ALLE;
				}
			break;
		case MITTAG:
			if (vergleich.after(frühbeforezeit) && vergleich.before(frühafterzeit)){
				back = PAUSENZEIT_MITTAG_FRÜHSCHICHT;
			}
			if (vergleich.after(spätbeforezeit) && vergleich.before(spätafterzeit)){
				back = PAUSENZEIT_MITTAG_SPÄTSCHICHT;
				}
			if (vergleich.after(nachtbeforezeit) && vergleich.before(nachtafterzeit)){
				back = PAUSENZEIT_MITTAG_NACHTSCHICHT;
				}
			break;
		default:
			break;
		}
		
		
		return back;
	}
}
