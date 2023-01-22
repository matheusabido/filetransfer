package dev.abidux.filetransfer.server;

import dev.abidux.filetransfer.Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static ServerSocket serverSocket;
    private static boolean running = true;
    public static void listen() throws IOException {
        serverSocket = new ServerSocket(3333);
        while(running) {
            try (Socket socket = serverSocket.accept()) {
                handleSocket(socket);
            }
        }
    }

    private static String fileName;
    private static int fileSize;
    private static void handleSocket(Socket socket) throws IOException {
        if (fileName == null) {
            try (DataInputStream input = new DataInputStream(socket.getInputStream())) {
                byte action = input.readByte();
                if (action == 1) {
                    running = false;
                    return;
                }
                String fileName = input.readUTF();
                int fileSize = input.readInt();

                System.out.println("[SERVER] Receiving " + fileName.substring(fileName.lastIndexOf("/") + 1) + " (" + fileSize + " bytes)...");

                Server.fileName = fileName;
                Server.fileSize = fileSize;
            }
            return;
        }

        try (InputStream input = socket.getInputStream()) {
            File file = new File(Main.DOWNLOADS_FOLDER, fileName);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            try (FileOutputStream output = new FileOutputStream(file)) {
                byte[] buffer = new byte[fileSize];
                int n;
                while ((n = input.read(buffer)) > 0) {
                    output.write(buffer, 0, n);
                }
                output.flush();
                System.out.println("[SERVER] " + fileName.substring(fileName.lastIndexOf("/") + 1) + " copied.");
            }
        }
        fileName = null;
        fileSize = 0;
    }

}