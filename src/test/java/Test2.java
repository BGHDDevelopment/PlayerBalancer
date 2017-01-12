import com.google.gson.Gson;
import org.junit.Test;

public class Test2 {
    @Test
    public void test() {
        Gson gson = new Gson();
        Class1 object = new Class1("test");
        String json = gson.toJson(object);
        System.out.println(json);
    }

    public class Class1 extends Class2 {
        public Class1(String test) {
            super(test);
        }
    }

    public class Class2 {
        private final String test;

        public Class2(String test) {
            this.test = test;
        }
    }
}

