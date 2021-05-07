package br.ufc.crateus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sensores {

	// Criação do exchange
	private static final String EXCHANGE_NAME = "casa_inteligente";

	// Criação das routing_keys para ser criadas as filas no consumer
	private final static String ROUNTING_TEMPERATURA = "temperatura";
	private final static String ROUNTING_LOCALIZACAO = "localizacao";
	private final static String ROUNTING_LUMINOSIDADE = "luminosidade";
	private final static String ROUNTING_LIMPEZA = "limpeza";

	public static void main(String[] args) {
		char option;
		Scanner scan = new Scanner(System.in);

		Date dataHoraAtual = new Date();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");

		while (true) {

			String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
			String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);

			System.out.println("=============== Ações fora de casa (" + data + ", " + hora + ") ===============");
			System.out.println("1 - Ir para casa");
			System.out.println("2 - Ir ao mercado");
			System.out.println("3 - Ir ao bar");
			System.out.println("Escolha o que João deve fazer: ");
			option = scan.next().charAt(0);
			clearBuffer(scan);

			switch (option) {
				case '1':
					capturarInfor(factory);
					break;
				case '2':
					try {
						System.out.println("Espere João voltar do mercado...");
						Thread.sleep(5000);
					} catch (InterruptedException _ignored) {
						Thread.currentThread().interrupt();
					}
					break;
				case '3':
					try {
						System.out.println("Espere João voltar do bar...");
						Thread.sleep(5000);
					} catch (InterruptedException _ignored) {
						Thread.currentThread().interrupt();
					}
					break;
				default:
					System.out.println("Escolha as opções presentes no menu.");
					break;
			}
		}
	}

	public static void capturarInfor(ConnectionFactory factory) {

		char option;
		int temperatura, luminosidade;
		boolean flagAspirador = false;
		Scanner scan = new Scanner(System.in);
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
			
			channel.basicPublish(EXCHANGE_NAME, ROUNTING_LOCALIZACAO, null, "1".getBytes());

			while (true) {
				System.out.println("============= Casa inteligente - Sensores =============");
				System.out.println("1 - Capturar a temperatura do ambiente");
				System.out.println("2 - Ligar / Desligar aspirador de pó");
				System.out.println("3 - Capturar a luminosidade do ambiente");
				System.out.println("4 - Sair da casa");

				System.out.println("Escolha uma opção: ");
				option = scan.next().charAt(0);
				clearBuffer(scan);

				if (option == '4') {
					channel.basicPublish(EXCHANGE_NAME, ROUNTING_LOCALIZACAO, null, "0".getBytes());
					break;
				}

				switch (option) {
					case '1':
						System.out.println("Digite a temperatura(°C) capturada pelo sensor para ligar "
								+ "o ar-condicionado: ");
						temperatura = scan.nextInt();
						clearBuffer(scan);
						
						channel.basicPublish(EXCHANGE_NAME, ROUNTING_TEMPERATURA, null, 
								Integer.toString(temperatura).getBytes());
						break;
					case '2':
						if(flagAspirador == false) {
							flagAspirador = true;
							channel.basicPublish(EXCHANGE_NAME, ROUNTING_LIMPEZA, null, "1".getBytes());
							break;
						} else {
							flagAspirador = false;
							channel.basicPublish(EXCHANGE_NAME, ROUNTING_LIMPEZA, null, "0".getBytes());
							break;
						}
					case '3':
						System.out.println("Digite a luminosidade(em lux) capturada pelo sensor para ligar "
								+ "a lâmpada: ");
						luminosidade = scan.nextInt();
						clearBuffer(scan);
						
						channel.basicPublish(EXCHANGE_NAME, ROUNTING_LUMINOSIDADE, null, 
								Integer.toString(luminosidade).getBytes());
						break;
					default:
						System.out.println("A opção digitada não existe!");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void clearBuffer(Scanner scanner) {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

}
