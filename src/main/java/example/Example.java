package example;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;

import com.javax0.sourcebuddy.Compiler;
public class Example {

    public interface Something {
        String say();
    }

    public static void main(String[] args)
        throws IOException, ClassNotFoundException, Compiler.CompileException, InvocationTargetException,
        NoSuchMethodException, InstantiationException, IllegalAccessException {

        var sourceFirstClass = """
                                package example.generated;                        

                                public class MyObject
                                    implements example.Example.Something {

                                    public String say() {
                                        return "Hello";
                                  }
                                }
                                """;

        final var compiled = Compiler.java()
            .from("example.generated.MyObject", sourceFirstClass)
            .from(Paths.get("src/test/java"))
            .compile();

        compiled.saveTo(Paths.get("build/classes/java/main"));
        compiled.stream().forEach(bc -> System.out.println("*** " + Compiler.getBinaryName(bc)));
        final var loaded = compiled.load();
        Class<?> clazz = loaded.get("example.generated.MyObject");
        var something = loaded.newInstance(Something.class);
        loaded.stream().forEach(klass -> System.out.println("*** " + klass.getSimpleName()));
        System.out.println("*** " + clazz.getName());
        System.out.println("*** " + something);
        System.out.println("*** " + clazz.getMethod("say").invoke(something));
        System.out.println("*** " + something.say());
    }
}
