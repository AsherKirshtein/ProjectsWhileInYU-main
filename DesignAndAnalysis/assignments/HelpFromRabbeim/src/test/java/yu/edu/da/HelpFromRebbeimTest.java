package yu.edu.da;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.yu.da.HelpFromRabbeim;
import edu.yu.da.HelpFromRabbeimI;
import edu.yu.da.HelpFromRabbeimI.HelpTopics;
import edu.yu.da.HelpFromRabbeimI.Rebbe;

/**
 * Unit test for simple App.
 */
public class HelpFromRebbeimTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        List<HelpTopics> akivaTeaches = new ArrayList<>();
        akivaTeaches.add(HelpTopics.BAVA_KAMMA);
        akivaTeaches.add(HelpTopics.SANHEDRIN);
        akivaTeaches.add(HelpTopics.CHUMASH);
        akivaTeaches.add(HelpTopics.NACH);
        Rebbe akiva = new Rebbe(1, akivaTeaches);
        List<HelpTopics> ravShechterTeaches = new ArrayList<>();
        ravShechterTeaches.add(HelpTopics.BAVA_KAMMA);
        ravShechterTeaches.add(HelpTopics.CHUMASH);
        Rebbe ravShechter = new Rebbe(2, ravShechterTeaches);
        List<HelpTopics> rashiTeaches = new ArrayList<>();
        rashiTeaches.add(HelpTopics.MUSSAR);
        rashiTeaches.add(HelpTopics.CHUMASH);
        rashiTeaches.add(HelpTopics.NACH);
        Rebbe rashi = new Rebbe(3, rashiTeaches);
        List<HelpTopics> rabbiStephaniTeaches = new ArrayList<>();
        rabbiStephaniTeaches.add(HelpTopics.NACH);
        Rebbe rabbiStephani = new Rebbe(4, rabbiStephaniTeaches);
        List<HelpTopics> rabbiWilligTeaches = new ArrayList<>();
        rabbiWilligTeaches.add(HelpTopics.MUSSAR);
        rabbiWilligTeaches.add(HelpTopics.SANHEDRIN);
        Rebbe rabbiWillig = new Rebbe(5, rabbiWilligTeaches);

        List<Rebbe> rabbeim = new ArrayList<>();
        rabbeim.add(akiva);
        rabbeim.add(ravShechter);
        rabbeim.add(rashi);
        rabbeim.add(rabbiStephani);
        rabbeim.add(rabbiWillig);

        Map<HelpTopics, Integer> requestedHelp = new HashMap<>();
        requestedHelp.put(HelpTopics.BAVA_KAMMA, 1);
        requestedHelp.put(HelpTopics.CHUMASH, 1);
        requestedHelp.put(HelpTopics.MUSSAR, 1);
        requestedHelp.put(HelpTopics.SANHEDRIN, 1);
        requestedHelp.put(HelpTopics.NACH, 1);

        HelpFromRabbeim helpFromRabbeim = new HelpFromRabbeim();
        helpFromRabbeim.scheduleIt(rabbeim, requestedHelp);
    }

    @Test
    public void AnotherTest()
    {
        List<HelpTopics> akivaTeaches = new ArrayList<>();
        akivaTeaches.add(HelpTopics.BAVA_KAMMA);
        akivaTeaches.add(HelpTopics.SANHEDRIN);
        akivaTeaches.add(HelpTopics.CHUMASH);
        akivaTeaches.add(HelpTopics.NACH);
        Rebbe akiva = new Rebbe(1, akivaTeaches);
        List<HelpTopics> ravShechterTeaches = new ArrayList<>();
        ravShechterTeaches.add(HelpTopics.BAVA_KAMMA);
        ravShechterTeaches.add(HelpTopics.CHUMASH);
        Rebbe ravShechter = new Rebbe(2, ravShechterTeaches);
        List<HelpTopics> rashiTeaches = new ArrayList<>();
        rashiTeaches.add(HelpTopics.MUSSAR);
        rashiTeaches.add(HelpTopics.CHUMASH);
        rashiTeaches.add(HelpTopics.NACH);
        Rebbe rashi = new Rebbe(3, rashiTeaches);
        List<HelpTopics> rabbiStephaniTeaches = new ArrayList<>();
        rabbiStephaniTeaches.add(HelpTopics.NACH);
        Rebbe rabbiStephani = new Rebbe(4, rabbiStephaniTeaches);
        List<HelpTopics> rabbiWilligTeaches = new ArrayList<>();
        rabbiWilligTeaches.add(HelpTopics.MUSSAR);
        rabbiWilligTeaches.add(HelpTopics.SANHEDRIN);
        Rebbe rabbiWillig = new Rebbe(5, rabbiWilligTeaches);
        List<HelpTopics> OzeTeaches = new ArrayList<>();
        OzeTeaches.add(HelpTopics.MUSSAR);
        OzeTeaches.add(HelpTopics.SANHEDRIN);
        Rebbe Oze = new Rebbe(69, OzeTeaches);
        List<HelpTopics> AsherTeaches = new ArrayList<>();
        AsherTeaches.add(HelpTopics.SANHEDRIN);
        Rebbe Asher= new Rebbe(420, rabbiWilligTeaches);

        List<Rebbe> rabbeim = new ArrayList<>();
        rabbeim.add(akiva);
        rabbeim.add(ravShechter);
        rabbeim.add(rashi);
        rabbeim.add(rabbiStephani);
        rabbeim.add(rabbiWillig);
        rabbeim.add(Oze);
        rabbeim.add(Asher);

        Map<HelpTopics, Integer> requestedHelp = new HashMap<>();
        requestedHelp.put(HelpTopics.BAVA_KAMMA, 1);
        requestedHelp.put(HelpTopics.CHUMASH, 1);
        requestedHelp.put(HelpTopics.MUSSAR, 1);
        requestedHelp.put(HelpTopics.SANHEDRIN, 3);
        requestedHelp.put(HelpTopics.NACH, 1);

        HelpFromRabbeim helpFromRabbeim = new HelpFromRabbeim();
        helpFromRabbeim.scheduleIt(rabbeim, requestedHelp);
    }
    @Test
    public void test1() {
        
        List<HelpTopics> a = new LinkedList<HelpTopics>();
        List<HelpTopics> b = new LinkedList<HelpTopics>();
        a.add(HelpTopics.BAVA_KAMMA);
        b.add(HelpTopics.BAVA_KAMMA);
        
        final Rebbe rebbe0 = new Rebbe(0, a);
        final Rebbe rebbe1 = new Rebbe(1, b);
        final Map<HelpTopics, Integer> requestedHelp = new HashMap<>();
        requestedHelp.put(HelpTopics.BAVA_KAMMA, 2);
        
        List<Rebbe> rabbeim = new ArrayList<>();
        
        final HelpFromRabbeimI hfr = new HelpFromRabbeim();
        rabbeim.add(rebbe0);
        rabbeim.add(rebbe1);
        final Map<Integer, HelpTopics> schedule = hfr.scheduleIt(rabbeim, requestedHelp); 

        System.out.println(schedule.toString());
    }
}
