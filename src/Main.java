/*Si vuole realizzare in Java un’applicazione per la gestione di un parcheggio multipiano. L’applicazione deve essere
        usata sia dagli utenti del parcheggio (procedure di ingresso e uscita, verifica esistenza posto libero, calcolo
        del costo etc.) che al gestore (statistiche sull’occupazione, calcolo fatturato etc.). Inoltre deve essere possibile
        esportare dati su file di testo ed è necessario implementare una persistenza dei dati mediante serializzazione. Scrivere
        un programma di test delle classi realizzate con interfaccia utente (testuale) utilizzabile.


        Suggerimenti: Un Piano è caratterizzato da un numero (un intero che identifica il piano), una lista di scontrini (relativi
        ai pagamenti per parcheggi effettuati in quel piano) e il numero di posti disponibili il quale viene decrementato ogni
        volta che una macchina occupa un posto nel piano e incrementato ogni volta che una macchina libera un posto nel
        piano. All’ingresso, all’automobilista viene indicato il piano in cui dovrà recarsi (che è quello con più posti disponibili).
        Uno Scontrino è caratterizzato da una data (un oggetto di tipo Data da implementare), un orario di arrivo (un oggetto
        di tipo Orario da implementare), un orario di uscita (un oggetto di tipo Orario), e da un prezzo pagato. Il prezzo è pari
        ad un Euro per ogni ora o frazione di ora di durata della permanenza all’interno del parcheggio. Una Data è caratterizzata
        da tre interi corrispondenti a giorno, mese e anno (oppure si usi la classe Date di Java). Un Orario è caratterizzato da
        due interi, uno corrispondente all’ora ed uno ai minuti. Oltre alle classi Piano, Scontrino, Data, Orario, potrebbe essere
        utile una classe GestioneParcheggio per la gestione delle informazioni complessive che comprende una opportuna
        collezione di riferimenti ad oggetti Piano ed implementa una serie di metodi utili.*/




import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class GestioneParcheggio{
    private static ArrayList<Piano> piano;
    public GestioneParcheggio() {
        piano = new ArrayList<>();
    }
    public static class Piano implements Comparable<Piano>{
        protected int COD;
        protected ArrayList<Scontrino> scontrini;
        protected int ptot;
        protected int pdis;
        public Piano(int COD, int ptot) {
            this.COD = COD;
            scontrini = new ArrayList<>();
            this.ptot = ptot;
            pdis = CalcoloPosti(ptot, scontrini);
        }
        public int CalcoloPosti(int ptot, ArrayList<Scontrino> s) {
            int posti;
            if (!(s.isEmpty())) posti = ptot - s.size();
            else posti = ptot;
            return posti;
        }
        @Override
        public String toString() {
            return "COD: " +COD+ " PTOT: " +ptot+ " PDIS: " +pdis;
        }
        @Override
        public int compareTo(Piano p) {
            return Integer.compare(pdis, p.pdis);
        }
    }
    public static class Scontrino implements Comparable<Scontrino>{
        protected int dest;
        protected int num;
        protected LocalDate date;
        protected LocalTime OraA;
        protected LocalTime OraU;
        protected int prezzo;
        public Scontrino(int num,LocalDate date, LocalTime oraA, LocalTime oraU) {
            dest = AssegnaPiano();
            this.num = num;
            this.date = date;
            this.OraA = oraA;
            this.OraU = oraU;
            prezzo = CalcoloPrezzo(OraA, OraU);
        }
        public int AssegnaPiano() {
            piano.sort(Piano::compareTo);
            Collections.reverse(piano);
            return piano.get(0).COD;
        }
        public int CalcoloPrezzo(LocalTime OraA, LocalTime OraU){
            int prezzo;
            if(OraA.getHour() > OraU.getHour()) prezzo = OraU.getHour() + 24-OraA.getHour();
            else prezzo = OraU.getHour() - OraA.getHour();
            if(OraU.getMinute() > OraA.getMinute()) prezzo += 1;
            return prezzo;
        }
        @Override
        public String toString() {
            return "NUM: " +num+ " DATA: " +date+ " DALLE: " +OraA+ " ALLE: " +OraU+ " COSTO: " +prezzo+ " EURO ";
        }
        @Override
        public int compareTo(Scontrino s) {
            return Integer.compare(prezzo, s.prezzo);
        }
    }
    public void AddPiano(Piano p) {
        piano.add(p);
    }
    public void AddScontrino(Scontrino s){
        for(Piano p : piano){
            if (s.dest == p.COD) {
                p.scontrini.add(s);
                p.pdis--;
            }
        }
    }
    public void RimuoviScontrino(Scontrino sctr){
        for(Piano p : piano) {
            boolean b = p.scontrini.removeIf(s -> s.equals(sctr));
            if(b) {
                p.pdis++;
                System.out.println("SCONTRINO RIMOSSO CORRETTAMENTE");
                break;
            }
        }
    }
    public void RimuoviPiano(Piano p) { piano.remove(p); }
    public void StampaPiani(){ piano.iterator().forEachRemaining(System.out::println); }
    public void GetPosti() {
        int tot = 0;
        int i = 1;
        for(Piano p : piano){
            System.out.println("IL PIANO N*: " +i+ " HA " +p.pdis+ " POSTI DISPONIBILI");
            tot += p.pdis;
            i++;
        }
        System.out.println("PER UN TOTALE DI: " +tot);
    }
    public void StampaScontrini(){ piano.iterator().forEachRemaining(p -> p.scontrini.iterator().forEachRemaining(System.out::println)); }
    public void StampaPrezzo(){
        Scanner sc = new Scanner(System.in);
        System.out.print("DIGITA IL NUMERO DI SCONTRINO: ");
        int num = sc.nextInt();
        piano.iterator().forEachRemaining(p -> { p.scontrini.iterator().forEachRemaining(s -> {
                if(s.num == num) System.out.println("LO SCONTRINO NUM*: " +s.num+ " DALLE " +s.OraA+ " ALLE " +s.OraU+ " HA PREZZO: " +s.prezzo+ " EURO"); });
        });
    }
}

