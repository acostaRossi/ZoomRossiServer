package itisrossi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerUDPVideo extends Thread {

    ArrayList<Client> clients;
    DatagramSocket serverSocket;

    final int BUFFER_SIZE = 500;

    public ServerUDPVideo() throws SocketException {
        serverSocket = new DatagramSocket(8);
        clients = new ArrayList<>();
    }

    @Override
    public void run() {

        byte[] dtArray = new byte[BUFFER_SIZE];

        DatagramPacket dtPacket = new DatagramPacket(dtArray, dtArray.length);

        while(!Thread.interrupted()) {

            System.out.println("receiving ...");

            try {
                serverSocket.receive(dtPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("received from client ip: " + dtPacket.getAddress().getHostAddress() + " port: " + dtPacket.getPort());

            boolean newClient = true;

            for (Client client : clients) {
                if (client.ip.equals(dtPacket.getAddress().getHostAddress())) {
                    if (client.port == dtPacket.getPort()) {
                        client.lastPacket = System.currentTimeMillis();
                        newClient = false;
                    }
                }
            }

            if (newClient) {
                clients.add(new Client(dtPacket.getAddress().getHostAddress(), dtPacket.getPort()));
            }

            try {
                for (Client client : clients) {
                    if (!client.ip.equals(dtPacket.getAddress().getHostAddress()) ||
                            (client.ip.equals(dtPacket.getAddress().getHostAddress()) && client.port != dtPacket.getPort())) {

                        DatagramPacket dtPacketResponse = new DatagramPacket(dtPacket.getData(), dtPacket.getData().length, InetAddress.getByName(client.ip), client.port);
                        System.out.println("sending to client ip: " + client.ip + " port: " + client.port);
                        serverSocket.send(dtPacketResponse);
                        System.out.println("sended to client ip: " + client.ip + " port: " + client.port);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // remove inactive clients (30 seconds)

            int randomInt = (int) (Math.random() * (10 + 1));

            if (randomInt == 5) {

                Iterator<Client> i = clients.iterator();
                while (i.hasNext()) {
                    Client client = i.next();

                    long eTime = System.currentTimeMillis() - client.lastPacket;

                    if (eTime > 1000 * 30) {
                        i.remove();
                    }
                }
            }
        }
    }
}
