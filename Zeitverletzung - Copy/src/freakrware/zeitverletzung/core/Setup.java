package freakrware.zeitverletzung.core;
import java.text.ParseException;
import java.util.Date;
import java.util.prefs.Preferences;


public class Setup {

	public static final String VERTRAG = "Vertrag";
	public static final String FR�HST�CK = "Fr�hst�ck";
	public static final String MITTAG = "Mittag";
	public static final String PAUSENZEIT_MITTAG = "Pausenzeit-Mittag";
	public static final String PAUSENZEIT_FR�HST�CK = "Pausenzeit-Fr�hst�ck";
	
	public static final String GLEITZEIT = "Gleitzeit";
	public static final String WECHSELSCHICHT = "Wechselschicht";
	public static final String SCHICHTMODEL = "Schichtmodel";
	
	public static final String FR�HST�CK_GLEITZEIT = "09:15:00"; 
	public static final String MITTAG_GLEITZEIT = "12:30:00";
	public static final String FR�HST�CK_FR�HSCHICHT = "08:30:00"; 
	public static final String MITTAG_FR�HSCHICHT = "11:41:00";
	public static final String FR�HST�CK_SP�TSCHICHT = "17:51:00"; 
	public static final String MITTAG_SP�TSCHICHT = "21:15:00";
	public static final String FR�HST�CK_NACHTSCHICHT = "01:15:00"; 
	public static final String MITTAG_NACHTSCHICHT = "04:15:00";
	
	public static final String PAUSENZEIT_MITTAG_GLEITZEIT = "45";
	public static final String PAUSENZEIT_MITTAG_FR�HSCHICHT = "21";
	public static final String PAUSENZEIT_MITTAG_SP�TSCHICHT = "21";
	public static final String PAUSENZEIT_MITTAG_NACHTSCHICHT = "15";
	
	public static final String PAUSENZEIT_FR�HST�CK_ALLE = "15";
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
		if(prefs.get(FR�HST�CK, "")==""){
			prefs.put(FR�HST�CK,FR�HST�CK_GLEITZEIT);
		}
		if(prefs.get(MITTAG, "")==""){
			prefs.put(MITTAG,MITTAG_GLEITZEIT);
		}
		if(prefs.get(PAUSENZEIT_MITTAG, "")==""){
			prefs.put(PAUSENZEIT_MITTAG,PAUSENZEIT_MITTAG_GLEITZEIT);
		}
		if(prefs.get(PAUSENZEIT_FR�HST�CK, "")==""){
			prefs.put(PAUSENZEIT_FR�HST�CK,PAUSENZEIT_FR�HST�CK_ALLE);
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
		Date fr�hbeforezeit = null;
		Date fr�hafterzeit = null;
		Date sp�tbeforezeit = null;
		Date sp�tafterzeit = null;
		Date nachtbeforezeit = null;
		Date nachtafterzeit = null;
		Date vergleich = null ;
		try {
			fr�hbeforezeit = zeit.dtf.parse(aktuellesdatum+" 00:00:00");
			fr�hafterzeit = zeit.dtf.parse(aktuellesdatum+" 12:30:00");
			sp�tbeforezeit = zeit.dtf.parse(aktuellesdatum+" 12:30:00");
			sp�tafterzeit = zeit.dtf.parse(aktuellesdatum+" 21:00:00");
			nachtbeforezeit = zeit.dtf.parse(aktuellesdatum+" 21:00:00");
			nachtafterzeit = zeit.dtf.parse(aktuellesdatum+" 23:59:00");
			vergleich = new Date(zeit.ezeitminus[9].getTime() - 1000*60*60*11);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch(pause){
		case FR�HST�CK:
			if (vergleich.after(fr�hbeforezeit) && vergleich.before(fr�hafterzeit)){
				back = FR�HST�CK_FR�HSCHICHT;
			}
			if (vergleich.after(sp�tbeforezeit) && vergleich.before(sp�tafterzeit)){
				back = FR�HST�CK_SP�TSCHICHT;
				}
			if (vergleich.after(nachtbeforezeit) && vergleich.before(nachtafterzeit)){
				back = FR�HST�CK_NACHTSCHICHT;
				}
			break;
		case MITTAG:
			if (vergleich.after(fr�hbeforezeit) && vergleich.before(fr�hafterzeit)){
				back = MITTAG_FR�HSCHICHT;
			}
			if (vergleich.after(sp�tbeforezeit) && vergleich.before(sp�tafterzeit)){
				back = MITTAG_SP�TSCHICHT;
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
		Date fr�hbeforezeit = null;
		Date fr�hafterzeit = null;
		Date sp�tbeforezeit = null;
		Date sp�tafterzeit = null;
		Date nachtbeforezeit = null;
		Date nachtafterzeit = null;
		Date vergleich = null ;
		try {
			fr�hbeforezeit = zeit.dtf.parse(aktuellesdatum+" 00:00:00");
			fr�hafterzeit = zeit.dtf.parse(aktuellesdatum+" 12:30:00");
			sp�tbeforezeit = zeit.dtf.parse(aktuellesdatum+" 12:30:00");
			sp�tafterzeit = zeit.dtf.parse(aktuellesdatum+" 21:00:00");
			nachtbeforezeit = zeit.dtf.parse(aktuellesdatum+" 21:00:00");
			nachtafterzeit = zeit.dtf.parse(aktuellesdatum+" 23:59:00");
			vergleich = new Date(zeit.ezeitminus[9].getTime() - 1000*60*60*11);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		switch(pause){
		case FR�HST�CK:
			if (vergleich.after(fr�hbeforezeit) && vergleich.before(fr�hafterzeit)){
				back = PAUSENZEIT_FR�HST�CK_ALLE;
			}
			if (vergleich.after(sp�tbeforezeit) && vergleich.before(sp�tafterzeit)){
				back = PAUSENZEIT_FR�HST�CK_ALLE;
				}
			if (vergleich.after(nachtbeforezeit) && vergleich.before(nachtafterzeit)){
				back = PAUSENZEIT_FR�HST�CK_ALLE;
				}
			break;
		case MITTAG:
			if (vergleich.after(fr�hbeforezeit) && vergleich.before(fr�hafterzeit)){
				back = PAUSENZEIT_MITTAG_FR�HSCHICHT;
			}
			if (vergleich.after(sp�tbeforezeit) && vergleich.before(sp�tafterzeit)){
				back = PAUSENZEIT_MITTAG_SP�TSCHICHT;
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
