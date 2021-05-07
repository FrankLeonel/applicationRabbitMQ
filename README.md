# applicationRabbitMQ

Esta aplicação foi desenvolvida durante a disciplina de Sistema Distribuído, na qual o intuito do programa é simular uma casa inteligente, onde o usuário é responsável por captar as informações dos sensores e, a partir disso, as mensagens são colocadas em um __Messaging Broken__(RabbitMQ). A comunicação entre os sensores e os objetos se davam por meio de um _Exchange_(foi usado o exchange direct) e o exchange se comunicava com filas.

# Sensores simulados:
  - Localização;
  - Temperatura;
  - Luminosidade.
 
# Objetos simulados:
  - Lâmpada;
  - Ar-condicionado;
  - Fechadura digital;
  - Aspirador de pó.
 
 # Funcionalidades
   - Quando o sensor de localização captar que o usuário está em casa, a fechadura digital deve destravar. Caso o usuário saia de casa, a fechadura digital deverá travar;
   - O usuário é responsável por ligar/desligar o aspirador de pó;
   - Quando o sensor de temperatura captar uma temperatura (em ºC) maior que a constante (_e.g._, 24 ºC) definida, o sensor avisa para o ar-condicionado ser ligado. Caso em um dado momento, o sensor captar uma temperatura menor ou igual que a constante, o ar-condicionado será desligado;
   - Quando o sensor de luminosidade captar uma luminosidade (em _lux_) menor que a constante (_e.g._, 500 lux) definida, o sensor avisa para a lâmpada ser ligada. Caso em um dado momento, o sensor captar uma luminosidade maior ou igual que a constante, a lâmpada será desligada;

__OBS:__ O usuário é responsável por simular os dados dos sensores.
