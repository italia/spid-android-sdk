package ing.vitiello.spid.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ing.vitiello.config.Config;
import ing.vitiello.sp.AuthSP;
import java.net.*;

/**
 * Servlet implementation class ConsumerServices
 */
@WebServlet("/consumerservices")
public class ConsumerServices extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public ConsumerServices() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("null")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Identificare dalla request l'identity provider
		
		String idp=request.getParameter("id");
		
		if (idp==null)
		{
			//In base all'identity provider creo l'AuthRequest
			
			AuthSP auth= new AuthSP();
			
			//Generate id
			
			auth.setId(generateID(idp));
			auth.setVersion(Config.VERSION);
			auth.setCallback(new URL(Config.COSUMERRESPONSE));
			switch (idp)
			{
				case "TIM":
					auth.setDestination(new URL(Config.TIM_DESTINATION));
					break;
				case "ARUBA":
					auth.setDestination(new URL(Config.ARUBA_DESTINATION));
					break;
				case "SIELTE":
					auth.setDestination(new URL(Config.SIELTE_DESTINATION));
					break;
				case "INFOCERT":
					auth.setDestination(new URL(Config.INFOCERT_DESTINATION));
					break;
				case "POSTE":
					auth.setDestination(new URL(Config.POSTE_DESTINATION));
					break;		
				default:
					auth.setDestination(null);
					break;
			}
			
			//Richiamo l'Identity Provider
			
			if (auth.getDestination()==null)
			{
				//Messaggio di errore in HTML (identity provider errato) verso l'UA
			}
			else
			{
				//CHIAMO l'IP
				if (auth.go())
				{
					//log
				}
				else
				{
					//Messaggio di errore in HTML (errore in fase di comunicazione con l'identity provider) verso l'UA
				}
			}
		}
		else
		{
			//Messaggio di errore in HTML (errore in fase di identificazione l'identity provider) verso l'UA
		}
		
		//TODO: Da eliminare
		
		response.sendRedirect("ipTest.jsp");
		
	}

	private String generateID(String idp) {
		// TODO Auto-generated method stub
		return Config.PREFIX_SP + "_" + idp + "_" + String.valueOf(System.currentTimeMillis());
		 
	}

}
