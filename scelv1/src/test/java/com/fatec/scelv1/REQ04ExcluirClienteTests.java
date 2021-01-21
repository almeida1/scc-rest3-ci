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

import com.fatec.scelv1.model.Cliente;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ04ExcluirClienteTests {

	@Autowired
	private TestRestTemplate testRestTemplate;
	@Test
	void quando_id_existe_retorna_deleteok() {
		ResponseEntity<Cliente> resposta = testRestTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/delete/{id}", HttpMethod.DELETE, null,
				Cliente.class, 9);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	@Test
	void quando_id_nao_existe_retorna_not_found() {
		ResponseEntity<Cliente> resposta = testRestTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/delete/{id}", HttpMethod.DELETE, null,
				Cliente.class, 99);

		assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
	}
	
}
