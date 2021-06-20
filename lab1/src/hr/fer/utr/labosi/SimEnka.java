package hr.fer.utr.labosi;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class SimEnka {

    public static void main(String[] args) throws IOException {

        List<String> ucitajLinije = new ArrayList<>();
        String jednaLinija;

        //za citanje cemo koristit buffered reader
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

        //3.redak - simboli abecede - niz
        String[] simboliAbecede = ucitajLinije.get(2).split(",");

        //4.redak - prihvatljiva stanja - niz
        String[] prihvatljivaStanja = ucitajLinije.get(3).split(",");

        //5. redak - pocetno stanje
        String pocetnoStanje = ucitajLinije.get(4);

        //6.redak i ostali - prijelazi

        Map<Map<String, String>, List<String>> zadaniPrijelazi = new HashMap<>();

        for (int i = 5; i < ucitajLinije.size(); i++) {
            //rastavi redak na dvi strane s obzirom na strelicu
            String[] jedanRedak = ucitajLinije.get(i).split("->");
            String[] lijevaStrana = jedanRedak[0].split(",");
            String[] desnaString = jedanRedak[1].split(",");
            List<String> desnaStrana = Arrays.asList(desnaString);

            //ratsavi livu stranu s obziron na zarez
            String trenutnoStanje = lijevaStrana[0];
            String poslaniZnak = lijevaStrana[1];

            //formiraj unutarnju mapu
            Map<String, String> unutaranjaMapa = new LinkedHashMap<>();
            unutaranjaMapa.put(trenutnoStanje, poslaniZnak);

            //formiraj mapu od dobivenih varijabli
            zadaniPrijelazi.put(unutaranjaMapa, desnaStrana);
        }

        List<String> trenutnaStanjaLista = new LinkedList<>();

        StringBuilder stringBuilder = new StringBuilder();

        for (List<String> ulazLista : listaUlaznihZnakova) {
            //prvo makni prosla stanja
            trenutnaStanjaLista.clear();
            // onda dodaj pocetno
            trenutnaStanjaLista.add(pocetnoStanje);

            Set<String> epsilonPrijelazi = new TreeSet<>();
            //prebacujemo listu u set jer nam on ide po redu
            Set<String> trenutnaStanjaSet = new TreeSet<>(trenutnaStanjaLista);

            funckijaZaEpsilone(trenutnaStanjaSet, zadaniPrijelazi, epsilonPrijelazi);

            //ubaci epsilon prijelaze u listu stanja i sortiraj ih
            trenutnaStanjaLista.addAll(epsilonPrijelazi);
            trenutnaStanjaLista = sortirajListu(trenutnaStanjaLista);

            for(String string: trenutnaStanjaLista){
                stringBuilder.append(string);
                stringBuilder.append(",");
            }
            //makni zadnji zarez
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append("|");

            for (int i = 0; i < ulazLista.size(); i++) {

                String jedanZnak = ulazLista.get(i);

                List<String> novaStanjaLista = new LinkedList<>();
                for (String string : trenutnaStanjaLista) {

                    Map<String, String > par = new TreeMap<>();
                    par.put(string, jedanZnak);

                    if (zadaniPrijelazi.containsKey(par)) {
                        //za zadani kljuc ucitaj stanja
                        List<String> trStanja = zadaniPrijelazi.get(par);

                        if (trStanja.get(0) == "#") continue;

                        epsilonPrijelazi = new TreeSet<>();
                        trenutnaStanjaSet = new TreeSet<>(trStanja);

                        //opet zovi funkcija da dohvatis epsilone
                        funckijaZaEpsilone(trenutnaStanjaSet, zadaniPrijelazi, epsilonPrijelazi);

                        //u listu novih stanja dodaj epsilon prijaze i trenutna stanja
                        novaStanjaLista.addAll(epsilonPrijelazi);
                        novaStanjaLista.addAll(trStanja);
                    }
                }
                //sortiraj listu stanja
                trenutnaStanjaLista = sortirajListu(novaStanjaLista);

                if (trenutnaStanjaLista.size() == 0) {
                    trenutnaStanjaLista.add("#");
                }

                for(String string: trenutnaStanjaLista){
                    stringBuilder.append(string);
                    stringBuilder.append(",");
                }
                //makni zadnji zarez
                stringBuilder.setLength(stringBuilder.length() - 1);

                if (i != ulazLista.size() - 1) {
                    stringBuilder.append("|");
                }
            }
            //zadnji znak je uvik enter
            stringBuilder.append("\n");
        }
        //ispisi na System.out
        System.out.println(stringBuilder);
    }

    //funckija za dohvat epsilona
    private static void funckijaZaEpsilone(Set<String> trenutnaStanjaSet, Map<Map<String, String>, List<String>> zadaniPrijelazi, Set<String> epsilonPrijelazi) {

        boolean jeliEpsilonDodan = false;

        for (String stringStanje : trenutnaStanjaSet) {

            //dodavanje znaka $ za epsilon prijelaze
            Map<String, String> par = new TreeMap<>();
            par.put(stringStanje, "$");


            if (zadaniPrijelazi.containsKey(par)) {
                //dohvati listu stanja po kljucu
                List<String> trenutnaStanjaLista2 = zadaniPrijelazi.get(par);

                if (trenutnaStanjaLista2.get(0) == "#") continue;

                jeliEpsilonDodan = jeliEpsilonDodan || epsilonPrijelazi.addAll(trenutnaStanjaLista2);
            }
        }

        if (jeliEpsilonDodan) {
            //napravi novu listu za rekurzivno pozivanje
            Set<String> novaTrenutnaStanjaSet = new TreeSet<>(epsilonPrijelazi);

            //pozovi rekurzivno fju
            funckijaZaEpsilone(novaTrenutnaStanjaSet, zadaniPrijelazi, epsilonPrijelazi);
        }
    }

    private static List<String> sortirajListu(List<String> lista){
        List<String> sortiranaLista = new LinkedList<>();
        sortiranaLista = lista
                .stream()
                .sorted()
                .distinct()
                .collect(Collectors.toList());
        return sortiranaLista;
    }
}


