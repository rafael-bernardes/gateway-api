package br.com.gateway.api.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import br.com.gateway.api.util.AutenticacaoUtil;
import br.com.gateway.api.util.PropertiesUtil;

@Path("mensageria-ibge")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MensageriaIbgeResource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ResteasyClient client = new ResteasyClientBuilder().build();
	
	@GET
	public Response get(@QueryParam("nome-api") String nomeAPI) throws IllegalArgumentException, NullPointerException, IOException {
		String resposta = AutenticacaoUtil.autenticar(nomeAPI);
		
		if(resposta.contains("autenticado")) {
			WebTarget target = client.target(PropertiesUtil.obterURI("mensageria-api")).path("message").path("dados-geograficos-ibge-atualizar").queryParam("type", "queue");
			
			Response response = target.request().header("Authorization", obterHeaderAutorizacao()).get();
			
			if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
				resposta = response.readEntity(String.class);
			}else {
				resposta = "Falha ao obter mensagem de atualiza��o de dados provenientes do IBGE";
			}
		}
		
		return Response.ok().entity(resposta).build();
	}
	
	@POST
	public Response post(String mensagem, @QueryParam("nome-api") String nomeAPI) throws IllegalArgumentException, NullPointerException, IOException {
		String resposta = AutenticacaoUtil.autenticar(nomeAPI);
		
		if(resposta.contains("autenticado")) {
			WebTarget target = client.target(PropertiesUtil.obterURI("mensageria-api")).path("message").path("dados-geograficos-ibge-atualizar").queryParam("type", "queue");
			
			Response response = target.request().header("Authorization", obterHeaderAutorizacao()).buildPost(Entity.entity(mensagem, MediaType.APPLICATION_JSON)).invoke();
			
			resposta = "";
			
			if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
				resposta = response.readEntity(String.class);
				
				System.out.println("mensageria-api.post: " + resposta);
			}else {
				System.out.println("Resposta do ActiveMQ na cria��o de mensagem: " + response.getStatus());
			}			
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