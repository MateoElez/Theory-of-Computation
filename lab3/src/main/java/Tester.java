import java.io.IOException;

public class Tester {

    public static void main(String[] args) throws IOException {

        int brojPrimjera = 25;

        for (int i = 1; i <= brojPrimjera; i++) {

            String broj = String.valueOf(i);
            if (i < 10) {
                broj = "0" + broj;
            }
            String input = "primjeri/test" + broj + "/primjer.in";
            String output = "primjeri/test" + broj + "/primjer.out";

            System.out.println(i);
            SimPa.main(new String[]{input, output});
        }

    }
}
