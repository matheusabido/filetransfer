package dev.abidux.filetransfer;

import dev.abidux.filetransfer.client.Client;
import dev.abidux.filetransfer.server.Server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static File FILE;
    public static String DOWNLOADS_FOLDER;
    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            DOWNLOADS_FOLDER = args[0];
            System.out.println("Aguardando conexão...");
            Server.listen();
            return;
        }

        String host;
        File file;
        boolean sending;
        try (Scanner scanner = new Scanner(System.in)) {
            boolean reachable = false;
            do {
                System.out.print("Insira o IP do servidor: ");
                host = scanner.nextLine();
                if (host.length() == 0) continue;
                try {
                    reachable = InetAddress.getByName(host).isReachable(1000);
                    if (!reachable) System.out.println("Não foi possível conectar ao servidor. Por favor, tente novamente.\n");
                } catch (UnknownHostException e) {
                    System.out.println("IP inválido.");
                }
            } while (!reachable);
            System.out.println("Conectado ao servidor.");

            do {
                boolean foundFile;
                do {
                    System.out.print("Qual o nome do arquivo que deseja enviar?\n> ");
                    String fileName = scanner.nextLine();
                    file = new File("./", fileName);
                    foundFile = file.exists();
                    if (!foundFile) {
                        Optional<File> op = Arrays.stream(new File("./").listFiles()).filter(f -> f.getName().toLowerCase().contains(fileName.toLowerCase())).findFirst();
                        foundFile = op.isPresent();
                        if (foundFile) file = op.get();
                        else System.out.println("Arquivo não encontrado. Por favor, tente novamente.\n");
                    }
                } while (!foundFile);

                FILE = file;
                Client.send(host, file);
                System.out.print("Deseja enviar mais arquivos? (true/false)\n> ");
                sending = scanner.nextBoolean();
            } while (sending);
            Client.finish(host);
        }
    }

}