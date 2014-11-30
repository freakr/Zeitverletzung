package freakrware.zeitverletzung.core;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Date;

public class SysTray {

	TrayIcon trayIcon = null;
	private ZeitService zeit;
	int counter;
	MouseListener mClick;
	long timestamp = 0L;
	boolean threadstarted = false;
	final static Image IMAGE_START = Toolkit.getDefaultToolkit().getImage(Zeit.class.getResource("/S.png"));
	
	
	public SysTray(ZeitService zeitService) {
		this.zeit = zeitService;
	}
	public void start() {
	
	if (SystemTray.isSupported()) {
        // get the SystemTray instance
        SystemTray tray = SystemTray.getSystemTray();
        // load an image
        
		//Image image = Toolkit.getDefaultToolkit().getImage(Zeit.class.getResource("/S.png"));
		//System.out.println(START_IMAGE);
        // create a action listener to listen for default action executed on the tray icon
        ActionListener beenden = new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                // execute default action of the application
                // ...
        		System.exit(0);
        		
            }

        };
        ActionListener einstellungen = new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
        		
                // execute default action of the application
                // ...
        		zeit.showsetup();
        		
            }

        };
        PopupMenu popup = new PopupMenu();
        MenuItem setupItem = new MenuItem();
        setupItem.addActionListener(einstellungen);
        setupItem.setLabel("Einstellungen");
        MenuItem endItem = new MenuItem();
        endItem.addActionListener(beenden);
        endItem.setLabel("Zeitverletzung Beenden");
        
        popup.add(setupItem);
        popup.add(endItem);
        trayIcon = new TrayIcon(IMAGE_START, "Zeit-Verletzung", popup);
        mClick = new MouseListener()
        {
        	@Override
			public void mouseClicked(MouseEvent arg0) {
        		
        		counter = arg0.getClickCount();
        		System.out.println(counter);
        		if (timestamp == 0L) {
        			timestamp = System.currentTimeMillis();
        		}
        		System.out.println(timestamp);
        		System.out.println(threadstarted);
        		if(!threadstarted && counter == 1 )
        		{
        			threadstarted = true;
        			Thread dialoganzeige = new Thread(new Runnable(){
 			           @Override
 			           public void run() {
 			        	   while(!((timestamp+300) <= System.currentTimeMillis()))
 			        	   {
 			        		   System.out.println(System.currentTimeMillis()-timestamp );
 			        	   }
 			        	   if(counter == 1)
 			        	   {
 			        		   zeit.showdialog(String.valueOf(zeit.M3));
 			        		   counter = 1;
 			        	   }
 			        	   if(counter == 2)
 			        	   {
 			        		   zeit.showsetup();
 			        	   }
 			        	   timestamp = 0L;
 			        	   threadstarted = false;
 			            }
 			        });
 	        	dialoganzeige.start();
        		}
				
				
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        };
		trayIcon.addMouseListener(mClick );
        
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println(e);
        }
    } else {
    }
}
	public void update(int umkehrer){
		if (trayIcon != null) {
			
			BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);  
			Graphics2D g2d = image.createGraphics();
			Font font = g2d.getFont();  
			font = font.deriveFont(Font.BOLD,18.0f); // or any other size  
			g2d.setFont(font);
			g2d.setColor(Color.GREEN);
			if(umkehrer == 7 || umkehrer == 8){
				g2d.setColor(Color.YELLOW);
			}
			if(umkehrer == 9 || umkehrer == 10){
				g2d.setColor(Color.RED);
			}
			g2d.drawString(String.valueOf(umkehrer),3, 15);
			g2d.dispose(); 
			trayIcon.setImageAutoSize(true);
			trayIcon.setImage(image);
			trayIcon.setToolTip(tooltip_string(umkehrer));
		}
	}
	public String tooltip_string(int zeitvorbei){
		int ststart = zeitvorbei +4;
		if(ststart>10){
			ststart = 10;
		}
		String tooltip = pad(String.valueOf(ststart),2)+" Std in - : "+diff_time(zeit.tf.format(zeit.ezeitminus[ststart-1]));//+Zeit.tf.format(Zeit.ezeitminus[zeitvorbei]);
		
		for(int x = 1;x < 4 && ststart-x>zeitvorbei;x++){
			tooltip = tooltip + "\n"+pad(String.valueOf(ststart-x),2)+" Std in - : "+diff_time(zeit.tf.format(zeit.ezeitminus[ststart-1-x]));
		}
		
		
		return tooltip;
	
	}
	private String diff_time(String zielzeit) {
		String back = null;
		String azeit = zeit.tf.format(new Date());
		int zstd = Integer.parseInt(zielzeit.substring(0 , 2));
		int zmin = Integer.parseInt(zielzeit.substring(3 , 5));
		int zsec = Integer.parseInt(zielzeit.substring(6 , 8));
		int astd = Integer.parseInt(azeit.substring(0 , 2));
		int amin = Integer.parseInt(azeit.substring(3 , 5));
		int asec = Integer.parseInt(azeit.substring(6 , 8));
		int bstd = zstd-astd;
		int bmin = 0;
		int bsec = 0;
		if(amin<zmin){
			bmin = zmin-amin;
			}
		else{
			
			bmin = zmin+(60-amin);
			bstd--;
		}
		if(asec<zsec){
			bsec = zsec-asec;
			}
		else{
			bsec = zsec+(60-asec);
			bmin--;
		}
		
		back = pad(String.valueOf(bstd),2)+":"+pad(String.valueOf(bmin),2)+":"+pad(String.valueOf(bsec),2);
		
		return back;
	}

	private static String pad(String s, int length)
	{
	    while (s.length() < length) s = "0"+s;
	    return s;
	}
}
	

