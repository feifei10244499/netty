package service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer {

    public static void  main(String[] args) throws Exception{
        System.out.println("waiting...");
        serve(8888);
    }
    public static void serve(int port) throws IOException {
        /**
         * 将服务器绑定 到指定端口
         */
        final ServerSocket socket = new ServerSocket(port);
        try {
            for (;;) {
                /**
                 *接受连接
                 */
                final Socket clientSocket = socket.accept();
                System.out.println(
                        "Accepted connection from " + clientSocket);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;
                        try {
                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(
                                    Charset.forName("UTF-8")));
                            out.flush();
                            clientSocket.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            try {
                                clientSocket.close();
                            }
                            catch (IOException ex) {
                                // ignore on close
                                 }
                        }
                    }
                }).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}