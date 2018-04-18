package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
//importa-se para criar um Socket que está aguardando conexões
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Servidor {

    public static void main(String[] args) throws IOException {


        /* o comando cria um socket chamado "servidor", do tipo ServerCocket,
        associado a porta 8000 e aguardando conexões*/
        ServerSocket servidor = new ServerSocket(8000);

        //o comando diz que o socket aceita a primeita conexao que vier
        Socket socket = servidor.accept();

        //a função if serve pra verificar se está conectado
        if (socket.isConnected()) {

            //imprime o IP do cliente   
            System.out.println(socket.getInetAddress());

            //cria um BufferedReader 'buffer' através do InputStream, que recebe os dados do cliente 
            //o buffer serve para pegar os dados do cliente
            BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Imprime "Requisição"
            System.out.println("Requisição: ");

            /* Lê a primeira linha com informaçoes da requisição*/
            String linha = buffer.readLine();

            //adiciona um espaço em branco, quebrando a string
            String[] dados = linha.split(" ");

            //importa o metodo
            String metodo = dados[0];

            //importa o caminho do arquivo
            String caminhoArquivo = dados[1];

             //importa o protocolo
            String protocolo = dados[2];

            //Adiciona um bloco de comando enquanto a linha não for vazia
            while (!linha.isEmpty()) {

                //imprime a linha
                System.out.println(linha);

                //lê a proxima linha
                linha = buffer.readLine();
            }

            //se o caminho passado pelo cliente for igual a / entao deve apresentar o /index.html
            switch (caminhoArquivo) {
                case "/":
                    caminhoArquivo = "C:\\Users\\igorl_000\\Desktop\\index.html"; //configurar para cada maquina servidora
                    break;
                case "/page2.html":
                    caminhoArquivo = "C:\\Users\\igorl_000\\Desktop\\page2.html";
                    break;
            }

            //abre o arquivo pelo caminho passado acima para encontrar o html
            File arquivo = new File(caminhoArquivo.replaceFirst("/", ""));


            String status = protocolo + " 200 OK\r\n";

            //se o arquivo procurado não existir. Abre-se o arquivo 404, representando erro.
            //Muda-se o status para 404
            if (!arquivo.exists()) {

                status = protocolo + " 404 Not Found\r\n";

                arquivo = new File("C:\\Users\\igorl_000\\Desktop\\404.html");

            }


            //lê todo o conteúdo do arquivo para bytes
            byte[] conteudo = Files.readAllBytes(arquivo.toPath());

            //cria um formato para o GMT espeficicado pelo HTTP
            SimpleDateFormat formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);

            formatador.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date data = new Date();

            //Formata a data para o padrao
            String dataFormatada = formatador.format(data) + " GMT";

            //cabeçalho padrão da resposta HTTP
            String header = status

                            + "Location: http://localhost:8000/\r\n"

                            + "Date: " + dataFormatada + "\r\n"

                            + "Server: MeuServidor/1.0\r\n"

                            + "Content-Type: text/html\r\n"

                            + "Content-Length: " 
                            + conteudo.length + "\r\n"

                            + "Connection: close\r\n"

                            + "\r\n";

            //cria o canal de resposta através do outputStream
            OutputStream resposta = socket.getOutputStream();

            //escreve o headers em bytes
            resposta.write(header.getBytes());

            //escreve o conteudo em bytes
            resposta.write(conteudo);

            //encerra a resposta
            resposta.flush();
        }
    }
}

