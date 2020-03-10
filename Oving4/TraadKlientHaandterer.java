import java.net.Socket;
import java.io.*;

class TraadKlientHaandterer extends Thread {
    private Socket s;

    public TraadKlientHaandterer(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            /* Apner strommer for kommunikasjon med klientprogrammet */
            InputStreamReader leseforbindelse = new InputStreamReader(s.getInputStream());
            BufferedReader leseren = new BufferedReader(leseforbindelse);
            PrintWriter skriveren = new PrintWriter(s.getOutputStream(), true);
            /* Sender innledning til klienten */
            skriveren.println("Hei, du har kontakt med tjenersiden!");
            skriveren.println("Skriv inn to tall og + eller -: ");
            String enLinje = leseren.readLine(); // mottar en linje med tekst
            /* Mottar data fra klienten */
            while (enLinje != null) { // forbindelsen pa klientsiden er lukket
                System.out.println("En klient skrev: " + enLinje);
                int tall1 = Integer.parseInt(enLinje.trim());
                System.out.println(tall1);
                enLinje = leseren.readLine();
                System.out.println("En klient skrev: " + enLinje);
                int tall2 = Integer.parseInt(enLinje.trim());
                System.out.println(tall2);

                enLinje = leseren.readLine();
                System.out.println(enLinje);

                if (enLinje.charAt(0) == '+')
                    skriveren.println(tall1 + tall2);
                else if (enLinje.charAt(0) == '-')
                    skriveren.println(tall1 - tall2);

                skriveren.println("Skriv inn to tall og + eller -: ");
                enLinje = leseren.readLine(); // mottar en linje med tekst
            }

            /* Lukker forbindelsen */
            leseren.close();
            skriveren.close();
            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}