public class Main {
    public static void Inizializzazione(ArrayList<GestioneParcheggio.Piano> piani, GestioneParcheggio gestione){
        Scanner sc = new Scanner(System.in);
        int cod, ptot;
        System.out.print("QUANTI PIANI HA LA STRUTTURA? ");
        int n = sc.nextInt();
        for(int i = 0; i<n; i++){
            System.out.print("NUMERO PIANO? ");
            cod = sc.nextInt();
            System.out.print("NUMERO POSTI? ");
            ptot = sc.nextInt();
            GestioneParcheggio.Piano p = new GestioneParcheggio.Piano(cod,ptot);
            piani.add(p);
        }
        piani.iterator().forEachRemaining(gestione::AddPiano);
    }
    public static int Menu(){
        Scanner sc = new Scanner(System.in);
        int index;

        do{
            System.out.println("DIGITARE UN NUMERO DAL MENU' PER SCEGLIERE UN OPZIONE [0 - 12]");
            System.out.println("0. CREA PIANO");
            System.out.println("1. RIMUOVI PIANO");
            System.out.println("2. STAMPA PIANI");
            System.out.println("3. CREA TICKET");
            System.out.println("4. RIMUOVI TICKET");
            System.out.println("5. STAMPA TICKET");
            System.out.println("6. STAMPA PREZZO TICKET");
            System.out.println("7. POSTI DISPONIBILI");
            System.out.println("8. STAMPA GUADAGNI TOTALI");
            System.out.println("9. STAMPA GUADAGNI DI UN GIORNO");
            System.out.println("10. STAMPA TUTTI I TICKET");
            System.out.println("11. STAMPA LA CLASSIFICA DEGLI SCONTRINI PIU' COSTOSI");
            System.out.println("12. STAMPA IL PIANO CON PIU' POSTI LIBERI");
            index = sc.nextInt();
            if(index > 12 || index < 0) System.out.println("SELEZIONE NON VALIDA, RIPROVARE");
        } while(index > 12 || index < 0);
        return index;
    }
    public static void Case0(ArrayList<GestioneParcheggio.Piano> piani, GestioneParcheggio gestione, ArrayList<GestioneParcheggio.Scontrino> scontrini){
        Scanner sc = new Scanner(System.in);
        boolean b;
        System.out.print("NUMERO DEL PIANO? ");
        int COD = sc.nextInt();
        System.out.print("NUMERO POSTI DEL PIANO? ");
        int ptot = sc.nextInt();
        GestioneParcheggio.Piano p;
        do{
            p = new GestioneParcheggio.Piano(COD,ptot);
            b = UnivocoP(p, piani);
            if(b) {
                piani.add(p);
                gestione.AddPiano(p);
            } else {
                System.out.print("IL PIANO AVENTE CODICE: " +COD+ " E' GIA' ESISTENTE, DIGITARE UN NUOVO CODICE: ");
                COD = sc.nextInt();
            }
        }while(!b);
        System.out.println("PIANO AGGIUNTO");
        if(!(scontrini.isEmpty())){
            for(GestioneParcheggio.Scontrino s : scontrini) {
                if(s.dest == p.COD) {
                    p.scontrini.add(s);
                    p.pdis -= 1;
                }
            }
        }
    }
    public static boolean UnivocoP(GestioneParcheggio.Piano piano, ArrayList<GestioneParcheggio.Piano> piani){
        for(GestioneParcheggio.Piano p : piani){
            if(p.COD == piano.COD) return false;
        }
        return true;
    }
    public static boolean UnivocoS(GestioneParcheggio.Scontrino scontrino, ArrayList<GestioneParcheggio.Scontrino> scontrini, ArrayList<GestioneParcheggio.Scontrino> storico){
        for(GestioneParcheggio.Scontrino s : scontrini){
            if(s.num == scontrino.num) return false;
        }
        for(GestioneParcheggio.Scontrino s : storico){
            if(s.num == scontrino.num) return false;
        }
        return true;
    }
    public static void Case1(GestioneParcheggio gestione, ArrayList<GestioneParcheggio.Piano> piani){
        Scanner sc = new Scanner(System.in);
        System.out.println("CHE PIANO VUOI RIMUOVERE? ");
        int num = sc.nextInt();
        for(GestioneParcheggio.Piano p : piani){
            if(p.COD == num){
                gestione.RimuoviPiano(p);
                System.out.println("PIANO RIMOSSO CORRETTAMENTE");
            }
        }
        piani.removeIf(p -> p.COD == num);
    }
    public static void Case3(GestioneParcheggio gestione, ArrayList<GestioneParcheggio.Scontrino> scontrini, ArrayList<GestioneParcheggio.Scontrino> storico){
        Scanner sc = new Scanner(System.in);
        boolean b;

        System.out.print("INSERISCI NUMERO SCONTRINO: ");
        int num = sc.nextInt();
        LocalTime OraA = getOrario("INGRESSO");
        LocalTime OraU = getOrario("USCITA");
        GestioneParcheggio.Scontrino s;
        do{
            s = new GestioneParcheggio.Scontrino(num,LocalDate.now(),OraA,OraU);
            b = UnivocoS(s,scontrini,storico);
            if(b){
                scontrini.add(s);
                storico.add(s);
                gestione.AddScontrino(s);
                System.out.println("SCONTRINO AGGIUNTO ");
            } else{
                System.out.print("LO SCONTRINO CON NUMERO: " +num+ " E' GIA' PRESENTE, INSERIRE NUOVO NUMERO: ");
                num = sc.nextInt();
            }
        } while(!b);
    }
    public static void Case4(GestioneParcheggio gestione, ArrayList<GestioneParcheggio.Scontrino> scontrini){
        Scanner sc = new Scanner(System.in);
        System.out.println("DIGITARE NUMERO DI SCONTRINO DA RIMUOVERE: ");
        int num = sc.nextInt();
        scontrini.iterator().forEachRemaining(s -> { if(s.num == num) gestione.RimuoviScontrino(s); });
        scontrini.removeIf(s -> s.num == num);
    }
    public static LocalTime getOrario(String s){
        Scanner sc = new Scanner(System.in);
        System.out.print("INSERISCI ORA ["+s+"]: ");
        int h = sc.nextInt();
        System.out.print("INSERISCI MINUTI ["+s+"]: ");
        int m = sc.nextInt();
        return LocalTime.of(h,m);
    }
    public static void Case8(ArrayList<GestioneParcheggio.Scontrino> storico){
        int[] gtot = new int[1];
        storico.iterator().forEachRemaining(s -> gtot[0] += s.prezzo);
        System.out.println("IN TOTALE, DALLA SUA APERTURA, LA STRUTTURA HA GUADAGNATO: " +gtot[0]+ " EURO");
    }
    public static void Case9(ArrayList<GestioneParcheggio.Scontrino> storico){
        Scanner sc = new Scanner(System.in);
        int[] ggiorno = new int[1];
        System.out.print("GIORNO: ");
        int d = sc.nextInt();
        System.out.print("MESE: ");
        int m = sc.nextInt();
        System.out.print("ANNO: ");
        int y = sc.nextInt();
        LocalDate date = LocalDate.of(y,m,d);
        storico.iterator().forEachRemaining(s -> { if(s.date.equals(date)) ggiorno[0] += s.prezzo; });
        System.out.println("NEL GIORNO: " +date+ " LA STRUTTURA HA GUADAGNATO: " +ggiorno[0]+ " EURO");
    }
    public static void Case10(ArrayList<GestioneParcheggio.Scontrino> storico){
        storico.iterator().forEachRemaining(System.out::println);
    }
    public static void Case11(ArrayList<GestioneParcheggio.Scontrino> storico){
        Scanner sc = new Scanner(System.in);
        int n;
            do{
                System.out.print("QUANTI SCONTRINI SI VUOLE STAMPARE? ");
                n = sc.nextInt();
                if(n > storico.size()){
                    System.out.println("NON SONO DISPONIBILI COSI' TANTI SCONTRINI, RIPROVARE...");
                }
            }while (n > storico.size());
            Collections.sort(storico);
            Collections.reverse(storico);
            for(int i = 0; i < n; i++){
                System.out.println((i+1)+ ". " +storico.get(i));
            }
    }
    public static void Case12(ArrayList<GestioneParcheggio.Piano> piani){
        int[] n = new int[1];
        Collections.sort(piani);
        Collections.reverse(piani);
        piani.iterator().forEachRemaining(p -> { if (piani.get(0).pdis == p.pdis) n[0]++; });
        if(n[0] > 1){
            System.out.println("I " +n[0]+ " PIANI CON PIU' POSTI DISPONIBILI SONO:");
            for(int i = 0; i < n[0]; i++) System.out.println(piani.get(i));
        }
        else System.out.println("IL PIANO CON PIU' POSTI DISPONIBILI E': " +piani.get(0));
    }
    public static boolean ControlloP(ArrayList<GestioneParcheggio.Piano> piani){
        if(piani.isEmpty()){
            System.out.println("OCCORRE PRIMA INIZIALIZZARE I PIANI:");
            System.out.println("INIZIALIZZAZIONE...");
            return false;
        } else return true;
    }
    public static boolean ControlloS(GestioneParcheggio gestione, ArrayList<GestioneParcheggio.Scontrino> scontrini, ArrayList<GestioneParcheggio.Scontrino> storico){
        Scanner sc = new Scanner(System.in);

        if(scontrini.isEmpty()){
            System.out.println("NESSUNO SCONTRINO RILEVATO");
            System.out.println("AGGIUNGERE UNO SCONTRINO? [SI/NO] ");
            String s = sc.next();
            if(s.equals("si")) Case3(gestione, scontrini, storico);
            return false;
        }
        else return true;
    }
    public static boolean ControlloST(GestioneParcheggio gestione, ArrayList<GestioneParcheggio.Scontrino> scontrini, ArrayList<GestioneParcheggio.Scontrino> storico){
        Scanner sc = new Scanner(System.in);

        if(scontrini.isEmpty()){
            System.out.println("NESSUNO SCONTRINO RILEVATO");
            System.out.println("AGGIUNGERE UNO SCONTRINO? [SI/NO] ");
            String s = sc.next();
            if(s.equals("si")) Case3(gestione, scontrini, storico);
            return false;
        }
        else return true;
    }
    public static void Read(File fn, GestioneParcheggio gestione, ArrayList<GestioneParcheggio.Piano> piani, ArrayList<GestioneParcheggio.Scontrino> scontrini, ArrayList<GestioneParcheggio.Scontrino> storico) throws IOException {
        FileReader fr = new FileReader(fn);
        BufferedReader br = new BufferedReader(fr);
        String test;
        while((test = br.readLine()) != null){
            String[] line = test.split("\\s");
            switch (line[0]) {
                case "COD:" -> {
                    GestioneParcheggio.Piano p = new GestioneParcheggio.Piano((Integer.parseInt(line[1])), (Integer.parseInt(line[3])));
                    gestione.AddPiano(p);
                    piani.add(p);
                }
                case "NUM:" -> {
                    GestioneParcheggio.Scontrino s = new GestioneParcheggio.Scontrino((Integer.parseInt(line[1])), (LocalDate.parse(line[3])), (LocalTime.parse(line[5])), (LocalTime.parse(line[7])));
                    gestione.AddScontrino(s);
                    scontrini.add(s);
                }
                case "-NUM:" -> {
                    GestioneParcheggio.Scontrino s = new GestioneParcheggio.Scontrino((Integer.parseInt(line[1])), (LocalDate.parse(line[3])), (LocalTime.parse(line[5])), (LocalTime.parse(line[7])));
                    storico.add(s);
                }
            }
        }
        br.close();
    }
    public static  void Update(File fn, ArrayList<GestioneParcheggio.Piano> piani, ArrayList<GestioneParcheggio.Scontrino> scontrini, ArrayList<GestioneParcheggio.Scontrino> storico) throws IOException {
        File ft = new File("temp.txt");
        FileWriter fw = new FileWriter(ft);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("NUMERO PIANI: " +piani.size());
        bw.newLine();
        bw.write("NUMERO SCONTRINI ATTIVI: " +scontrini.size());
        bw.newLine();
        bw.write("NUMERO SCONTRINI TOTALI: " +storico.size());
        bw.newLine();
        bw.write("-");
        bw.newLine();
        bw.write("LISTA PIANI: ");
        bw.newLine();
        for(GestioneParcheggio.Piano p : piani){
            bw.write("COD: " +p.COD+ " PTOT: " +p.ptot+ " PDIS: " +p.pdis);
            bw.newLine();
        }
        bw.write("-");
        bw.newLine();
        bw.write("LISTA SCONTRINI ATTIVI: ");
        bw.newLine();
        for(GestioneParcheggio.Scontrino s : scontrini){
            bw.write("NUM: " +s.num+ " DATA: " +s.date+ " DALLE: " +s.OraA+ " ALLE: " +s.OraU);
            bw.newLine();
        }
        bw.write("-");
        bw.newLine();
        bw.write("LISTA SCONTRINI TOTALI: ");
        bw.newLine();
        for(GestioneParcheggio.Scontrino s : storico){
            bw.write("-NUM: " +s.num+ " DATA: " +s.date+ " DALLE: " +s.OraA+ " ALLE: " +s.OraU);
            bw.newLine();
        }
        bw.close();
        fn.delete();
        ft.renameTo(fn);
    }
    public static void Write(File fn, ArrayList<GestioneParcheggio.Piano> piani, ArrayList<GestioneParcheggio.Scontrino> scontrini, ArrayList<GestioneParcheggio.Scontrino> storico) throws IOException {
        FileWriter fw = new FileWriter(fn);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("NUMERO PIANI: " +piani.size());
        bw.newLine();
        bw.write("NUMERO SCONTRINI ATTIVI: " +scontrini.size());
        bw.newLine();
        bw.write("NUMERO SCONTRINI TOTALI: " +storico.size());
        bw.newLine();
        bw.write("-");
        bw.newLine();
        bw.write("LISTA PIANI: ");
        bw.newLine();
        for(GestioneParcheggio.Piano p : piani){
            bw.write("COD: " +p.COD+ " PTOT: " +p.ptot+ " PDIS: " +p.pdis);
            bw.newLine();
        }
        bw.write("-");
        bw.newLine();
        bw.write("LISTA SCONTRINI ATTIVI: ");
        bw.newLine();
        for(GestioneParcheggio.Scontrino s : scontrini){
            bw.write("NUM: " +s.num+ " DATA: " +s.date+ " DALLE: " +s.OraA+ " ALLE: " +s.OraU);
            bw.newLine();
        }
        bw.write("-");
        bw.newLine();
        bw.write("LISTA SCONTRINI TOTALI: ");
        bw.newLine();
        for(GestioneParcheggio.Scontrino s : storico){
            bw.write("-NUM: " +s.num+ " DATA: " +s.date+ " DALLE: " +s.OraA+ " ALLE: " +s.OraU);
            bw.newLine();
        }
        bw.close();
    }
    public static String SwitchCase(int index, GestioneParcheggio gestione, ArrayList<GestioneParcheggio.Piano> piani, ArrayList<GestioneParcheggio.Scontrino> scontrini, ArrayList<GestioneParcheggio.Scontrino> storico){
        Scanner sc = new Scanner(System.in);

        String strg;
        boolean c;

        switch (index) {
            //CREA UN NUOVO PIANO
            case 0 -> Case0(piani, gestione, scontrini);
            //RIMUOVE UN PIANO A SCELTA
            case 1 -> {
                c = ControlloP(piani);
                if(!c) Inizializzazione(piani,gestione);
                else Case1(gestione,piani);
            }
            //STAMPA TUTTI I DATI DEI PIANI
            case 2 -> {
                c = ControlloP(piani);
                if(!c) Inizializzazione(piani,gestione);
                gestione.StampaPiani();
            }
            //CREA UN NUOVO SCONTRINO
            case 3 -> {
                c = ControlloP(piani);
                if(!c) Inizializzazione(piani,gestione);
                Case3(gestione, scontrini, storico);
            }
            //RIMUOVE UNO SCONTRINO A SCELTA
            case 4 -> {
                c = ControlloS(gestione,scontrini,storico);
                if(c) Case4(gestione, scontrini);
            }
            //STAMPA TUTTI I DATI DEGLI SCONTRINI
            case 5 -> {
                ControlloS(gestione,scontrini,storico);
                gestione.StampaScontrini();
            }
            //STAMPA IL PREZZO DI UNO SCONTRINO A SCELTA
            case 6 -> {
                ControlloS(gestione,scontrini,storico);
                gestione.StampaPrezzo();
            }
            //STAMPA TUTTI I POSTI DISPONIBILI DEI PIANI, IN LISTA, SEGUITI DAL NUMERO DI POSTI TOTALI
            case 7 -> {
                c = ControlloP(piani);
                if(!c) Inizializzazione(piani,gestione);
                gestione.GetPosti();
            }
            //STAMPA I GUADAGNI TOTALI
            case 8 -> {
                ControlloST(gestione,scontrini,storico);
                Case8(storico);
            }
            //STAMPA I GUADAGNI DI UN GIORNO A SCELTA
            case 9 -> {
                ControlloST(gestione,scontrini,storico);
                Case9(storico);
            }
            //STAMPA LA LISTA DI TUTTI GLI SCONTRINI
            case 10 -> {
                ControlloST(gestione,scontrini,storico);
                Case10(storico);
            }
            //STAMPA LA CLASSIFICA DEGLI N SCONTRINI PIU' COSTOSI
            case 11 -> {
                c = ControlloST(gestione,scontrini,storico);
                if (c) Case11(storico);
            }
            case 12 -> {
                c = ControlloP(piani);
                if(!c) Inizializzazione(piani,gestione);
                else Case12(piani);
            }
        }
        System.out.println("IMMETTERE UN'ALTRA OPZIONE? [SI/NO]: ");
        strg = sc.next();
        return strg;
    }

