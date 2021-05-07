package br.ufc.crateus;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Objects {

	// Criação do exchange
	private static final String EXCHANGE_NAME = "casa_inteligente";

	// Criação das routing_keys para ser criadas as filas no consumer
	private final static String ROUNTING_TEMPERATURA = "temperatura";
	private final static String ROUNTING_LOCALIZACAO = "localizacao";
	private final static String ROUNTING_LUMINOSIDADE = "luminosidade";
	private final static String ROUNTING_LIMPEZA = "limpeza";

	// Definindo constantes para Luminosidade e Temperatura
	private static final int TEMPERATURA_DEFAULT = 24;
	private static final int LUMINOSIDADE_DEFAULT = 500;

	// Criação das flags;
	private static boolean ligarArCondicionado = false;
	private static boolean ligarAspirador = false;
	private static boolean ligarLampada = false;
	private static boolean fechaduraDigital = true;
	
	static Date dataHoraAtual = new Date();
	static String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
	static String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);

	public static void main(String[] args) throws Exception {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "direct");

		String queueTemperatura = channel.queueDeclare().getQueue();
		String queueLocalizacao = channel.queueDeclare().getQueue();
		String queueLimpeza = channel.queueDeclare().getQueue();
		String queueLuminosidade = channel.queueDeclare().getQueue();

		channel.queueBind(queueTemperatura, EXCHANGE_NAME, ROUNTING_TEMPERATURA);
		channel.queueBind(queueLocalizacao, EXCHANGE_NAME, ROUNTING_LOCALIZACAO);
		channel.queueBind(queueLimpeza, EXCHANGE_NAME, ROUNTING_LIMPEZA);
		channel.queueBind(queueLuminosidade, EXCHANGE_NAME, ROUNTING_LUMINOSIDADE);

		imprime();

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {

			String message;
			if (delivery.getEnvelope().getRoutingKey().equals(ROUNTING_LOCALIZACAO)) {
				message = new String(delivery.getBody(), "UTF-8");
				if (message.equals("1")) {
					fechaduraDigital = false;
					imprime();
				} else {
					fechaduraDigital = true;
					ligarArCondicionado = false;
					ligarAspirador = false;
					ligarLampada = false;
					
					imprime();
				}
			} else if (delivery.getEnvelope().getRoutingKey().equals(ROUNTING_TEMPERATURA)) {
				message = new String(delivery.getBody(), "UTF-8");
				int msg = Integer.parseInt(message);
				if (msg > TEMPERATURA_DEFAULT) {
					ligarArCondicionado = true;
					imprime();
				} else {
					ligarArCondicionado = false;
					imprime();
				}
			} else if (delivery.getEnvelope().getRoutingKey().equals(ROUNTING_LIMPEZA)) {
				message = new String(delivery.getBody(), "UTF-8");
				if (message.equals("1")) {
					ligarAspirador = true;
					imprime();
				} else {
					ligarAspirador = false;
					imprime();
				}
			} else {
				message = new String(delivery.getBody(), "UTF-8");
				int msg = Integer.parseInt(message);
				if (msg < LUMINOSIDADE_DEFAULT) {
					ligarLampada = true;
					imprime();
				} else {
					ligarLampada = false;
					imprime();
				}
			}
		};

		channel.basicConsume(queueLocalizacao, true, deliverCallback, consumerTag -> { });
		channel.basicConsume(queueTemperatura, true, deliverCallback, consumerTag -> { });
		channel.basicConsume(queueLimpeza, true, deliverCallback, consumerTag -> { });
		channel.basicConsume(queueLuminosidade, true, deliverCallback, consumerTag -> {	});

	}

	public static void imprime() {
		System.out.println();
		System.out.println("============= Casa inteligente (" + data + ", " + hora + ") =============");
		
//		Imprime se a fechadura está travada ou não
		if (fechaduraDigital == false) 
			System.out.println("Fechadura digital destravada");
		else 
			System.out.println("Fechadura digital travada");

//		Imprime se o ar-condicionado está ligado ou não
		if (ligarArCondicionado == true)
			System.out.println("Ar-condicionado ligado");
		else
			System.out.println("Ar-condicionado desligado");
		
//		Imprime se a lâmpada está ligada ou não
		if (ligarLampada == true)
			System.out.println("A lâmpada foi ligada");
		else
			System.out.println("A lâmpada foi desligada");

//		Imprime se o aspirador de pó está ligado ou não
		if (ligarAspirador == true) 
			System.out.println("Aspirador de pó ligado");
		else
			System.out.println("Aspirador de pó desligado");
	}

}
