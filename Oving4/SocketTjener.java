import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class SocketTjener {
    public static void main(String[] args) throws IOException {
        ServerSocket tjener = new ServerSocket(1250);

        while (true) {
            Socket forbindelse = tjener.accept();
            Thread klienttrad = new TraadKlientHaandterer(forbindelse); // oppgave1
            // Thread klienttrad = new TraadKlientWeb(forbindelse); //oppgave 2

            klienttrad.start();
        }
    }
}

/*
 * Utskrift pa tjenersiden: Logg for tjenersiden. Na venter vi... En klient
 * skrev: Hallo, dette er en prove. En klient skrev: Og det fungerer utmerket.
 */