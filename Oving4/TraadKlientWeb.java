import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class TraadKlientWeb extends Thread {
    Socket forbindelse;

    public TraadKlientWeb(Socket forbindelse) {
        this.forbindelse = forbindelse;
    }

    public void run() {
        InputStreamReader leseforbindelse = null;
        PrintWriter skriveren = null;
        try {
            leseforbindelse = new InputStreamReader(forbindelse.getInputStream());
            skriveren = new PrintWriter(forbindelse.getOutputStream(), true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedReader leseren = new BufferedReader(leseforbindelse);
        /* Mottar data fra klienten */
        String enLinje = null;
        try {
            enLinje = leseren.readLine();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } // mottar en linje med tekst
        if (enLinje != null || enLinje.equals("")) {
            System.out.println("Mottok header: " + enLinje);
            skriveren.println("HTTP/1.1 200 OK");
            skriveren.println("Content-Type: text/html; charset=utf-8");
            skriveren.println();
            skriveren.println("<HTML><BODY>");
            skriveren.println("<H1> Vær hilset studass. Du har koblet deg opp til min enkle web-tjener </h1>");
            skriveren.println("<ul>");
        }
        while (enLinje != null) { // forbindelsen pa klientsiden er lukket
            skriveren.println("<li>" + enLinje + "</li>");
            try {
                enLinje = leseren.readLine();
                if (enLinje.equals("")) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        skriveren.println("</ul>");
        skriveren.println();
        skriveren.println();
        /* Lukker forbindelsen */
        try {
            leseren.close();
            skriveren.close();
            forbindelse.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}