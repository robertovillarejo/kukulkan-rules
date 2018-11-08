package mx.infotec.dads.kukulkan.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
// @PropertySource(value = "classpath:application.yml")
public class RulesApplicationTests {

    @Autowired
    RulesProperties properties;

    @Before
    public void contextLoads() {
    }

    @Test
    public void testRulesApplier() {
        List<Rule> rules = getRules();
        Costo costo = getCosto();
        RulesApplier applier = new DefaultRulesApplier(rules, new SpelExpressionParser());
        applier.apply(new StandardEvaluationContext(costo));
        System.out.println(costo);
    }

    @Test
    public void testCreation() {
        properties.getRules();
    }

    public List<Rule> getRules() {
        ArrayList<Rule> rules = new ArrayList<>();
        List<Action> actions = getActions();
        rules.add(new Rule("rule1", "iva == 16", actions, 10));
        rules.add(new Rule("rule2", "id == '1'", actions, 1));
        rules.sort((Rule r1, Rule r2) -> r1.getOrder() - r2.getOrder());
        return rules;
    }

    public Costo getCosto() {
        Costo costo = new Costo();
        costo.setId("1");
        costo.setIva(0);
        costo.setBeneficiario("Roberto Villarejo");
        return costo;
    }

    public List<Action> getActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(new Action("iva = iva + 16", 10));
        actions.add(new Action("id = id.concat('0000')", 1));
        actions.sort((Action a1, Action a2) -> a1.getOrder() - a2.getOrder());
        return actions;
    }

    @SpringBootApplication
    public static class DemoApplication {

    }

}
