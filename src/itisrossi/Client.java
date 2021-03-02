package itisrossi;

public class Client {

    public String ip;
    public int port;
    public long lastPacket;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
