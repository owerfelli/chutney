package com.chutneytesting.engine.domain.execution.engine.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.engine.domain.execution.engine.scenario.ScenarioContextImpl;
import com.chutneytesting.engine.domain.execution.evaluation.SpelFunctions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(JUnitParamsRunner.class)
public class StepDataEvaluatorTest {

    private StepDataEvaluator evaluator = new StepDataEvaluator(new SpelFunctions());

    @Test
    public void testInputDataEvaluator() throws Exception {
        TestObject testObject = new TestObject("attributeValue");

        Map<String, Object> context = new HashMap<>();
        context.put("destination", "stringDestination");
        context.put("jsonSringVariable", "{\"key\": \"value\"}");
        context.put("object", testObject);

        ScenarioContextImpl scenarioContext = new ScenarioContextImpl();
        scenarioContext.putAll(context);

        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("dateTimeFormat", "ss");
        innerMap.put("dateTimeFormatRef", "${#dateTimeFormat}");

        Map<String, Object> map = new HashMap<>();
        map.put("stringRawValue", "rawValue");
        map.put("objectRawValue", testObject);
        map.put("destination", "${#destination}");
        map.put("other destination", "other ${#destination}");
        map.put("${#destination}", "destination");
        map.put("jsonKey", "${#jsonSringVariable}");
        map.put("objectKey", "${#object}");
        map.put("${#object}", "objectValue");
        map.put("objectAttributeValue", "${#object.attribute()}");
        map.put("innerMap", innerMap);

        List<Object> list = new ArrayList<>();
        list.add("rawValue");
        list.add(testObject);
        list.add("${#destination}");
        list.add("other ${#destination}");
        list.add("${#jsonSringVariable}");
        list.add("${#object}");
        list.add("${#object.attribute()}");

        Set<Object> set = new HashSet<>();
        set.add("rawValue");
        set.add(testObject);
        set.add("${#destination}");
        set.add("other ${#destination}");
        set.add("${#jsonSringVariable}");
        set.add("${#object}");
        set.add("${#object.attribute()}");

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("stringRawValue", "rawValue");
        inputs.put("objectRawValue", testObject);
        inputs.put("destination", "${#destination}");
        inputs.put("jsonKey", "${#jsonSringVariable}");
        inputs.put("objectKey", "${#object}");
        inputs.put("inputReference", "${#objectKey}");
        inputs.put("objectAttributeValue", "${#object.attribute()}");
        inputs.put("map", map);
        inputs.put("list", list);
        inputs.put("set", set);

        Map<String, Object> evaluatedInputs = evaluator.evaluateNamedDataWithContextVariables(inputs, scenarioContext);

        assertThat(evaluatedInputs.get("stringRawValue")).isEqualTo("rawValue");
        assertThat(evaluatedInputs.get("objectRawValue")).isEqualTo(testObject);
        assertThat(evaluatedInputs.get("destination")).isEqualTo("stringDestination");
        assertThat(evaluatedInputs.get("jsonKey")).isEqualTo("{\"key\": \"value\"}");
        assertThat(evaluatedInputs.get("objectKey")).isEqualTo(testObject);
        assertThat(evaluatedInputs.get("inputReference")).isEqualTo(testObject);
        assertThat(evaluatedInputs.get("objectAttributeValue")).isEqualTo("attributeValue");

        assertThat(evaluatedInputs.get("map")).isInstanceOf(Map.class);
        Map evaluatedMap = (Map<String, Object>)evaluatedInputs.get("map");
        assertThat(evaluatedMap.get("stringRawValue")).isEqualTo("rawValue");
        assertThat(evaluatedMap.get("objectRawValue")).isEqualTo(testObject);
        assertThat(evaluatedMap.get("destination")).isEqualTo("stringDestination");
        assertThat(evaluatedMap.get("other destination")).isEqualTo("other stringDestination");
        assertThat(evaluatedMap.get("jsonKey")).isEqualTo("{\"key\": \"value\"}");
        assertThat(evaluatedMap.get("objectKey")).isEqualTo(testObject);
        assertThat(evaluatedMap.get(testObject)).isEqualTo("objectValue");
        assertThat(evaluatedMap.get("objectAttributeValue")).isEqualTo("attributeValue");

        assertThat(evaluatedMap.get("innerMap")).isInstanceOf(Map.class);
        Map evaluatedInnerMap = (Map<Object, Object>)evaluatedMap.get("innerMap");
        assertThat(evaluatedInnerMap.get("dateTimeFormat")).isEqualTo("ss");
        assertThat(evaluatedInnerMap.get("dateTimeFormatRef")).isEqualTo("ss");


        assertThat(evaluatedInputs.get("list")).isInstanceOf(List.class);
        List evaluatedList = (List<Object>)evaluatedInputs.get("list");
        assertThat(evaluatedList.get(0)).isEqualTo("rawValue");
        assertThat(evaluatedList.get(1)).isEqualTo(testObject);
        assertThat(evaluatedList.get(2)).isEqualTo("stringDestination");
        assertThat(evaluatedList.get(3)).isEqualTo("other stringDestination");
        assertThat(evaluatedList.get(4)).isEqualTo("{\"key\": \"value\"}");
        assertThat(evaluatedList.get(5)).isEqualTo(testObject);
        assertThat(evaluatedList.get(6)).isEqualTo("attributeValue");

        assertThat(evaluatedInputs.get("set")).isInstanceOf(Set.class);
        Set evaluatedSet = (Set<Object>)evaluatedInputs.get("set");
        assertThat(evaluatedSet).contains("rawValue");
        assertThat(evaluatedSet).contains(testObject);
        assertThat(evaluatedSet).contains("stringDestination");
        assertThat(evaluatedSet).contains("other stringDestination");
        assertThat(evaluatedSet).contains("{\"key\": \"value\"}");
        assertThat(evaluatedSet).contains(testObject);
        assertThat(evaluatedSet).contains("attributeValue");
    }

    @Test(expected = com.chutneytesting.engine.domain.execution.engine.evaluation.EvaluationException.class)
    @Parameters({
        "${T(java.lang.Runtime).getRuntime().exec(\"echo I_c4n_5cr3w_Y0ur_1if3\")}",
        "${\"\".getClass().forName(\"java.lang.Runtime\").getMethod(\"getRuntime\")}",
        "${T(com.chutneytesting.engine.domain.execution.engine.evaluation.StepDataEvaluator).getClass().forName('java.lang.Runtime').getRuntime()}",
        "${new java.lang.ProcessBuilder({\"whoami\"}).start()}"
    })
    public void should_prevent_malicious_use_of_spel(String magicSpel) {
        // Given
        ScenarioContextImpl scenarioContext = new ScenarioContextImpl();
        scenarioContext.putAll(new HashMap<>());

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("MaliciousInjection", magicSpel);

        // When
        evaluator.evaluateNamedDataWithContextVariables(inputs, scenarioContext);
    }

    @Test
    public void should_not_prevent_legit_use_of_spel() {
        // Given
        ScenarioContextImpl scenarioContext = new ScenarioContextImpl();
        scenarioContext.putAll(new HashMap<>());

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("dateTimeFormat", "ss");
        inputs.put("MaliciousInjection", "${T(java.time.format.DateTimeFormatter).ofPattern(#dateTimeFormat).format(T(java.time.ZonedDateTime).now().plusSeconds(5))}");

        // When
        evaluator.evaluateNamedDataWithContextVariables(inputs, scenarioContext);
    }

    private class TestObject {
        private String attribute;
        public TestObject(String attribute) {
            this.attribute = attribute;
        }
        public String attribute() {
            return attribute;
        }
    }
}
