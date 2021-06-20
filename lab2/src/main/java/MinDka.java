import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class MinDka {

    public static void main(String[] args) throws IOException {

        List<String> ucitajLinije = new ArrayList<>();
        String jednaLinija;

        //za citanje cemo koristit buffered reader
        //BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]));
        //BufferedReader bufferedReader = new BufferedReader(new FileReader("primjeri/test08/t.ul"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            jednaLinija = bufferedReader.readLine();
            if (jednaLinija == null) break;
            ucitajLinije.add(jednaLinija);
        }

        //2.redak - znakovi abecede
        String[] znakoviAbecede = ucitajLinije.get(1).split(",");

        //3.redak - prihvatljiva stanja
        String[] prihvatljivaStanja = ucitajLinije.get(2).split(",");

        //4.redak - pocetno stanje
        String pocetnoStanje = ucitajLinije.get(3);

        //5+ retci - funkcije prijelaza
        Map<Map<String, String>, String> zadaniPrijelazi = new HashMap<>();
        Map<String, List<String>> stanjeSimbol = new HashMap<>();

        for (int i = 4; i < ucitajLinije.size(); i++) {
            //rastavi redak na dvi strane s obzirom na strelicu
            String[] jedanRedak = ucitajLinije.get(i).split("->");
            String[] lijevaStrana = jedanRedak[0].split(",");
            String desnaStrana = jedanRedak[1];

            //ratsavi livu stranu s obziron na zarez
            String trenutnoStanje = lijevaStrana[0];
            String poslaniZnak = lijevaStrana[1];

            //formiraj unutarnju mapu
            Map<String, String> unutaranjaMapa = new LinkedHashMap<>();
            unutaranjaMapa.put(trenutnoStanje, poslaniZnak);

            //formiraj mapu od dobivenih varijabli
            zadaniPrijelazi.put(unutaranjaMapa, desnaStrana);

            if(!stanjeSimbol.containsKey(lijevaStrana[0])) {
                stanjeSimbol.put(lijevaStrana[0], new LinkedList<>());
            }
            stanjeSimbol.get(lijevaStrana[0]).add(lijevaStrana[1]);
        }

        //izbacujemo nedohvatljiva, pa imamo set onih koje prihvacamo
        Set<String> dohvatljivaStanja = new HashSet<>();
        dohvatljivaStanja.add(pocetnoStanje);

        //pomocu DFS algoritma obilazimo sva stanja i trazimo prihvatljiva
        DFS(zadaniPrijelazi, dohvatljivaStanja, pocetnoStanje, stanjeSimbol);
        //System.out.println("dohvatljiva poslije DFS" + dohvatljivaStanja);

        //dobivena prihvatljiva stanja samo sortiraj
        List<String> dohvatljivaStanjaSort = sortirajSet(dohvatljivaStanja);
        //System.out.println("dohvatljiva sortirana: " + dohvatljivaStanjaSort);

        //izbaci iz mape prijalaza one prijelaze koji ne sadrze dohvatljiva stanja kao pocetna...
        Map<Map<String, String>, String> potrebniPrijelazi= filtirajMapu(zadaniPrijelazi, dohvatljivaStanjaSort);

        //makni iz liste prihvatljivih stanja eventualna stanja koja su visak
        List<String> prihvatljivaStanjaNew = new ArrayList<>();

        //prodi kroz sva zadana prihvatljiva stanja (F) i filtriraj ih..
        for(String stanje : prihvatljivaStanja) {
            //System.out.println("stanje" + stanje);
            if(dohvatljivaStanja.contains(stanje)) {
                prihvatljivaStanjaNew.add(stanje);
            }
        }
        //System.out.println("prihatljivaStanjNew: " + prihvatljivaStanjaNew);

        //od tu

        Map<Map<String, String>, Boolean> stanjaProdena = new HashMap<>();
        //lista uz stanja - oni koje stavis u prodena stanja kad prodes uz taj stanja koja su njimna Key
        Map<Map<String, String>, List<Map<String, String>>> neprodenaLista = new HashMap<>();

        for (int i = 0; i < dohvatljivaStanjaSort.size(); i++) {
            for (int j = i + 1; j < dohvatljivaStanjaSort.size(); j++) {
                Map<String, String> paroviStanja = new HashMap<>();
                paroviStanja.put(dohvatljivaStanjaSort.get(j), dohvatljivaStanjaSort.get(i));

                String stanje1 = "";
                String stanje2 = "";
                for(Map.Entry<String, String> entry : paroviStanja.entrySet()) {
                    stanje1 = entry.getKey();
                   // System.out.println(stanje1);
                    stanje2 = entry.getValue();
                   // System.out.println(stanje2);
                }
                //System.out.println(prihvatljivaStanjaNew);
                    if ((prihvatljivaStanjaNew.contains(stanje1) && !prihvatljivaStanjaNew.contains(stanje2)) ||
                            (!prihvatljivaStanjaNew.contains(stanje1) && prihvatljivaStanjaNew.contains(stanje2))) {
                        stanjaProdena.put(paroviStanja, true);
                        //System.out.println("stanja prodena 1: " + stanjaProdena);
                    } else {
                        stanjaProdena.put(paroviStanja, false);
                        //System.out.println("stanja prodena 2: " + stanjaProdena);
                    }
            }
        }

//        for(int i = 0; i < paroviStanja.size() - 1; i++) {
//            for(int j = i +1; j < paroviStanja.size(); j++) {
//                for(String simbol : znakoviAbecede) {
//                    for(Map.Entry<String, String> entry : paroviStanja.entrySet()) {
//                        String stanje1 = entry.getKey();
//                        String stanje2 = entry.getValue();
//                        Map<String, String> mapa1 = new HashMap<>();
//                        mapa1.put(stanje1, simbol);
//
//                        Map<String, String> mapa2 = new HashMap<>();
//                        mapa2.put(stanje2, simbol);
//
//                        Set<String> kombinacija = new HashSet<>();
//                        kombinacija.add((zadaniPrijelazi.get(mapa1)));
//                        kombinacija.add((zadaniPrijelazi.get(mapa2)));
//
//                        if(kombinacija.size() == 2) {
//
//                            Map<String, String> paroviStanja2 = new HashMap<>();
//                            paroviStanja2.put(stanje1, stanje2);
//                            //(1,2) == (2,1) - dodaj i duplikate u prodena stanja
//                            if(stanjaProdena.get(paroviStanja2)) {
//                                //paroviStanja = new HashMap<>();
//                                paroviStanja2.put(paroviStanja2.get(i), paroviStanja2.get(j));
//                                stanjaProdena.put(paroviStanja2, true);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        Set<String> istovjetnaStanja = new HashSet<>();
//
//        for(Map.Entry<Map<String, String>, Boolean> entry : stanjaProdena.entrySet()) {
//            Map<String, String> parStanja = entry.getKey();
//            for(Map.Entry<String, String> entry2 : parStanja.entrySet()) {
//                if(entry2.getKey() != entry2.getValue()) {
//                    if(entry.getValue() == false) {
//                        istovjetnaStanja.add(entry2.getValue());
//                        //u prijelazima takoder zamijeni sva stanja koja si reducira
//                        for(Map.Entry<Map<String, String>, String> entry3 : zadaniPrijelazi.entrySet()) {
//                            if(entry3.getValue() == entry2.getValue())
//                                entry3.setValue(entry2.getKey());
//                        }
////                        if(pocetnoStanje == entry2.getValue())
////                            pocetnoStanje = entry2.getKey();
//                    }
//                }
//            }
//        }
//        prihvatljivaStanjaNew.removeAll(istovjetnaStanja);
//        dohvatljivaStanjaSort.removeAll(istovjetnaStanja);
//
//        //procisti zadane prijelaze
//        for(String stanje : istovjetnaStanja) {
//            for(String simbol : znakoviAbecede) {
//                Map<String, String> stanje_simbol = new HashMap<>();
//                stanje_simbol.put(stanje, simbol);
//
//                //ako postoji izbrisi ga
//                if(zadaniPrijelazi.containsKey(stanje_simbol)) {
//                    //zadaniPrijelazi.remove(stanje_simbol);
//                }
//            }
//            //ako se u prijlazima pojavljuje istovjetno stanje makni ga
//            for(Map.Entry<Map<String, String>, String> entry : zadaniPrijelazi.entrySet()) {
//                if(entry.getValue() == stanje);
//                    //zadaniPrijelazi.remove(entry.getKey());
//            }
//        }


        ukloniIstovjetne(stanjaProdena, neprodenaLista, dohvatljivaStanjaSort, znakoviAbecede, potrebniPrijelazi);

        Set<Map<String, String>> tranzitivnaStanja = new HashSet<>();

        List<Map<String, String>> istovjetnaStanja = stanjaProdena.keySet().stream().filter(s -> !stanjaProdena.get(s)).collect(Collectors.toList());

        for (Map<String, String> stanje : istovjetnaStanja) {
            String prvoStanje = "";
            String drugoStanje = "";
            for (Map.Entry<String, String> entry : stanje.entrySet()) {
                prvoStanje = entry.getKey();
                drugoStanje = entry.getValue();
            }
            //funkcija za "punjenje" tranzitivinh stanja
            //ukloniTranzitivne(prvoStanje, drugoStanje, stanje, istovjetnaStanja, tranzitivnaStanja);
        }

        for (Map<String, String> tranzitivnoStanje : tranzitivnaStanja) {

            for (Map.Entry<String, String> entry : tranzitivnoStanje.entrySet()) {
                dohvatljivaStanjaSort.remove(entry.getKey());
                prihvatljivaStanjaNew.remove(entry.getKey());

                if (pocetnoStanje.equals(entry.getKey())) {
                    pocetnoStanje = entry.getValue();
                }

                for(Map<String, String> entry2 : potrebniPrijelazi.keySet()) {
                    for(Map.Entry<String, String> entry3 : entry2.entrySet()) {
                        if(entry3.getKey() == entry.getKey()) {
                            Map<String, String> mapa = new HashMap<>();
                            mapa.put(entry.getValue(), entry3.getValue());
                            if(!potrebniPrijelazi.containsKey(mapa)) {
                                //entry2 = entry.getValue();
                            }
                        }
                    }
                }
            }
        }

        for (Map.Entry<Map<String, String>, String> entry : potrebniPrijelazi.entrySet()) {
            for (Map.Entry<String, String> entry2 : entry.getKey().entrySet()) {
                if(dohvatljivaStanjaSort.contains(entry2.getKey())) {

                }
            }
        }
        //potrebniPrijelazi = potrebniPrijelazi.entrySet().stream().filter(e -> dohvatljivaStanjaSort.contains(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

//        for (ParStanjeSimbol p : tranzitivnaStanja) {
//
//            for (ParStanjeSimbol p2 : potrebniPrijelazi.keySet()) {
//
//                if (potrebniPrijelazi.get(p2).equals(p.stanje)) {
//                    potrebniPrijelazi.put(p2, p.simbol);
//                }
//            }
//
//        }

//do tu

        StringBuilder ispisStringBuilder = isprintajStanja(potrebniPrijelazi, dohvatljivaStanjaSort, znakoviAbecede, prihvatljivaStanjaNew, pocetnoStanje);
        String ispis = ispisStringBuilder.toString();

        if (args.length == 2) {
            String outputOcekivani = Files.readString(Paths.get(args[1]));
            System.out.println(outputOcekivani.equals(ispis));
        } else {
            System.out.println(ispis);
        }
    }

    private static List<String> sortirajSet(Set<String> set){
        List<String> sortiranaLista;
        sortiranaLista = set
                .stream()
                .sorted()
                .collect(Collectors.toList());
        return sortiranaLista;
    }

    private static Map<Map<String, String>, String> filtirajMapu(Map<Map<String, String>, String> mapa, List<String> lista){
        Map<Map<String, String>, String> filtriranaMapa = new HashMap<>();

        //filtriraj == izbaci one dijelove mape koji za stanje nisu sadrzani u
        //poslanoj listi prihvatljivih stanja...
        for (Map.Entry<Map<String, String>, String> mapStringEntry : mapa.entrySet()){
            Map<String, String> unutarnjaMapa = mapStringEntry.getKey();

            for(Map.Entry<String, String> mapStringEntry2 : unutarnjaMapa.entrySet()) {
                String stanje = mapStringEntry2.getKey();
                if(lista.contains(stanje))
                    filtriranaMapa.put(unutarnjaMapa, mapStringEntry.getValue());
            }
        }
        return filtriranaMapa;
    }

