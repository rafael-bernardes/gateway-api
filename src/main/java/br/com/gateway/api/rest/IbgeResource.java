package br.com.gateway.api.rest;

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

import br.com.gateway.api.util.PropertiesUtil;

@Path("dados-geograficos-ibge")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IbgeResource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ResteasyClient client = new ResteasyClientBuilder().build();
	
	@GET
	public Response get(@QueryParam("nome-cidade") String nomeCidade, @QueryParam("nome-api") String nomeAPI) {
		client = new ResteasyClientBuilder().build();
		
		WebTarget target;
		
		Response response;
		
		try {
			target = client.target(PropertiesUtil.obterURI("autenticacao-api")).path("autenticacao");
			target = target.queryParam("nome-api", nomeAPI);
			
			response = target.request().get();
			
			String resposta = "";
			
			if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
				resposta = response.readEntity(String.class);
			}else {
				StringBuilder mensagemErro = new StringBuilder();
				
				mensagemErro.append("Erro ao autenticar API ");
				mensagemErro.append(nomeAPI);
				mensagemErro.append(". Resposta do serviço: ");
				mensagemErro.append(response.getStatus());
				
				System.out.println(mensagemErro.toString());
			}
			
			if(resposta.contains("autenticado")) {
				target = client.target(PropertiesUtil.obterURI("ibge-api"));
				target.queryParam("nome-cidade", nomeCidade);
				
				response = target.request().get();
				
				if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
					resposta = response.readEntity(String.class);
				}else {
					StringBuilder mensagemErro = new StringBuilder();
					
					mensagemErro.append("Erro ao obter dados do IBGE");
					mensagemErro.append(". Resposta do serviço: ");
					mensagemErro.append(response.getStatus());
					
					System.out.println(mensagemErro.toString());
				}
			}
			
			return Response.ok().entity(resposta).build();
		} catch (IllegalArgumentException e) {
			imprimirErroConsole(e);
			return Response.status(Status.BAD_REQUEST).build();
		} catch (NullPointerException e) {
			imprimirErroConsole(e);
			return Response.serverError().build();
		} catch (IOException e) {
			imprimirErroConsole(e);
			return Response.serverError().build();
		}
	}

	private void imprimirErroConsole(Exception e) {
		System.out.println(e.getMessage());
	}
}