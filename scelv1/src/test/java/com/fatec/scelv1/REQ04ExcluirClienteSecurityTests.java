package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import com.fatec.scelv1.model.Cliente;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration

class REQ04ExcluirClienteSecurityTests {
	@Autowired
	private TestRestTemplate testRestTemplate;
	//@WithMockUser(username = "jose", password = "123")
	@Test
	public void quando_senha_invalida_retorna_nao_autorizado() {
		ResponseEntity<Cliente> resposta = testRestTemplate.withBasicAuth("jose", "1234").exchange("/api/clientes/v1/delete/{id}", HttpMethod.DELETE, null,
				Cliente.class, 9);

		assertEquals(HttpStatus.UNAUTHORIZED, resposta.getStatusCode());
	}

}