//    private static void ukloniTranzitivne(String stanjePocetno,String drugoStanje,  Map<String, String> trenutno, List<Map<String, String>> istovjetnaStanja, Set<Map<String, String>> oznacenaStanjaTranzitivna) {
//
//        Optional<Map<String, String>> parStanjeSimbolTranzitivno = istovjetnaStanja.stream().filter(e -> e.stanje.equals(drugoStanje)).findFirst();
//        if (parStanjeSimbolTranzitivno.isPresent()) {
//            ukloniTranzitivne(stanjePocetno, drugoStanje, parStanjeSimbolTranzitivno.get(), istovjetnaStanja, oznacenaStanjaTranzitivna);
//        } else {
//            Map<String, String> tranzitivnoStanjeSimbol = new HashMap<>();
//            tranzitivnoStanjeSimbol.put(stanjePocetno, drugoStanje);
//            oznacenaStanjaTranzitivna.add(tranzitivnoStanjeSimbol);
//        }
//    }

    private static void ukloniIstovjetne(Map<Map<String, String>, Boolean> stanjaProdena, Map<Map<String, String>, List<Map<String, String>>> neprodenaLista, List<String> dohvatljivaStanjaNew, String[] abecedaSimbola, Map<Map<String, String>, String> prijelazi) {
        for (int i = 0; i < dohvatljivaStanjaNew.size(); i++) {
            OUTER:
            for (int j = i + 1; j < dohvatljivaStanjaNew.size(); j++) {
                Map<String, String> trenutnaStanja = new HashMap<>();
                trenutnaStanja.put(dohvatljivaStanjaNew.get(j), dohvatljivaStanjaNew.get(i));

                //System.out.println("stanja prodena: " + stanjaProdena);
                //System.out.println("trenutna stanja: " + trenutnaStanja);

                //ako stanja koja smo prosli sadrze ta stanja samoidi dalje
                if (stanjaProdena.get(trenutnaStanja)) {
                    continue OUTER;
                }

                for (String slovo : abecedaSimbola) {
                    Map<String, String> mapa1 = new HashMap<>();
                    Map<String, String> mapa2 = new HashMap<>();
                    for (Map.Entry<String, String> entry : trenutnaStanja.entrySet()) {
                        mapa1.put(entry.getKey(), slovo);
                        mapa2.put(entry.getValue(), slovo);
                    }
                        String stanje1 = prijelazi.get(mapa1);
                        String stanje2 = prijelazi.get(mapa2);

                        if (stanje1.equals(stanje2)) continue;
                        if (stanje1.compareTo(stanje2) < 0) {
                            String zamj = stanje1;
                            stanje1 = stanje2;
                            stanje2 = zamj;
                        }


                        Map<String, String> parZaProvjeru = new HashMap<>();
                        parZaProvjeru.put(stanje1, stanje2);

                        if (stanjaProdena.get(parZaProvjeru)) {
                            stanjaProdena.put(trenutnaStanja, true);

                            if (neprodenaLista.containsKey(trenutnaStanja)) {
                                rekurzivnoOznaci(trenutnaStanja, stanjaProdena, neprodenaLista);
                            }

                            continue OUTER;
                        }

                }

                for (String s : abecedaSimbola) {
                    Map<String, String> mapa1 = new HashMap<>();
                    Map<String, String> mapa2 = new HashMap<>();

                    for (Map.Entry<String, String> entry : trenutnaStanja.entrySet()) {

                        String stanje1 = prijelazi.get(mapa1);
                        String stanje2 = prijelazi.get(mapa2);

                        Map<String, String> parZaListu = new HashMap<>();
                        parZaListu.put(stanje1, stanje2);

                        if (!neprodenaLista.containsKey(parZaListu)) {
                            neprodenaLista.put(parZaListu, new ArrayList<>());
                        }

                        neprodenaLista.get(parZaListu).add(trenutnaStanja);
                    }
                }
            }
        }


    }

    private static void rekurzivnoOznaci(Map<String, String> trenutna, Map<Map<String, String>, Boolean> stanjaProdena, Map<Map<String, String>, List<Map<String, String>>> neprodenaLista) {

        List<Map<String, String>> lista = neprodenaLista.get(trenutna);

        for (Map<String, String> p : lista) {
            stanjaProdena.put(p, true);

            if (neprodenaLista.containsKey(p)) {
                rekurzivnoOznaci(p, stanjaProdena, neprodenaLista);
            }
        }

    }


    private static void DFS(Map<Map<String, String>, String> zadaniPrijelazi, Set<String> dohvatljivaStanja, String trenutnoStanje, Map<String, List<String>> stanjeSimbol) {

        //trebamo iz mape u mapi izvuci simbole abecede
        List<String> simboliAbecede = new ArrayList<>(stanjeSimbol.get(trenutnoStanje));

        //idemo po simbolima abecede i trazimo sva moguca stanja kroz koja moze proc
        for (int i = 0; i < simboliAbecede.size(); i++) {
            //mapa u koju spremimo par stanje, simbol
            Map<String, String> stanjeSimbolmapa = new HashMap<>();
            //dodajemo u petlji stanje i simbol...
            stanjeSimbolmapa.put(trenutnoStanje, simboliAbecede.get(i));
            //System.out.println(stanjeSimbolmapa);
            //System.out.println("stanjeSimbol: " + stanjeSimbol);
            //iz mape zdanih prijelaza dobivamo sljedece stanje
            String sljedeceStanje = zadaniPrijelazi.get(stanjeSimbolmapa);
            //System.out.println("sljedecestanje: " + sljedeceStanje);
            //radimo DFS da "procistimo" prijelaze pa onda zovemo
            // rekurzivno istu mnetodu za one koje mozemo dodat... tj necemo nikada dodati nedohvatljive
            if (dohvatljivaStanja.add(zadaniPrijelazi.get(stanjeSimbolmapa))) {
                //System.out.println(dohvatljivaStanja);
                DFS(zadaniPrijelazi, dohvatljivaStanja, sljedeceStanje, stanjeSimbol);
                //System.out.println(dohvatljivaStanja);
            }
        }
    }

    private static List<String> sortirajListu(List<String> lista) {
        List<String> sortiranaLista = new ArrayList<>();
        sortiranaLista = lista.
                stream().
                distinct().
                sorted().
                collect(Collectors.toList());
        return sortiranaLista;
    }

    private static String izmijeni(List<String> ulaz) {
        String izmjenjeni;
        izmjenjeni = ulaz.toString().substring(1, ulaz.toString().length() - 1);
        String makniRazmak;
        makniRazmak = izmjenjeni.replaceAll(" ", "");
        return  makniRazmak;
    }

    //ispisivanje rezultata
    private static StringBuilder isprintajStanja(Map<Map<String, String>, String> potrebniPrijelazi, List<String> dohvatljivaStanja, String[] abecedaSimbola, List<String> prihvatljivaStanja, String pocetnoStanje) {
        StringBuilder stringBuilder = new StringBuilder();
        //1.redak - nova dohvatljiva stanja - makni sve razmake iza zareza...
        String  ispis= izmijeni(dohvatljivaStanja);
        stringBuilder.append(ispis).append("\n");

        //2.redak - ulazni znakovi abecede
        ispis = izmijeni(Arrays.asList(abecedaSimbola));
        stringBuilder.append(ispis).append("\n");

        //3.redak - nova prihvatljiva stanja
        ispis = izmijeni(prihvatljivaStanja);
        stringBuilder.append(ispis).append("\n");

        //4.redak - pocetno stanje
        stringBuilder.append(pocetnoStanje).append("\n");

        //5.+ retci - novi prijelazi
        List<String> noviPrijelazi = new ArrayList<>();

        //iteriramo po entrySet() jer nam treebaju i key i value
        for (Map.Entry<Map<String, String>, String> mapStringEntry : potrebniPrijelazi.entrySet()) {
            StringBuilder stringBuilder2 = new StringBuilder();
            Map<String, String> stanjeSimbol = mapStringEntry.getKey();
            //definiramo stanje i simbol koje dodajemo u builder
            String stanje = "";
            String simbol = "";
            for(Map.Entry<String, String> map : stanjeSimbol.entrySet()){
                stanje = map.getKey();
                simbol = map.getValue();
            }

            //definramo sljedece stanje koje cemo dodati u builder
            String sljedeceStanje = mapStringEntry.getValue();
            stringBuilder2.append(stanje).append(",");
            stringBuilder2.append(simbol).append("->");
            stringBuilder2.append(sljedeceStanje).append("\n");

            //u listu novih prijelaza koje ispisujemo dodamo to sto smo napunili -> to moramo radi sortiranja
            noviPrijelazi.add(stringBuilder2.toString());
        }

        //sortiraj listu nakon sto je napunis
        noviPrijelazi = sortirajListu(noviPrijelazi);

        //napuni string builder
        for (int i = 0;i < noviPrijelazi.size(); i++) {
            stringBuilder.append(noviPrijelazi.get(i));
        }

        //vrati string builder
        return stringBuilder;
    }



}
