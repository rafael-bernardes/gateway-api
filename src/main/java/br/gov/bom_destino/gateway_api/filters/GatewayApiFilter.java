package br.gov.bom_destino.gateway_api.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

public class GatewayApiFilter implements Filter{

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		 String nomeCliente = arg0.getParameter("nome-cliente");
		 
		 if(nomeCliente != null && nomeCliente.length() > 0) {
			 arg2.doFilter(arg0, arg1);
		 }else {
			 HttpServletResponse resp = (HttpServletResponse)arg1;
			 
			 resp.sendError(Status.UNAUTHORIZED.getStatusCode());
		 }
		
	}

}
	