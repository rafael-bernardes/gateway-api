package br.gov.bom_destino.gateway_api.rest;

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

import br.gov.bom_destino.gateway_api.util.PropertiesUtil;

@Path("mensageria-satelite")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MensageriaSateliteResource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ResteasyClient client = new ResteasyClientBuilder().build();
	
	@GET
	public Response get(@QueryParam("nome-cliente") String nomeAPI) throws IllegalArgumentException, NullPointerException, IOException {

		byte[] respostaServico = null;

		String resposta = "";

		WebTarget target = client.target(PropertiesUtil.obterURI("mensageria-api")).path("message").path("dados-geograficos-satelite-atualizar").queryParam("type", "queue");

		Response response = target.request().header("Authorization", obterHeaderAutorizacao()).get();

		if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			respostaServico = response.readEntity(byte[].class);
		}			

		return Response.ok().entity(respostaServico).build();
	}
	
	@POST
	public Response post(byte[] mensagem, @QueryParam("nome-cliente") String nomeAPI) throws IllegalArgumentException, NullPointerException, IOException {

		String resposta = "";

		WebTarget target = client.target(PropertiesUtil.obterURI("mensageria-api")).path("message").path("dados-geograficos-satelite-atualizar").queryParam("type", "queue");

		Response response = target.request().header("Authorization", obterHeaderAutorizacao()).buildPost(Entity.entity(mensagem, MediaType.APPLICATION_JSON)).invoke();

		resposta = "";

		if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			resposta = response.readEntity(String.class);

			System.out.println("mensageria-api.post: " + resposta);
		}else {
			System.out.println("Resposta do ActiveMQ na criação de mensagem: " + response.getStatus());
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