package br.gov.bom_destino.gateway_api.util;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import br.gov.bom_destino.gateway_api.entities.Credencial;

public class AutenticacaoUtil {
	
	public static String autenticar(String nomeCliente) throws IOException {
		Credencial credencial = new Credencial();
		
		credencial.setNomeCliente(nomeCliente);
		credencial.setToken("kdjkdfjsdfhskhf87678686");
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		WebTarget target;
		Response response;
		
		target = client.target(PropertiesUtil.obterURI("autenticacao-api")).path("autenticacao");
		
		response = target.request().post(Entity.entity(credencial, MediaType.APPLICATION_JSON));
		
		String resposta = "";
		
		if(Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			resposta = response.readEntity(String.class);
			System.out.println(resposta);
		}else {
			StringBuilder mensagemErro = new StringBuilder();
			
			mensagemErro.append("Erro ao autenticar API ");
			mensagemErro.append(nomeCliente);
			mensagemErro.append(". Resposta do serviço: ");
			mensagemErro.append(response.getStatus());
			
			System.out.println(mensagemErro.toString());
		}
		return resposta;
	}
}