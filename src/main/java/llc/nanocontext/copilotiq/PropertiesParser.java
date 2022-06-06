package llc.nanocontext.copilotiq;

import java.util.ArrayList;
import java.util.List;
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
public class PropertiesParser extends Properties {
    /**
     * A RegEx pattern for a key subst: ${key}
     * Literally, match on any sequence starting with the literal "${",
     * followed by any characters except '}' as the named capturing group "key",
     * followed by '}'
     */
    private final static String KEY_GROUP_NAME = "key";
    private static final String SUBSTITUTION_REGEX = "\\$\\{(?<" + KEY_GROUP_NAME + ">[^\\}]*)\\}";
    private static final Pattern PATTERN = Pattern.compile(SUBSTITUTION_REGEX);

    /**
     * The properties as passed to the constructor.
     */
    private final Properties rawProperties;

    /**
     *
     * @param rawProperties
     */
    public PropertiesParser(final Properties rawProperties) {
        this();
        this.rawProperties.putAll(rawProperties);
    }

    public PropertiesParser() {
        this.rawProperties = new Properties();
    }

    /**
     *
     * @param key
     * @return the resolved property, including substitutions of needed, or null if not found
     */
    @Override
    public String getProperty(final String key) {
        return internalGetProperty(key, new ArrayList<>());
    }

    @Override
    public String getProperty(final String key, final String defaultValue) {
        String resolvedValue = getProperty(key);
        return resolvedValue == null ? defaultValue : resolvedValue;
    }

    /**
     *
     */
    public void parse() {
        for (String key : rawProperties.stringPropertyNames()) {
            String value = rawProperties.getProperty(key);
            if (hasSubstKey(value))
                value = applyPattern(value);
        }
    }

    /**
     * build a string, replacing substitution keys with property values
     * the algorithm is as follows:
     * the result will be accumulated into a StringBuilder
     * set the start to 0
     * loop until the start is past the end
     *   find a match (from start) for the substitution regex
     *   if a match exists
     *     copy from the start to the beginning of the match to the result
     *     resolve the key to the value, copy the value to the result
     *     set start to the end of the match
     *   else (no match exists
     *     copy from start to the end to the result
     *     set start to the end plus 1
     *
     * @param key - the property to resolve
     * @param keyPath = the path of keys which we have already visited, used to prevent an infinite loop
     * @return
     */
    private String internalGetProperty(final String key, final List<String> keyPath) {
        if (keyPath.contains(key))
            throw new IllegalArgumentException("Circular reference detected when resolving '" + key + "'.");
        final StringBuilder result = new StringBuilder();
        final String rawProperty = rawProperties.getProperty(key);
        if (rawProperty != null) {
            int nextFindStartIndex = 0;
            Matcher substitutionMatcher = PATTERN.matcher(rawProperty);

            // find all of the substitution strings in the raw property value
            // note that the substitutionMatcher always operates on the rawProperty
            while (substitutionMatcher.find(nextFindStartIndex)) {
                final int matchStart = substitutionMatcher.start();
                final int matchedEnd = substitutionMatcher.end();
                final String substitutionKey = substitutionMatcher.group(KEY_GROUP_NAME);

                // get the leading characters before changing the value of result
                final String leading = rawProperty.substring(nextFindStartIndex, matchStart);
                result.append(leading);

                // get the value of the key, note that this may be recursive
                keyPath.add(key);
                final String substitutionValue = internalGetProperty(substitutionKey, keyPath);
                result.append(substitutionValue);

                nextFindStartIndex = matchedEnd;
            }

            // append any leftover constant vales to the result
            if (nextFindStartIndex < rawProperty.length())
                result.append(rawProperty.substring(nextFindStartIndex));
            return result.toString();
        } else {
            return null;
        }
    }


    /**
     * Executes string substitution for the {@code value}
     *
     * @param value a String, containing key substitution values
     * @return the fully substituted value, if possible
     */
    protected String applyPattern(final String value) {
        String result = null;
        if (value != null) {
            result = value;

            Pattern keyPattern = Pattern.compile(SUBSTITUTION_REGEX);
            Matcher m = keyPattern.matcher(value);
            if (m.find()) {
                StringBuilder sb = new StringBuilder();

                sb.append(value.substring(0, m.start())).append(extractKeyValue(m.group()));
                sb.append(value.substring(m.end()));

                result = sb.toString();
            }
        }
        return result;
    }


    /** Replaces the subst key with its value, if available in {@code props} */
    // TODO: improve javadoc
    private String extractKeyValue(final String key) {
        String result = null;
        if (this.rawProperties != null) {

        }

        return result;
    }


    private boolean hasSubstKey(final String value) {
        return SUBSTITUTION_REGEX.matches(value);
    }
}

