import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Parser {

    //globalne varijable:

    static List<String> ulaz = new LinkedList<>();
    static String ulazniZnak;
    static StringBuilder stringBuilder = new StringBuilder();
    static boolean flag;

    public static void main(String[] args) throws IOException {

        //BufferedReader reader = new BufferedReader(new FileReader("primjeri/test19" +"/test.in"));
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));

        String ulazniNiz = reader.readLine();

        //IDEJA: idemo napravit niz charova
        // i onda taj niz charova kroz petlju pretvorit u niz stringova
        // pomocu Character.toString....

        List<Character> listaChar = new LinkedList<>();

        char[] ulazniNizChar = ulazniNiz.toCharArray();

        for (char ch : ulazniNizChar) {
            listaChar.add(ch);
        }
        //sada imamo listu charova iz koje moramo dobit listu stringova

        for(char ch : listaChar) {
            ulaz.add(Character.toString(ch));
        }

        flag = true;
        S();

        if(ulaz.isEmpty() && flag) {
            stringBuilder.append("\nDA");
        } else {
            stringBuilder.append("\nNE");
        }
        stringBuilder.append("\n");

        if (args.length == 2) {
            String output = Files.readString(Paths.get(args[1]));
            System.out.println(output.equals(stringBuilder.toString()));
        } else {
            System.out.println(stringBuilder);
        }

    }

    private static void S() {
        stringBuilder.append('S');
        if(ulaz.isEmpty()){
            flag = false;
        } else {
            ulazniZnak = ulaz.get(0);
            ulaz.remove(0);
            switch (ulazniZnak) {
                case ("a"):
                    A();
                    if (flag)
                        B();
                    break;
                case ("b"):
                    B();
                    if (flag)
                        A();
                    break;
                default:
                    flag = false;
            }
        }
    }

    private static void A() {
        stringBuilder.append('A');

        if(ulaz.isEmpty()){
            flag = false;
        } else {
            ulazniZnak = ulaz.get(0);

            switch (ulazniZnak) {
                case ("a"):
                    ulaz.remove(0);
                    flag = true;
                    break;
                case ("b"):
                    ulaz.remove(0);
                    C();
                    break;
                default:
                    flag = false;
            }
        }

    }

    private static void B() {
        stringBuilder.append('B');

        if(ulaz.isEmpty()){
            flag = true;
        } else {
            //lakse s if-ovima rjesit nego switch case

            //za B produkciju on moze procitat samo c ili *nista*
            if (ulaz.get(0).equals("c")) {
                ulaz.remove(0);
                //flag = true;
                if (!ulaz.isEmpty() && ulaz.get(0).equals("c")) {
                    ulaz.remove(0);
                    //flag = true;
                    S();
                    if (flag && !ulaz.isEmpty() && ulaz.get(0).equals("b")) {
                        ulaz.remove(0);
                        //flag = true;
                        if (!ulaz.isEmpty() && ulaz.get(0).equals("c")) {
                            ulaz.remove(0);
                            flag = true;
                        } else
                            flag = false;
                    } else
                        flag = false;
                } else
                    flag = false;
            }
        }
    }

    private static void C() {
        stringBuilder.append('C');
        flag = false;

        //nema citanja znakova opce jer C u svakom slucaju poziva prvo A,
        // pa ako vrijedi uvjet onda jos jednom A...
        A();
        if(flag)
            A();
    }
}
