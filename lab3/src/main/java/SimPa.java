import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class SimPa {

    public static void main(String[] args) throws IOException {

        List<String> ucitajLinije = new ArrayList<>();
        String jednaLinija;

        //za citanje cemo koristit buffered reader
        //BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]));
        //BufferedReader bufferedReader = new BufferedReader(new FileReader("primjeri/test02" +"/primjer.in"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            jednaLinija = bufferedReader.readLine();
            if (jednaLinija == null) {
                break;
            }
            ucitajLinije.add(jednaLinija);
        }

        String[] ulazniNizovi = ucitajLinije.get(0).split("\\|");

        // sa readera cemo citat liniju po liniju (stavit cemo sve u listu i dohvacat red po red s get(i)
        List<List<String>> listaUlaznihZnakova = new LinkedList<>();

        //1.redak - ulazni nizovi
        for (int i = 0; i < ulazniNizovi.length; i++) {
            String[] jedanNizString = ulazniNizovi[i].split(",");
            List<String> jedanNizZnakova = Arrays.asList(jedanNizString);

            listaUlaznihZnakova.add(jedanNizZnakova);
        }
        //trenutno imamo listu u listi u kojima su ulazni nizovi

        //2.redak - skup stanja - lista
        String[] skupStanjaString = ucitajLinije.get(1).split(",");
        List<String> skupStanja = Arrays.asList(skupStanjaString);

        //3.redak - ulazni znakovi - niz
        String[] ulazniZnakovi = ucitajLinije.get(2).split(",");

        //4.redak - znakovi stoga
        String[] znakoviStoga = ucitajLinije.get(3).split(",");

        //5.redak - prihvatljiva stanja - niz
        String[] prihvatljivaStanjaString = ucitajLinije.get(4).split(",");
        List<String> prihvatljivaStanja = Arrays.asList(prihvatljivaStanjaString);

        //6. redak - pocetno stanje
        String pocetnoStanje = ucitajLinije.get(5);

        //7.redak - pocetni znak stoga
        String pocetniZnakStoga = ucitajLinije.get(6);

        //8.redak i ostali - prijelazi

        //prijelazi zadani s: trenutnoStanje,ulazniZnak,znakStoga -> >novoStanje,nizZnakovaStoga
        Map<List<String>, String[]> zadaniPrijelazi = new HashMap<>();

        for (int i = 7; i < ucitajLinije.size(); i++) {
            //rastavi redak na dvi strane s obzirom na strelicu
            String[] jedanRedak = ucitajLinije.get(i).split("->");
            String[] stanjeSimbolStogArray = jedanRedak[0].split(",");
            List<String> stanjeSimbolStog = Arrays.asList(stanjeSimbolStogArray);

            String[] stanjeStog = jedanRedak[1].split(",");

            //formiraj mapu od dobivenih varijabli
            zadaniPrijelazi.put(stanjeSimbolStog, stanjeStog);
        }

        //definirat cemo varijable di pamtimo znak stoga i stanje
        String trenutniZnakStoga;
        String trenutnoStanje;

        StringBuilder stringBuilder = new StringBuilder();

        //zastavica kojom gledamo imamo li prijelaz potisnot automata za trenutni ulazni znak
        boolean postojiPrijelazPA;

        for (List<String> ulazLista : listaUlaznihZnakova) {

            //pocetak izlaza je ovakav uvijek:
            stringBuilder.append(pocetnoStanje);
            stringBuilder.append("#");
            stringBuilder.append(pocetniZnakStoga);
            stringBuilder.append("|");

            trenutnoStanje = pocetnoStanje;
            trenutniZnakStoga = pocetniZnakStoga;

            //List<String> epsilonPrijelazi = new LinkedList<>();
            List<String> listaStanja = new LinkedList<>();
            List<String> listaZnakovaStoga = new LinkedList<>();

            listaStanja.add(trenutnoStanje);
            listaZnakovaStoga.add(trenutniZnakStoga);

            while (true) {
                //if (prihvatljivaStanja.contains(trenutnoStanje)) break;

                char znakStogaTr = trenutniZnakStoga.charAt(0);

                List<String> stanjeSimbolStog = new ArrayList<>();
                stanjeSimbolStog.add(trenutnoStanje);
                stanjeSimbolStog.add("$");
                stanjeSimbolStog.add(Character.toString(znakStogaTr));

                if (zadaniPrijelazi.containsKey(stanjeSimbolStog)) {
                    //dohvati trenutni znak stoga...
                    String znakStoga = zadaniPrijelazi.get(stanjeSimbolStog)[1];
                    if (listaZnakovaStoga.contains("$"))
                        trenutniZnakStoga = trenutniZnakStoga.substring(1);
                    else
                        trenutniZnakStoga = znakStoga + trenutniZnakStoga.substring(1);

                    //dohvati trenutno stanje...
                    trenutnoStanje = zadaniPrijelazi.get(stanjeSimbolStog)[0];
                    listaStanja.add(trenutnoStanje);
                    listaZnakovaStoga.add(trenutniZnakStoga);

                    //dodaj ih za ispis
                    stringBuilder.append(trenutnoStanje);
                    stringBuilder.append("#");
                    stringBuilder.append(trenutniZnakStoga);
                    stringBuilder.append("|");

                } else {
                    break;
                }
            }

            //ubaci epsilon prijelaze u listu stanja i listu znakova stoga
            //trenutnaStanjaLista.addAll(epsilonPrijelazi);
            //trenutnaStanjaLista = sortirajListu(trenutnaStanjaLista);

            trenutnoStanje = getLast(listaStanja);
            trenutniZnakStoga = getLast(listaZnakovaStoga);

            for (int i = 0; i < ulazLista.size(); i++) {

                String jedanZnak = ulazLista.get(i);
                //na pocetku postavi zastavicu prijelza na false i minjaj je ako naides na prijelaza
                postojiPrijelazPA = false;

                char znakStogaChar = trenutniZnakStoga.charAt(0);
                List<String> stanjeSimbolStog = new ArrayList<>();
                stanjeSimbolStog.add(trenutnoStanje);
                stanjeSimbolStog.add(jedanZnak);
                stanjeSimbolStog.add(Character.toString(znakStogaChar));

                if (zadaniPrijelazi.containsKey(stanjeSimbolStog)) {
                    String znakStogaTr = zadaniPrijelazi.get(stanjeSimbolStog)[1];

                    if (znakStogaTr.equals("$"))
                        trenutniZnakStoga = trenutniZnakStoga.substring(1);
                    else
                        trenutniZnakStoga = znakStogaTr + trenutniZnakStoga.substring(1);

                    //ucitaj trenutno stanje...
                    trenutnoStanje = zadaniPrijelazi.get(stanjeSimbolStog)[0];

                    //dodaj stanje u znak stoga u njihove liste
                    listaZnakovaStoga.add(trenutniZnakStoga);
                    listaStanja.add(trenutnoStanje);

                    //dodaj ih u stringbuilder prema pravilu
                    stringBuilder.append(trenutnoStanje);
                    stringBuilder.append("#");
                    stringBuilder.append(trenutniZnakStoga);
                    stringBuilder.append("|");

                    while (true) {
                        if (i == ulazLista.size() - 1 && prihvatljivaStanja.contains(trenutnoStanje)) break;

                        char znakStoga = trenutniZnakStoga.charAt(0);

                        //stanjeSimbolStog.clear();
                        List<String> stanjeSimbolStogtrojka = new ArrayList<>();
                        stanjeSimbolStogtrojka.add(trenutnoStanje);
                        stanjeSimbolStogtrojka.add("$");
                        stanjeSimbolStogtrojka.add(Character.toString(znakStoga));

                        if (zadaniPrijelazi.containsKey(stanjeSimbolStogtrojka)) {
                            //dohvati trenutni znak stoga...
                            String znakStogaTr2 = zadaniPrijelazi.get(stanjeSimbolStogtrojka)[1];
                            if (listaZnakovaStoga.contains("$"))
                                trenutniZnakStoga = trenutniZnakStoga.substring(1);
                            else
                                trenutniZnakStoga = znakStogaTr2 + trenutniZnakStoga.substring(1);

                            //dohvati trenutno stanje...
                            trenutnoStanje = zadaniPrijelazi.get(stanjeSimbolStogtrojka)[0];
                            listaStanja.add(trenutnoStanje);
                            listaZnakovaStoga.add(trenutniZnakStoga);

                            //dodaj ih za ispis
                            stringBuilder.append(trenutnoStanje);
                            stringBuilder.append("#");
                            stringBuilder.append(trenutniZnakStoga);
                            stringBuilder.append("|");

                        }
                        else {
                            break;
                        }
                    }

                    trenutnoStanje = getLast(listaStanja);
                    trenutniZnakStoga = getLast(listaZnakovaStoga);

                    //postavi zastavicu da postoji prijelaz PA
                    postojiPrijelazPA = true;
                }

                //Ako ulazni niz nije obraden do kraja, a ne postoji prijelaz potisnog automata za trenutni ulazni znak,
                // trenutno stanje i znak na vrhu stoga, automat treba ispisati fail umjesto para stanje#znak

                if (postojiPrijelazPA == false) {
                    stringBuilder.append("fail");
                    trenutnoStanje = null;
                    stringBuilder.append("|");
                    //...nakon cega automat zavrsava s radom
                    break;
                }
            }

            //dodajemo 1 ili 0 u ovisnosti o prihvatljivosti
            if (prihvatljivaStanja.contains(trenutnoStanje))
                stringBuilder.append("1");
            else
                stringBuilder.append("0");

            //zadnji znak je uvik enter
            stringBuilder.append("\n");
        }
        if (args.length == 2) {
            String output = Files.readString(Paths.get(args[1]));
            System.out.println(output.equals(stringBuilder.toString()));
        } else {
            System.out.println(stringBuilder);
        }
    }

    private static String getLast(List<String> list) {
        return list.get(list.size() - 1);
    }
}
