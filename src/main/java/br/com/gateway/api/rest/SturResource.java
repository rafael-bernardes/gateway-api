package br.com.gateway.api.rest;

import java.io.IOException;
import java.io.Serializable;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import br.com.gateway.api.util.AutenticacaoUtil;
import br.com.gateway.api.util.PropertiesUtil;

@Path("dados-geograficos-stur")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SturResource implements Serializable {
	
	private static final long serialVersionUID = 1L;

	ResteasyClient client = new ResteasyClientBuilder().build();
	
	@POST
	public Response get(String corpo) {
		client = new ResteasyClientBuilder().build();
		
		WebTarget target;
		
		Response response;
		
		try {
			String resposta = AutenticacaoUtil.autenticar("stur-web");
			
			if(resposta.contains("autenticado")) {
				boolean ibge = corpo.contains("IBGE");
				boolean satelite = corpo.contains("SATELITE");
				
				if(ibge) {
					target = client.target(PropertiesUtil.obterURI("stur-atualizacao-api")).path("dados-geograficos-ibge");
					response = target.request().post(Entity.entity("", MediaType.APPLICATION_JSON));
					
					if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
						resposta = response.readEntity(String.class);
					}else {
						StringBuilder mensagemErro = new StringBuilder();
						
						mensagemErro.append("Erro ao atualizar dados geográficos do IBGE no STUR");
						mensagemErro.append(". Resposta do serviço: ");
						mensagemErro.append(response.getStatus());
						
						System.out.println(mensagemErro.toString());
					}
				}
				
				if(satelite) {
					target = client.target(PropertiesUtil.obterURI("stur-atualizacao-api")).path("dados-geograficos-satelite");
					response = target.request().post(Entity.entity("", MediaType.APPLICATION_JSON));
					
					if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
						resposta = response.readEntity(String.class);
					}else {
						StringBuilder mensagemErro = new StringBuilder();
						
						mensagemErro.append("Erro ao atualizar dados geográficos de satélite no STUR");
						mensagemErro.append(". Resposta do serviço: ");
						mensagemErro.append(response.getStatus());
						
						System.out.println(mensagemErro.toString());
					}
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