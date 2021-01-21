package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fatec.scelv1.model.Cliente;
import com.fatec.scelv1.model.Endereco;
import com.fatec.scelv1.servico.ClienteServico;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ03AlterarClienteTests {
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private ClienteServico servico;
	private Cliente cliente;

	@Test
	public void ct01_quando_solicita_alteracao_com_dados_validos_resultado_ok() {
		// dado que - os dados são validos
		cliente = new Cliente("88888888888", "Carlos Jose9", "carlos_jose9@email", "03694000");
		long id = 9;
		cliente.setId(id);
		cliente.setEndereco("Avenida Águia de Haia");
		// quando solicita alteraçao
		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/update/{id}", HttpMethod.PUT, httpEntity,
				String.class, cliente.getId());
		// testRestTemplate.put("/clientes/update/{id}",cliente,cliente.getId());
		RestTemplate template = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		ResponseEntity<Endereco> response = template.getForEntity(url, Endereco.class, "03694000",
				new ParameterizedTypeReference<Endereco>() {});

		System.out.println(">>>>>>" + response.getBody().getLogradouro());
		// então
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		// Quando temos um objeto optional retornado de um método, pode-se verificar se
		// existe
		// um valor nele ou não com o método isPresent ():
		Cliente clienteAlterado = servico.consultaPorCpf("88888888888").getBody();
		// usa-se o get quando temos certeza de que o optional nao esta vazio
		assertTrue(cliente.equals(clienteAlterado));
		assertEquals("Cliente atualizado", resposta.getBody());
		// No estilo de programação funcional típico, pode-se executar
		// uma ação em um objeto que está presente.
		// No exemplo abaixo, usamos apenas duas linhas de código para substituir as
		// cinco que
		// funcionaram no primeiro exemplo: uma linha para envolver o objeto em um
		// objeto
		// opcional e a próxima para realizar a validação implícita, bem como executar o
		// código.

		// https://www.baeldung.com/java-optional
		// https://medium.com/@racc.costa/optional-no-java-8-e-no-java-9-7c52c4b797f1

	}

	@Test
	public void ct02_quando_solicita_alteracao_com_dados_invalidos_DeveRetornarMensagemDeErro() {
		// dado que - os dados são invalidos
		cliente = new Cliente("", "", "carlos_jose9@email", "043309");
		long id = 9;
		cliente.setId(id);
		// quando solicita alteraçao
		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
		ResponseEntity<List<String>> resposta = testRestTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/update/{id}", HttpMethod.PUT,
				httpEntity, new ParameterizedTypeReference<List<String>>() {
				}, cliente.getId());
		// então
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertTrue(resposta.getBody().contains("CPF deve ter 11 caracteres"));
		assertTrue(resposta.getBody().contains("Nome deve ser preenchido"));
	}

	@Test
	public void ct03_cep_invalido() {
		ResponseEntity<Endereco> response = servico.obtemEndereco("043309");
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	@Test
	public void ct04_cep_invalido() {
		ResponseEntity<Endereco> response = servico.obtemEndereco("99999999");
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	@Test
	public void ct05_cep_invalido() {
		// dado que - o cep é inválido
				cliente = new Cliente("88888888888", "Carlos Jose9", "carlos_jose9@email", "043309");
				long id = 9;
				cliente.setId(id);
				cliente.setEndereco("Avenida Águia de Haia");
		// quando solicita alteraçao
		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/update/{id}", HttpMethod.PUT, httpEntity,
				String.class, cliente.getId());
		;
		assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
	}

}
