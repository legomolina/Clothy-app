import config.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Clothy extends Application {
    @Override
    public void start(Stage loginStage) throws Exception {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("views/login/login.fxml"));
        loginStage.setTitle("Clothy App");
        loginStage.getIcons().add(new Image("resources/clothy_icon.png"));
        loginStage.setScene(new Scene(loginRoot));
        loginStage.sizeToScene();
        loginStage.setResizable(false);
        loginStage.show();

        /*new Thread(() -> {
            try {
                System.out.println("Started socket on port " + Constants.SOCKET_PORT);
                DatagramSocket serverSocket = new DatagramSocket(Constants.SOCKET_PORT);
                byte[] receiveData = new byte[1024];
                byte[] sendData = new byte[1024];

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    System.out.println("RECEIVED: " + sentence);
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    sendData = "1".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
                }
            } catch (SocketException e) {
                System.out.println("Can't connect to socket on port " + Constants.SOCKET_PORT);

                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error while receiving or sending through socket");

                e.printStackTrace();
            }
        }).start();*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
