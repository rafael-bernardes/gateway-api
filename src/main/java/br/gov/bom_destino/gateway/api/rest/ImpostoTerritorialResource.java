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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import br.gov.bom_destino.gateway.api.util.PropertiesUtil;

@Path("espelhos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImpostoTerritorialResource implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@GET
	public Response getImpostoTerritorial(@QueryParam("documento") String documento, @QueryParam("tipo-pessoa") Integer tipoPessoa) throws IllegalArgumentException, NullPointerException, IOException {
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		WebTarget target = client.target(PropertiesUtil.obterURI("stur-imposto-territorial-api")).path("espelhos");
		
		target = target.queryParam("documento", documento);
		target = target.queryParam("tipo-pessoa", tipoPessoa);
		
		Response response = target.request().get();
		
		String resposta = "";
		
		if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			resposta = response.readEntity(String.class);
			
			return Response.ok(resposta).build();
		}else {
			return Response.status(Status.fromStatusCode(response.getStatus())).build();
		}
		
		
	}

}
