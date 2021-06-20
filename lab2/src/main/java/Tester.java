import java.io.IOException;

public class Tester {

    public static void main(String[] args) throws IOException {

        int brojPrimjera = 14;

        for (int i = 1; i <= brojPrimjera; i++) {

            String broj = String.valueOf(i);
            if (i < 10) {
                broj = "0" + broj;
            }
            String input = "primjeri/test" + broj + "/t.ul";
            String output = "primjeri/test" + broj + "/t.iz";

            System.out.println(i);
            MinDka.main(new String[]{input, output});
        }

    }
}