    public static void main(String[] args) {

        GestioneParcheggio gestione = new GestioneParcheggio();
        ArrayList<GestioneParcheggio.Piano> piani = new ArrayList<>();
        ArrayList<GestioneParcheggio.Scontrino> scontrini = new ArrayList<>();

        //STORICO E' LA LISTA COMPLETA DEGLI SCONTRINI
        ArrayList<GestioneParcheggio.Scontrino> storico = new ArrayList<>();

        int index;
        String strg;

        String path = "GP.txt";
        try {
            File fn = new File(path);
            if(fn.exists()){
                //LETTURA DA FILE
                Read(fn,gestione,piani,scontrini,storico);
                do{
                    index = Menu();
                    strg = SwitchCase(index,gestione,piani,scontrini,storico);
                } while(strg.equals("si"));
                //AGGIORNAMENTO FILE
                Update(fn,piani,scontrini,storico);
            } else if (fn.createNewFile()) {
                //INIZIALIZZAZIONE PIANI
                Inizializzazione(piani,gestione);
                do {
                    index = Menu();
                    strg = SwitchCase(index,gestione,piani,scontrini,storico);
                } while(strg.equals("si"));
                //SCRITTURA SU FILE
                Write(fn,piani,scontrini,storico);
            } else System.out.println("IL FILE NON PUO' ESSERE CREATO");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}