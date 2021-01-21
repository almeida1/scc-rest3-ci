package com.fatec.scelv1;



import static org.junit.jupiter.api.Assertions.*;


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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fatec.scelv1.model.Cliente;
import com.fatec.scelv1.model.ClienteRepository;
import com.fatec.scelv1.model.Endereco;
import com.fatec.scelv1.servico.ClienteServicoI;



@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

class REQ01CadastrarClienteTests {

	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private ClienteServicoI servico;
	@Autowired
	ClienteRepository repository;
	private Cliente cliente;

	@Test
	public void ct01_quando_post_dadosValidos_deveSalvarCliente() {

		cliente = new Cliente("66666666666", "Carlos", "carlos@email", "03694000");
		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
		// o metodo exchange é um metodo generico com diversas assinaturas que pode
		// executar qualquer metodo http, no geral o que ele faz é excutar uma
		// requisição e encapsular a resposta
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/save", HttpMethod.POST, httpEntity,
				String.class);
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
	    repository.deleteByCpf("66666666666");

	}

	/**
	 * neste exemplo o erro é detectado atraves do manipulador de excecoes este
	 * teste quando executado pelo postman retorna somente as mensagens de erro este
	 * caso de teste usa o Spring Boot @RestControllerAdvice obtem erro do @valid o
	 * parameterized pode ser utilizado quando se espera uma lista de strings retornada da
	 * requisicao
	 */
//	@Test
//	public void ct02_quando_post_com_cpf_em_branco_DeveRetornarMensagemDeErro() {
//		cliente = new Cliente("", "Carlos", "carlos@email", "03694000");
//		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
//		ResponseEntity<List<String>> resposta = testRestTemplate.exchange("/clientes/save", HttpMethod.POST, httpEntity,
//				new ParameterizedTypeReference<List<String>>() {
//				});
//
//		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
//		assertTrue(resposta.getBody().contains("CPF deve ter 11 caracteres")); // neste exemplo retornou um string
//
//	}
	@Test
	public void ct02_quando_post_com_cpf_em_branco_DeveRetornarMensagemDeErro() {
		cliente = new Cliente("", "Carlos", "carlos@email", "03694000");
		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/save", HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<String>() {
				});

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertTrue(resposta.getBody().contains("CPF deve ter 11 caracteres")); // neste exemplo retornou um string

	}
	/*
	 * obtem erro da camada de banco de dados
	 */
	@Test
	public void ct03_quando_post_com_cpf_ja_cadastrado_DeveRetornarMensagemDeErro() {
		cliente = new Cliente("11111111111", "Carlos", "carlos@email", "03694000");
		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("jose", "123").exchange("/api/clientes/v1/save", HttpMethod.POST, httpEntity,
				String.class);
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());

	}

	// alem do exchange é possivel realizar chamadas com metodos mais especificos
	// metodo getForEntity realiza a chamada para um endereco, converte o resultado
	// no tipo passado como parametro e vai encapsular em um ResponseEntity
	// diferentemente do exchange estes metodos não recebem o Parameterized
	@Test
	public void ct04_quando_post_com_for_entity_inserirDeveSalvarCliente() {
		cliente = new Cliente("44444444444", "Carlos", "carlos@email", "03694000");
		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("jose", "123").postForEntity("/api/clientes/v1/save", httpEntity, String.class);

		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		
		 repository.deleteByCpf("44444444444");
	}

	@Test
	public void ct05_quando_post_for_object_DeveSalvarCliente() {
		cliente = new Cliente("55555555555", "Carlos", "carlos@email", "03694000");
		HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente);
		String resposta = testRestTemplate.withBasicAuth("jose", "123").postForObject("/api/clientes/v1/save",httpEntity, String.class);
		assertEquals("Cliente cadastrado", resposta);
		repository.deleteByCpf("55555555555");

	}

	// verifica o comportamento acesso à resposta HTTP, portanto, podemos fazer
	// coisas como
	// verificar o código de status para garantir que a operação foi bem-sucedida ou
	// trabalhar
	// com o corpo real da resposta:
	// Estamos trabalhando com o corpo da resposta como uma String padrão aqui e
	// usando
	// Jackson (e a estrutura de nó JSON que Jackson fornece) para verificar alguns
	// detalhes.
	@Test
	public void ct06_quando_cep_valido_viacep_retorna_ok() {
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://viacep.com.br/ws/04330020/json/";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}
   // no postmam retorna 500 internal server error
	@Test
	public void ct07_quando_cep_invalido_via_cep_retorna_string() { //viacep deveria retornar json
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://viacep.com.br/ws/04330/json/";
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.getForEntity(url, String.class);
		} catch (HttpClientErrorException e) {
			assertEquals("400 Bad Request", e.getMessage().substring(0, 15));

		}
	}
	 // no postmam retorna 500 internal server error
		@Test
		public void ct08_quando_cep_invalido_viacep_retorna_400() {
			RestTemplate restTemplate = new RestTemplate();
			String url = "https://viacep.com.br/ws/04330/json/";
			ResponseEntity<String> response = null;
			try {
				response = restTemplate.getForEntity(url, String.class);
			} catch (HttpClientErrorException e) {
				assertEquals("400 Bad Request", e.getMessage().substring(0, 15));

			}
		}
	// recuperando pojo ao inves de json
	@Test
	public void ct09_quando_cep_valido_viacep_retorna_ok() {
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		String cep = "03694000";
		Endereco endereco = restTemplate.getForObject(url, Endereco.class, cep);
		assertEquals("Avenida Águia de Haia", endereco.getLogradouro());
	}
	 // teste pela camdada de servico
	@Test
	public void ct10_quando_cep_valido_servico_obtem_endereco_retorna_ok() {
		ResponseEntity<Endereco> response = servico.obtemEndereco("03694000") ;
		Endereco endereco = response.getBody();
		assertEquals("Avenida Águia de Haia", endereco.getLogradouro());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	 // teste pela camdada de servico
		@Test
		public void ct11_quando_cep_invalido_servico_obtem_endereco_retorna_bad_request() {
			ResponseEntity<Endereco> response = servico.obtemEndereco("03694") ;
			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}
}
