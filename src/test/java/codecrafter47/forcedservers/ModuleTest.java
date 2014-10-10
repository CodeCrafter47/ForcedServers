package codecrafter47.forcedservers;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class ModuleTest {

    private String testConfig = ""
            + "TestModule:\n"
            + "  enabled: true\n"
            + "  number: 47\n";

    private boolean en = false;

    @Test
    public void testModule(){
        TestModule module = new TestModule(ConfigurationProvider.getProvider(YamlConfiguration.class).load(testConfig));
        Assert.assertEquals(module.getName(), "TestModule");
        Assert.assertEquals(module.isEnabled(), true);
        Assert.assertEquals(47, module.number);
    }

    public class TestModule extends Module{

        @Setting
        public int number;

        public TestModule(Configuration config) {
            super(config);
        }

        @Override
        public void onEnable() {
            en = true;
        }

        @Override
        public void onDisable() {

        }
    }
}