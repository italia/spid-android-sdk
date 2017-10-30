package ing.vitiello.sp;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import ing.vitiello.config.Config;

import java.net.* ;
import java.text.SimpleDateFormat;

public class AuthSP {
	
	private String id;
	private String version;
	private Calendar timestamp;
	private URL destination;
	private URL callback;
	
	private boolean save ()
	{
		boolean ret=false;
		try {
		}
		catch (Exception ex)
		{}
		finally
		{}
		
		return ret;
		
	}
	
	public boolean go ()
	{
		boolean ret=false;
		try {
			
			
			StringBuffer msgSpid = new StringBuffer();
			msgSpid.append("<samlp:AuthnRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ");
			msgSpid.append(" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ");
			msgSpid.append(" ID=\""+ this.getId() + "\"");
			msgSpid.append(" Version=\"" + this.getVersion() + "\"");
			msgSpid.append(" IssueInstant=\"" + getIssueInstant() + "\"");
			msgSpid.append(" Destination=\"" + this.getDestination() + "\"");
			msgSpid.append(" AssertionConsumerServiceURL=\"" + this.getCallback() + "\"");
			msgSpid.append(" ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\"");
			msgSpid.append(" AttributeConsumingServiceIndex=\"1\"");
			msgSpid.append(" >");
			msgSpid.append(" <saml:Issuer NameQualifier=\"" + Config.DOMAIN_SP );
			msgSpid.append(" Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity \"");
			msgSpid.append(" >" + Config.ENTITY_ID + "</saml:Issuer>");
			msgSpid.append(" <samlp:NameIDPolicy Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\""); 
			msgSpid.append(" AllowCreate=\"true\"");
			msgSpid.append(" />");
			msgSpid.append(" <samlp:RequestedAuthnContext>");
			msgSpid.append(" <saml:AuthnContextClassRef>https://www.spid.gov.it/SpidL1</saml:AuthnContextClassRef>");
			msgSpid.append(" </samlp:RequestedAuthnContext>");
			msgSpid.append(" </samlp:AuthnRequest>");
			
			
			//TODO: incapsulare la chiamata a OneLogin per comunicare con l'IP il msgSpid.toString();
			
			System.out.println(msgSpid.toString());

		}
		catch (Exception ex)
		{}
		finally
		{
			save ();	//Salvo su una strittura chiave-valore (id-msgSpid) il messaggio che sto per invocare	
		}
		
		return ret;
		
	}
	
	private String getIssueInstant() {
		
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		SimpleDateFormat simpleDateFormat = 
		       new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ", Locale.ITALY);
		simpleDateFormat.setTimeZone(timeZone);

		System.out.println("Time zone: " + timeZone.getID());
		System.out.println("default time zone: " + TimeZone.getDefault().getID());
		System.out.println();

		System.out.println("UTC:     " + simpleDateFormat.format(calendar.getTime()));
		System.out.println("Default: " + calendar.getTime());
		
		return simpleDateFormat.format(calendar.getTime());
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Calendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	public URL getDestination() {
		return destination;
	}
	public void setDestination(URL destination) {
		this.destination = destination;
	}
	public URL getCallback() {
		return callback;
	}
	public void setCallback(URL callback) {
		this.callback = callback;
	}
	
	
	
	
	

}
