import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by Ian on 15.03.2020.
 */
public class Client {
    public static void main(String[] args) {
        final int PORT_NUMBER = 1337;

        System.out.println("Kobler til tjener...");
        System.out.println("Skriv inn et regnestykke her (eller 'close' hvis du vil lukke programmet)..");

        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getByName("127.0.0.1");

            byte[] sendBytes;
            byte[] receiveBytes = new byte[256];

            while (true) {
                String melding = bufferedReader.readLine();
                if (melding.equals("close")) {
                    datagramSocket.close();
                    return;
                }

                sendBytes = melding.getBytes();

                DatagramPacket datagramPacket = new DatagramPacket(sendBytes, 0, sendBytes.length, inetAddress,
                        PORT_NUMBER);
                datagramSocket.send(datagramPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
                datagramSocket.receive(receivePacket);

                String svar = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Svar fra server: " + svar);
            }

            // datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}