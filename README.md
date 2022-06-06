# CopilotIQ
CopilotIQ Coding Test

This is a MAven project using the default file structure.
To build and run unit tests, run:
>> mvn clean install


Part 1
=======

The code below was written with some bugs in it and we must fix them. Please write unit tests to expose the bugs; then fix the code; then, ideally, add a couple more unit tests to make sure the code works, now and after the next refactoring.


Please provide a runnable project (using either Gradle or Maven) with at least the fixed class, and the unit tests; including comments/javadoc as appropriate.


Note - the javadoc may also be incomplete, so please don't
assume that all possible test cases are covered there.



import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <h3>PropertiesParser</h3>
 *
 * Resolves key substitution on a properties set; in other words, given:
 * <pre>FOO = foo
 * BAR = bar
 * baz = ${FOO} is ${BAR}
 * </pre>
 *
 * the following code will satisfy the assertion:
 * <pre> Properties props = Properties.load('my.properties');
 *  PropertiesParser parser = new PropertiesParser(props);
 *  parser.parse();
 *  assert("foo is bar".equals(props.getProperty("baz"));
 * </pre>
 */
public class PropertiesParser {


    /** A RegEx pattern for a key subst: ${key} */


    private final String PATTERN = "${.*}";
    private final Properties props;


    public PropertiesParser(Properties props) {
        this.props = props;
    }


    public void parse() {
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            if (hasSubstKey(value))
            value = applyPattern(value);
        }
    }


    /**
     * Executes string substitution for the {@code value}
     *
     * @param value
     * @return the fully substituted value, if possible
     */
    protected String applyPattern(String value) {
        Pattern keyPattern = Pattern.compile(PATTERN);
        Matcher m = keyPattern.matcher(value);
        StringBuilder sb = new StringBuilder();
        if (m.find()) {
            sb.append(value.substring(curPos, m.start())).append(extractKeyValue(m.group()));
            sb.append(value.substring(m.end()));
        }
        return sb.toString();
    }


    /** Replaces the subst key with its value, if available in {@code props} */
    // TODO: improve javadoc
    private String extractKeyValue(String key) {
        // TODO: implement
    }


    private boolean hasSubstKey(String value) {
        // TODO: implement
    }
}






Part 2
=======


Define the Class and method(s) signature(s) for a utility
that provides the following functionality:


Given a set of 'pseudo-paths' such as


      `/opt/util/labs`,
      `/opt/util/labs/java`,
      `/home/users/bob`,
      `/home/users/joe`, etc.


we want subsequently to be able to find out whether a given 'pseudo-path'
would belong to the tree defined by the paths originally given (eg, /home/users would belong, /opt/util/labs/python and /root/foo would not).


Provide only the signature(s), not the implementation.
