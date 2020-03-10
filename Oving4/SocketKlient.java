/*
 * SocketKlient.java  - "Programmering i Java", 4.utgave - 2009-07-01
 *
 * Programmet kontakter et tjenerprogram som allerede kjorer pa port 1250.
 * Linjer med tekst sendes til tjenerprogrammet. Det er laget slik at
 * det sender disse tekstene tilbake.
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

class SocketKlient {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 1250;

        /* Bruker en scanner til a lese fra kommandovinduet */
        Scanner leserFraKommandovindu = new Scanner(System.in);
        System.out.print("Oppgi navnet pa maskinen der tjenerprogrammet kjorer: ");
        String tjenermaskin = leserFraKommandovindu.nextLine();

        /* Setter opp forbindelsen til tjenerprogrammet */
        Socket forbindelse = new Socket(tjenermaskin, PORTNR);
        System.out.println("Na er forbindelsen opprettet.");

        /* Apner en forbindelse for kommunikasjon med tjenerprogrammet */
        InputStreamReader leseforbindelse = new InputStreamReader(forbindelse.getInputStream());
        BufferedReader leseren = new BufferedReader(leseforbindelse);
        PrintWriter skriveren = new PrintWriter(forbindelse.getOutputStream(), true);

        /* Leser innledning fra tjeneren og skriver den til kommandovinduet */
        String innledning1 = leseren.readLine();
        String innledning2 = leseren.readLine();
        System.out.println(innledning1 + "\n" + innledning2);

        /* Leser tekst fra kommandovinduet (brukeren) */

        String enLinje = leserFraKommandovindu.nextLine();
        while (!enLinje.equals("")) {
            skriveren.println(enLinje); // sender tall1 til tjeneren
            enLinje = leserFraKommandovindu.nextLine();
            skriveren.println(enLinje); // sender tall2 til tjeneren

            enLinje = leserFraKommandovindu.nextLine();
            skriveren.println(enLinje); // sender operasjon til tjeneren

            String respons = leseren.readLine(); // mottar respons fra tjeneren
            System.out.println("Du far tallet: " + respons);

            innledning1 = leseren.readLine();
            System.out.println(innledning1);
            enLinje = leserFraKommandovindu.nextLine();
            if (enLinje.equals("exit"))
                break;
        }

        /* Lukker forbindelsen */
        leseren.close();
        skriveren.close();
        forbindelse.close();
    }
}

/*
 * Utskrift pa klientsiden: Oppgi navnet pa maskinen der tjenerprogrammet
 * kjorer: tonje.aitel.hist.no Na er forbindelsen opprettet. Hei, du har kontakt
 * med tjenersiden! Skriv hva du vil, sa skal jeg gjenta det, avslutt med
 * linjeskift. Hallo, dette er en prove. Fra tjenerprogrammet: Du skrev: Hallo,
 * dette er en prove. Og det fungerer utmerket. Fra tjenerprogrammet: Du skrev:
 * Og det fungerer utmerket.
 */