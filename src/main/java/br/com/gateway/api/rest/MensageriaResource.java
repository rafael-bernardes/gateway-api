package br.com.gateway.api.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import br.com.gateway.api.util.PropertiesUtil;

@Path("mensageria")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MensageriaResource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ResteasyClient client = new ResteasyClientBuilder().build();
	
	@GET
	public Response get() throws IllegalArgumentException, NullPointerException, IOException {
		WebTarget target = client.target(PropertiesUtil.obterURI("mensageria"));
		
		Response response = target.request().get();
		
		String resposta = "";
		
		if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			resposta = response.readEntity(String.class);
			
			System.out.println("mensageria-api.get: " + resposta);
		}
		
		return Response.ok().entity(resposta).build();
	}
	
	@POST
	public Response post(String mensagem) throws IllegalArgumentException, NullPointerException, IOException {
		WebTarget target = client.target(PropertiesUtil.obterURI("mensageria-api")).path("message").path("dados-geograficos-atualizar").queryParam("type", "queue");
		
		Response response = target.request().header("Authorization", obterHeaderAutorizacao()).buildPost(Entity.entity(mensagem, MediaType.APPLICATION_JSON)).invoke();
		
		String resposta = "";
		
		if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			resposta = response.readEntity(String.class);
			
			System.out.println("mensageria-api.post: " + resposta);
		}else {
			System.out.println("Resposta do ActiveMQ na cria��o de mensagem: " + response.getStatus());
		}
		
		return Response.ok().entity(resposta).build();
	}

	private String obterHeaderAutorizacao() {
		return "Basic " + codificarUsuarioSenha();
	}

	private String codificarUsuarioSenha() {
		return Base64.getEncoder().encodeToString(("admin:admin").getBytes());
	}
}