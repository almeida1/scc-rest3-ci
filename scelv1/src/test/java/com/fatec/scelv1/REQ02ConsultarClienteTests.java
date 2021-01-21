package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fatec.scelv1.model.Cliente;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ02ConsultarClienteTests {

	@Autowired
	private TestRestTemplate restTemplate;
	private Cliente cliente;

	@Test
	public void ct01_quando_consulta_todos_entao_retorna_array() {
		//dado que - existem clientes cadastrados
		//quando solicita consulta todos
		//RestTemplate restTemplate = new RestTemplate();//com testresttemplate nao deve ser definido o caminho com localhost
		ResponseEntity<Cliente[]> resposta = restTemplate.withBasicAuth("jose", "123").getForEntity("/api/clientes/v1/consulta", Cliente[].class);
		
		Cliente[] array = resposta.getBody();
		//Cliente umCliente = array[0];
		//umCliente.getNome();
		//entao 
		assertEquals(9, array.length);
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	@Test
	public void ct02_quando_consulta_todos_retorna_um_List() {
		//dado que - existem clientes cadastrados
		//quando solicita consulta todos
		ResponseEntity<List<Cliente>> resposta = 
				  restTemplate.withBasicAuth("jose", "123")
				  .exchange(
					"/api/clientes/v1/consulta",
				    HttpMethod.GET,
				    null,
				    new ParameterizedTypeReference<List<Cliente>>() {}
				  );
		//então
		List<Cliente> clientes = resposta.getBody();
		Cliente umCliente = clientes.get(1);
		clientes.contains(umCliente);
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	@Test
	public void ct03_deveMostrarTodosClientes() {
		ResponseEntity<String> resposta = restTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/consulta", HttpMethod.GET, null,
				String.class);
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}

	// o metodo exchange é um metodo generico com diversas assinaturas que pode
	// executar
	// qualquer metodo http, no geral o que ele faz é excutar uma requisição e
	// encapsular a resposta
	// em um ReponseEntity
	// exchange("url",HttpMethod.GET,httpEntity(header ou body passado na
	// requisição), Integer.class(indica o tipo
	// do retorno da resposta)
	@Test
	public void ct04_deveMostrarTodosClientesUsandoList() {
		ParameterizedTypeReference<List<Cliente>> tipoRetorno = new ParameterizedTypeReference<List<Cliente>>() {
		};
		ResponseEntity<List<Cliente>> resposta = restTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/consulta", HttpMethod.GET, null,
				tipoRetorno);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
//		assertTrue(resposta.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON));
		assertEquals(9, resposta.getBody().size());

	}

	@Test
	public void ct05_deveMostrarUmCliente() {
		cliente = new Cliente("11111111119", "Jose9", "jose9@email", "043309");
        long id = 9;
		cliente.setId(id);
		cliente.setEndereco("Rua Sao Paulo9");
        //para o uso de generics exchange é a melhor opção
		ResponseEntity<Cliente> resposta = restTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/consulta_id/{id}", HttpMethod.GET, null,
				Cliente.class, 9);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertTrue(resposta.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON));
		assertEquals(id, resposta.getBody().getId());
	}
	@Test
	public void ct06_deveRetornarContatoNaoEncontrado() {
		
		ResponseEntity<Cliente> resposta = restTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/consulta_id/{id}", HttpMethod.GET, null,
				Cliente.class, 99);

		assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode()); //valida comportamento
		assertNull(resposta.getBody()); //valida o estado
		
	}
	//alem do exchange é possivel realizar chamadas com metodos mais especificos
		// metodo getForEntity realiza a chamada para um endereco, converte o resultado
		// no tipo passado como parametro e vai encapsular em um ResponseEntity
		// diferentemente do exchange estes metodos não recebem o Parameterized
	@Test
	public void ct07_deveMostrarUmClienteComGetForEntity() {
		cliente = new Cliente("11111111119", "Jose9", "jose9@email", "043309");
		long id = 9;
		cliente.setId(id);
		
		cliente.setEndereco("Rua Sao Paulo9");
		ResponseEntity<Cliente> resposta =
				restTemplate.withBasicAuth("jose", "123").getForEntity("/api/clientes/v1/consulta_id/{id}", Cliente.class,cliente.getId());
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertTrue(resposta.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON));
		//assertEquals(cliente, resposta.getBody()); //valida estado
	}
	
	@Test
	public void ct08_quando_consulta_id_valido_retorna_cliente() {
		//dado que o id existe
		long id = 9;
		//quando - consulta por id
		Cliente resposta = 	restTemplate.withBasicAuth("jose", "123").getForObject("/api/clientes/v1/consulta_id/{id}", Cliente.class,id);
		//entao
		assertEquals("Jose9", resposta.getNome());
	}
	@Test
	public void ct09_quando_consulta_id_invalido_retorna_cliente() {
		//dado que o id existe
		//quando - consulta por id
		ResponseEntity<Cliente> resposta = restTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/consulta_id/{id}", HttpMethod.GET, null,
				Cliente.class, 19);

		assertEquals(HttpStatus.NOT_FOUND,resposta.getStatusCode());
		
	}
	@Test
	public void ct10_quando_consulta_cpf_valido_retorna_cliente() {
		Cliente resposta =
				restTemplate.withBasicAuth("jose", "123").getForObject("/api/clientes/v1/consulta_cpf/{cpf}", Cliente.class,"11111111119");
		assertEquals("Jose9", resposta.getNome());
		
	}
	
}
