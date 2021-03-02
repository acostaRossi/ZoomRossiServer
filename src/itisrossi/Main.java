package itisrossi;

import java.net.SocketException;

public class Main {

    public static void main(String[] args) {

        try {
            new ServerUDPVideo().start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
