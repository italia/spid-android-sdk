import java.util.HashSet;
import java.util.HashMap;

import openSAML.code.*;

import org.apache.commons.codec.binary.Base64;

public class SPrequest {

	public static void main(String[] args) {
		SPAuthnRequest samlAssertionObject = new SPAuthnRequest();
		
		try {
		String samlAssertion = samlAssertionObject.samlWriterPoste();
		System.out.print(samlAssertion);
		String samlAuthRequest = new String(Base64.encodeBase64String(samlAssertion.getBytes()));
				
		// response.sendRedirect("https://colo-pm2.adx.isi.edu/adfs/ls/?SAMLRequest="+ new String(Base64.encodeBase64String(samlAssertion.getBytes())));
		/*
		String responseMessage = new String();
		AuthResponse samlResponse = new AuthResponse();
		//HashMap<String, HashSet<String>> claimsContainer = samlResponse.processResponse(request.getParameter("SAMLResponse"));
		
		HashMap<String, HashSet<String>> claimsContainer = samlResponse.processResponse(responseMessage);
		
		for (String claim : claimsContainer.keySet()) {
			//System.out.print(" ");
			System.out.print(claim.toString() +" : ");
			HashSet<String> claimValues = claimsContainer.get(claim);
			for (String claimValue : claimValues) {
				System.out.print(claimValue + " ");
			}
			System.out.println();
		}*/
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
