package br.gov.bom_destino.gateway.api.rest;

import java.io.IOException;
import java.io.Serializable;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import br.gov.bom_destino.gateway.api.util.AutenticacaoUtil;

@Path("dados-geograficos-satelite")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SateliteResource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ResteasyClient client = new ResteasyClientBuilder().build();
	
	@GET
	public Response get(@QueryParam("nome-cliente") String nomeAPI) throws IOException {
		
		String resposta = AutenticacaoUtil.autenticar(nomeAPI);
		
		if(resposta.contains("autenticado")) {
			client = new ResteasyClientBuilder().build();
			
			WebTarget target = client.target("http://localhost:8080/satelite-api").path("dados-geograficos");
			
			Response response = target.request().get();
			
			resposta = "";
			
			if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
				resposta = response.readEntity(String.class);
			}			
		}
		
		return Response.ok().entity(resposta).build();
	}
}