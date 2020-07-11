# Unnoficial DarkOrbit Bot

Bot feito para facilitar a gestão do servidor de Discord não oficial do jogo DarkOrbit. Este servidor já não está ativo.

## Funcionalidades

### Sistema de Feedback

Ao postar uma mensagem no canal de feedback, a mensagem é automaticamente substituída por uma mensagem formada através de Embeds do Discord para um melhor aspeto visual. A estas mensagens são adicionadas reações que permitem que os outros utilizadores digam se gostam ou não da ideia. Após um período de tempo definido, as reações são eliminadas e o resultado é mostrado.

### Notificações de Posts no Fórum BigPoint

Misturando uma análise do feed RSS e da página HTML do post, o bot encontra novos posts no fórum e notifica o evento num canal do Discord. Posts podem ser novas threads ou comentários.

### Espelhamento de Mensagens

Possibilidade de copiar mensagem de um canal de texto para outro. É possível especificar qualquer quantidade de espelhos (par canal origem/destino).

Este sistema é implementado através de duas contas Discord. Uma conta é uma conta de bot enquanto que a outra é uma conta de utilizador. As mensagens são copiadas através da conta de utilizador e postadas através da conta de bot.

### Sistema de Compra/Venda/Troca de Contas

Este sistema ficou incompleto quando o projeto foi abandonado. Ficou implementado apenas o registo de contas a vender. O utilizador indica que quer vender uma conta através de um comando e o bot apresenta-lhe um questionário que deve responder. As respostas do questionário são guardadas.

## Tecnologias/Frameworks Utilizadas

Java, Maven, JDBC, MySQL, HTML, XML, W3C DOM Parser e Jsoup.
