import java.util.*;

// codebreaker - ratespiel von sw4gaccino
// man muss das passwort erraten mit hilfe von hinweisen

public class CodeBreaker {

    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    static int level = 1;
    static int punkte = 0;

    // passwort listen pro level
    static String[][] passwoerter = {
        {"1234", "4321", "1111", "2580", "1357"},
        {"ab12", "xy99", "lo42", "hi77", "go00"},
        {"pass1", "dev42", "loki9", "code7", "hack5"},
        {"sharp1", "java42", "luca99", "crypt0", "l33t42"},
        {"l0k1dev", "sh4rp42", "c0d3r99", "h4ck3r1", "d3v1337"},
    };

    public static void main(String[] args) {
        System.out.println("=== CodeBreaker by lowkyloki ===");
        System.out.println("errate das passwort anhand der hinweise");
        System.out.println("tipp: 'hinweis' eingeben fuer extra hilfe (kostet einen versuch)");
        System.out.println();
        System.out.println("enter druecken zum starten...");
        scanner.nextLine();

        while (true) {
            boolean gewonnen = spieleLevel();

            if (!gewonnen) {
                System.out.println("game over! du hast level " + level + " nicht geschafft");
                System.out.println("punkte gesamt: " + punkte);
                System.out.print("nochmal? (ja/nein): ");
                String antwort = scanner.nextLine().trim().toLowerCase();
                if (antwort.equals("ja") || antwort.equals("j")) {
                    level = 1;
                    punkte = 0;
                } else {
                    System.out.println("tschuess!");
                    break;
                }
            } else {
                level++;
            }
        }
    }

    static boolean spieleLevel() {
        String passwort = getPasswort(level);
        int maxVersuche = getVersuche(level);
        int versuche = 0;

        System.out.println();
        System.out.println("--- LEVEL " + level + " ---");
        System.out.println("versuche: " + maxVersuche);
        System.out.println();

        boolean[] verraten = initVerraten(passwort, level);
        zeigeHinweise(passwort, level, verraten);

        while (versuche < maxVersuche) {
            int uebrig = maxVersuche - versuche;
            System.out.println();
            System.out.println("noch " + uebrig + " versuche uebrig");
            System.out.print("eingabe: ");
            String eingabe = scanner.nextLine().trim();

            if (eingabe.equals("hinweis") || eingabe.equals("hint")) {
                // naechstes unbekanntes zeichen verraten
                int naechste = -1;
                for (int i = 0; i < passwort.length(); i++) {
                    if (!verraten[i]) {
                        naechste = i;
                        break;
                    }
                }
                if (naechste == -1) {
                    System.out.println("keine hinweise mehr, du kennst schon alles");
                } else {
                    verraten[naechste] = true;
                    System.out.println("hinweis: position " + (naechste + 1) + " ist '" + passwort.charAt(naechste) + "'");
                    // muster anzeigen
                    String muster = "";
                    for (int i = 0; i < passwort.length(); i++) {
                        if (verraten[i]) muster += passwort.charAt(i);
                        else muster += "_";
                    }
                    System.out.println("muster: " + muster);
                }
                versuche++;
                continue;
            }

            if (eingabe.equals(passwort)) {
                int bonus = uebrig * 10;
                int gewonnen = level * 100 + bonus;
                punkte += gewonnen;
                System.out.println("richtig! passwort war: " + passwort);
                System.out.println("+" + gewonnen + " punkte (gesamt: " + punkte + ")");
                System.out.println("weiter zu level " + (level + 1));
                return true;
            } else {
                System.out.println("falsch!");
                // nach jedem 2. falschen versuch positionstipp
                if (versuche % 2 == 1) {
                    String tipp = "";
                    int len = Math.min(passwort.length(), eingabe.length());
                    for (int i = 0; i < len; i++) {
                        if (passwort.charAt(i) == eingabe.charAt(i)) tipp += "[O]";
                        else tipp += "[X]";
                    }
                    for (int i = len; i < passwort.length(); i++) tipp += "[ ]";
                    System.out.println("positionen: " + tipp);
                    System.out.println("[O]=richtig [X]=falsch [ ]=fehlt");
                }
            }
            versuche++;
        }

        System.out.println("keine versuche mehr. das passwort war: " + passwort);
        return false;
    }

    static boolean[] initVerraten(String passwort, int level) {
        boolean[] verraten = new boolean[passwort.length()];
        if (level <= 2) {
            verraten[0] = true;
        } else if (level <= 4) {
            verraten[0] = true;
            verraten[passwort.length() - 1] = true;
        } else if (level <= 6) {
            verraten[0] = true;
            verraten[passwort.length() / 2] = true;
            verraten[passwort.length() - 1] = true;
        } else if (level <= 9) {
            verraten[0] = true;
        }
        return verraten;
    }

    static void zeigeHinweise(String passwort, int level, boolean[] verraten) {
        boolean hatZahlen = false;
        boolean hatBuchstaben = false;
        int ziffernsumme = 0;

        for (int i = 0; i < passwort.length(); i++) {
            char c = passwort.charAt(i);
            if (Character.isDigit(c)) {
                hatZahlen = true;
                ziffernsumme += c - '0';
            }
            if (Character.isLetter(c)) hatBuchstaben = true;
        }

        System.out.println("hinweise:");
        System.out.println("  laenge: " + passwort.length());
        System.out.println("  zahlen: " + (hatZahlen ? "ja" : "nein"));
        System.out.println("  buchstaben: " + (hatBuchstaben ? "ja" : "nein"));
        if (hatZahlen) System.out.println("  ziffernsumme: " + ziffernsumme);

        String muster = "";
        for (int i = 0; i < passwort.length(); i++) {
            if (verraten[i]) muster += passwort.charAt(i);
            else muster += "_";
        }
        System.out.println("  muster: " + muster);
    }

    static String getPasswort(int level) {
        if (level <= 5) {
            int tier = Math.min(level - 1, passwoerter.length - 1);
            return passwoerter[tier][random.nextInt(passwoerter[tier].length)];
        } else {
            // ab level 6 zufaellig generieren
            int laenge = Math.min(4 + (level / 2), 12);
            String zeichen = "0123456789abcdefghijklmnopqrstuvwxyz";
            if (level >= 8) zeichen += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            if (level >= 12) zeichen += "!@#$";
            String pw = "";
            for (int i = 0; i < laenge; i++) {
                pw += zeichen.charAt(random.nextInt(zeichen.length()));
            }
            return pw;
        }
    }

    static int getVersuche(int level) {
        if (level <= 2) return 8;
        if (level <= 4) return 6;
        if (level <= 7) return 5;
        if (level <= 10) return 4;
        return 3;
    }
}
