package br.gov.bom_destino.gateway.api.rest;

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

import br.gov.bom_destino.gateway.api.util.AutenticacaoUtil;
import br.gov.bom_destino.gateway.api.util.PropertiesUtil;

@Path("dados-geograficos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImgCriarResource implements Serializable {
	private static final String IMG_CRIAR_API = "img-criar-api";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ResteasyClient client = new ResteasyClientBuilder().build();
	
	@POST
	public Response get(String nomeApi) {
		client = new ResteasyClientBuilder().build();
		
		WebTarget target;
		
		Response response;
		
		StringBuilder parametros = new StringBuilder();
		
		parametros.append(nomeApi);
		parametros.append("|");
		parametros.append(IMG_CRIAR_API);
		
		try {
			String resposta = AutenticacaoUtil.autenticar(nomeApi);
			
			if(resposta.contains("autenticado")) {
				target = client.target(PropertiesUtil.obterURI(IMG_CRIAR_API)).path("dados-geograficos");
				
				response = target.request().post(Entity.entity(parametros.toString(), MediaType.APPLICATION_JSON));
				
				if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
					resposta = response.readEntity(String.class);
				}else {
					StringBuilder mensagemErro = new StringBuilder();
					
					mensagemErro.append("Erro ao atualizar dados geográficos");
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