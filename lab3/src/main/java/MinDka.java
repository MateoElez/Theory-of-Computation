//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//
//
//public class MinDka {
//
//    public static void main(String[] args) throws IOException {
//
//         BufferedReader br = new BufferedReader(new FileReader(args[0]));
//        //BufferedReader br = new BufferedReader(new FileReader("primjeri/test16" +"/primjer.in"));
//        //      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        List<String> lines = new ArrayList<>();
//        String line;
//        while (true) {
//            line = br.readLine();
//            if (line == null) break;
//            lines.add(line);
//        }
//
//        String[] ulazni = lines.get(0).split("\\|");
//
//        List<List<String>> listaUlaznih = new ArrayList<>();
//
//        for (int i = 0; i < ulazni.length; i++) {
//            List<String> temp = new ArrayList<>();
//
//            temp = Arrays.asList(ulazni[i].split(","));
//
//            listaUlaznih.add(temp);
//        }
//
//        List<String> skupStanja = Arrays.asList(lines.get(1).split(","));
//        String[] skupUlaznihZnakova = lines.get(2).split(",");
//        String[] skupZnakovaStoga = lines.get(3).split(",");
//        List<String> prihvatljivaStanja = Arrays.asList(lines.get(4).split(","));
//        String pocetnoStanje = lines.get(5);
//        String pocetniZnakStoga = lines.get(6);
//
//
//        Map<TrojkaStanjeZnakStog, ParStanjeStog> prijelazi = new LinkedHashMap<>();
//        Map<String, List<String>> stanjeSimbol = new LinkedHashMap<>();
//
//        for (int i = 7; i < lines.size(); i++) {
//            String[] povezivanje = lines.get(i).split("->");
//            String[] podijelaTrojka = povezivanje[0].split(",");
//            String[] stanjeStogPrijelaza = povezivanje[1].split(",");
//            TrojkaStanjeZnakStog trojka = new TrojkaStanjeZnakStog(podijelaTrojka[0], podijelaTrojka[1], podijelaTrojka[2]);
//            ParStanjeStog par = new ParStanjeStog(stanjeStogPrijelaza[0], stanjeStogPrijelaza[1]);
//            prijelazi.put(trojka, par);
//
//
//        }
//
//        StringBuilder sbIzlaz = new StringBuilder();
//        String trenutnoStanje;
//        String  trenutniStog;
//
//
//        boolean imaPrijelaz = false;
//
//
//
//        for (List<String> ulazi : listaUlaznih) {
//
//            sbIzlaz.append(isprintajStanja(pocetnoStanje,pocetniZnakStoga));
//
//
//            trenutniStog = pocetniZnakStoga;
//            trenutnoStanje = pocetnoStanje;
//            List<String> epsiloni = new ArrayList<>();
//            List<String> stanje = new ArrayList<>();
//            List<String> stog = new ArrayList<>();
//            stog.add(trenutniStog);
//            stanje.add(trenutnoStanje);
//
//            dohvatiEpsilon(trenutnoStanje,trenutniStog,prijelazi,epsiloni,stog,stanje,sbIzlaz,0,5,prihvatljivaStanja);
//            trenutnoStanje = stanje.get(stanje.size()-1);
//            trenutniStog = stog.get(stog.size()-1);
//
//            for (int i = 0; i < ulazi.size(); i++) {
//                imaPrijelaz = false;
//                String znak = ulazi.get(i);
//                //stog.clear();
//                //stanje.clear();
//
//
//                TrojkaStanjeZnakStog trojka = new TrojkaStanjeZnakStog(trenutnoStanje, znak, Character.toString(trenutniStog.charAt(0)));
//
//                if (prijelazi.containsKey(trojka)) {
//                    String stog1 = prijelazi.get(trojka).stog;
//                    if (stog1.equals("$") ){
//                        trenutniStog =  trenutniStog.substring(1);
//                    }else{
//                        trenutniStog = prijelazi.get(trojka).stog + trenutniStog.substring(1);
//                    }
//                    trenutnoStanje = prijelazi.get(trojka).stanje;
//                    stog.add(trenutniStog);
//                    stanje.add(trenutnoStanje);
//                    sbIzlaz.append(isprintajStanja(trenutnoStanje,trenutniStog));
//
//                    dohvatiEpsilon(trenutnoStanje,trenutniStog,prijelazi,epsiloni,stog,stanje,sbIzlaz,i,ulazi.size(),prihvatljivaStanja);
//                        trenutnoStanje = stanje.get(stanje.size()-1);
//                        trenutniStog = stog.get(stog.size()-1);
//                    imaPrijelaz = true;
//
//                }
//
//
//                if (!imaPrijelaz) {
//                    sbIzlaz.append("fail");
//                    trenutnoStanje = null;
//                    sbIzlaz.append("|");
//                    break;
//                } else {
//
//
//                }
//
//
//            }
//            if (prihvatljivaStanja.contains(trenutnoStanje)) {
//                sbIzlaz.append("1");
//            } else {
//                sbIzlaz.append("0");
//            }
//
//
//            sbIzlaz.append("\n");
//
//        }
//
//
//        if (args.length == 2) {
//            String output = Files.readString(Paths.get(args[1]));
//            System.out.println(output.equals(sbIzlaz.toString()));
//        } else {
//            System.out.println(sbIzlaz);
//        }
//
//
//    }
//
//
//
//
//    private static void dohvatiEpsilon(String trenutnoStanje, String trenutniStog, Map<TrojkaStanjeZnakStog, ParStanjeStog> prijelazi,List<String> epsiloni, List<String> stog, List<String> stanje,StringBuilder sbIzlaz,int i ,int size_ulaz , List<String> prihvatljivaStanja) {
//
//        TrojkaStanjeZnakStog trojka = new TrojkaStanjeZnakStog(trenutnoStanje, "$", Character.toString(trenutniStog.charAt(0)));
//        if(  i== size_ulaz-1 && prihvatljivaStanja.contains(trenutnoStanje)) return;
//        if (prijelazi.containsKey(trojka)) {
//            String stog1 = prijelazi.get(trojka).stog;
//            if (stog.equals("$") ){
//                trenutniStog =  trenutniStog.substring(1, trenutniStog.length());
//            }else{
//                trenutniStog = prijelazi.get(trojka).stog + trenutniStog.substring(1, trenutniStog.length());
//            }
//
//            trenutnoStanje = prijelazi.get(trojka).stanje;
//            stog.add(trenutniStog);
//            stanje.add(trenutnoStanje);
//            sbIzlaz.append(isprintajStanja(trenutnoStanje,trenutniStog));
//
//
//
//            dohvatiEpsilon(trenutnoStanje,trenutniStog,prijelazi,epsiloni,stog,stanje ,sbIzlaz,i,size_ulaz,prihvatljivaStanja);
//
//        }
//
//    }
//
//
//    private static String isprintajStanja(String pocetnoStanje,String pocetniZnakStoga) {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(pocetnoStanje);
//        sb.append("#");
//        sb.append(pocetniZnakStoga);
//        sb.append("|");
//        return sb.toString();
//    }
//
//    private static class ParStanjeStog {
//
//        String stanje;
//        String stog;
//
//        public ParStanjeStog(String stanje, String stog) {
//            this.stanje = stanje;
//            this.stog = stog;
//        }
//
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            ParStanjeStog that = (ParStanjeStog) o;
//            return Objects.equals(stanje, that.stanje) && Objects.equals(stog, that.stog);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(stanje, stog);
//        }
//
//        @Override
//        public String toString() {
//            return "Pa0rStanjeSimbol{" +
//                    "stanje='" + stanje + '\'' +
//                    ", simbol='" + stog + '\'' +
//                    '}';
//        }
//
//
//    }
//
//    private static class TrojkaStanjeZnakStog {
//
//        String stanje;
//        String simbol;
//        String stog;
//
//        public TrojkaStanjeZnakStog(String stanje, String simbol, String stog) {
//            this.stanje = stanje;
//            this.simbol = simbol;
//            this.stog = stog;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            TrojkaStanjeZnakStog that = (TrojkaStanjeZnakStog) o;
//            return Objects.equals(stanje, that.stanje) && Objects.equals(simbol, that.simbol) && Objects.equals(stog, that.stog);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(stanje, simbol, stog);
//        }
//
//        @Override
//        public String toString() {
//            return "TrojkaStanjeZnakStog{" +
//                    "stanje='" + stanje + '\'' +
//                    ", simbol='" + simbol + '\'' +
//                    ", stog='" + stog + '\'' +
//                    '}';
//        }
//    }
//}
