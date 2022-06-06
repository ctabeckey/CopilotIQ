package llc.nanocontext.copilotiq;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertiesParserTest {

    // Create test data that can be used with multiple test cases
    static final List<Properties> validPropertiesList;
    static final List<Properties> circularPropertiesList;

    static {
        validPropertiesList = new ArrayList<>();

        Properties properties = new Properties();
        properties.put("a", "correct");
        validPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "${b}");
        properties.put("b", "correct");
        validPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        properties.put("c", "correct");
        validPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "${b}${c}");
        properties.put("b", "correct");
        properties.put("c", "correcter");
        validPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "${b}AND${c}");
        properties.put("b", "correct");
        properties.put("c", "correcter");
        validPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "hello ${b}");
        properties.put("b", "world");
        validPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "${b} world");
        properties.put("b", "hello");
        validPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "${b655321}");
        properties.put("b655321", "correct");
        validPropertiesList.add(properties);

        circularPropertiesList = new ArrayList<>();

        properties = new Properties();
        properties.put("a", "${a}");
        circularPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "${b}");
        properties.put("b", "${a}");
        circularPropertiesList.add(properties);

        properties = new Properties();
        properties.put("a", "${b}");
        properties.put("b", "${c}");
        properties.put("c", "${a}");
        circularPropertiesList.add(properties);
    }

    // ====================================================================
    // Test simple use cases, including recursive property resolution
    // ====================================================================
    @DataProvider
    public Object[][] validPropertiesDataProvider() {
        return new Object[][] {
                {validPropertiesList.get(0), "a", "correct"},
                {validPropertiesList.get(1), "a", "correct"},
                {validPropertiesList.get(2), "a", "correct"},
                {validPropertiesList.get(3), "a", "correctcorrecter"},
                {validPropertiesList.get(4), "a", "correctANDcorrecter"},
                {validPropertiesList.get(0), "d", null},
                {validPropertiesList.get(5), "a", "hello world"},
                {validPropertiesList.get(6), "a", "hello world"},
                {validPropertiesList.get(7), "a", "correct"},
        };
    }

    @Test(dataProvider= "validPropertiesDataProvider")
    public void testGetProperty(final Properties properties, final String key, final String expectedlValue) {
        PropertiesParser sut = new PropertiesParser(properties);
        final String actual = sut.getProperty(key);

        Assert.assertEquals(actual, expectedlValue);
    }

    // ====================================================================
    // Test default value usage, including tests where the default should
    // not be used
    // ====================================================================
    @DataProvider
    public Object[][] propertiesWithDefaultDataProvider() {
        return new Object[][] {
                {validPropertiesList.get(0), "a", "default", "correct"},
                {validPropertiesList.get(1), "a", "default", "correct"},
                {validPropertiesList.get(2), "a", "default", "correct"},
                {validPropertiesList.get(0), "d", "default", "default"},
                {validPropertiesList.get(0), "hj12w", "default", "default"},
        };
    }

    @Test(dataProvider= "propertiesWithDefaultDataProvider")
    public void testGetPropertyWithDefault(final Properties properties, final String key, final String defaultValue, final String expectedlValue) {
        PropertiesParser sut = new PropertiesParser(properties);
        final String actual = sut.getProperty(key, defaultValue);

        Assert.assertEquals(actual, expectedlValue);
    }

    // ====================================================================
    // Test properties with circular references
    // ====================================================================
    @DataProvider
    public Object[][] circularPropertiesDataProvider() {
        return new Object[][] {
                {circularPropertiesList.get(0), "a"},
                {circularPropertiesList.get(1), "a"},
                {circularPropertiesList.get(1), "b"},
                {circularPropertiesList.get(2), "a"},
                {circularPropertiesList.get(2), "b"},
                {circularPropertiesList.get(2), "c"},
        };
    }

    @Test(dataProvider = "circularPropertiesDataProvider", expectedExceptions = {IllegalArgumentException.class})
    public void testCircularGetProperty(final Properties properties, final String key) {
        PropertiesParser sut = new PropertiesParser(properties);
        sut.getProperty(key);
    }
}
