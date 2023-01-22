package dev.abidux.filetransfer.client;

import dev.abidux.filetransfer.Main;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {

    public static void send(String host, File file) throws IOException {
        send(host, file, "./");
    }

    private static void send(String host, File file, String path) throws IOException {
        int fileSize = (int)Files.size(file.toPath());
        if (!file.isDirectory()) {
            try (Socket socket = new Socket(host, 3333)) {
                try (DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                    output.writeByte(0);
                    output.writeUTF(path + "/" + file.getName());
                    output.writeInt(fileSize);
                    output.flush();
                }
            }
            System.out.println("[CLIENT] Enviando " + file.getName() + "...");
            FileInputStream fileInput = new FileInputStream(file);
            byte[] buffer = new byte[fileSize];
            fileInput.read(buffer);
            try (Socket socket = new Socket(host, 3333)) {
                try (DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                    output.write(buffer, 0, buffer.length);
                    output.flush();
                }
            }
        } else for (File f : file.listFiles()) {
            send(host, f, file.getPath().substring(file.getPath().indexOf(Main.FILE.getName())));
        }
    }

    public static void finish(String host) throws IOException {
        try (Socket socket = new Socket(host, 3333)) {
            try (DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                output.writeByte(1);
                output.flush();
            }
        }
    }

}