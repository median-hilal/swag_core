package at.jku.dke.swag.analysis_graphs.operations.operation_types.gran_ops;


import at.jku.dke.swag.AppConstants;
import at.jku.dke.swag.analysis_graphs.AnalysisSituation;
import at.jku.dke.swag.analysis_graphs.basic_elements.Pair;
import at.jku.dke.swag.analysis_graphs.operations.Operation;
import at.jku.dke.swag.analysis_graphs.utils.Utils;
import at.jku.dke.swag.md_elements.Level;
import at.jku.dke.swag.md_elements.MDGraph;
import at.jku.dke.swag.md_elements.init.MDGraphInit;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;

public class DrillDownToTest {

    MDGraph mdGraph;
    AnalysisSituation source;
    AnalysisSituation target;

    AnalysisSituation opTarget;
    Set<Operation> ops;

    @BeforeEach
    void initMDGraph() {
        this.mdGraph = MDGraphInit.initMDGraph();
    }

    @Nested
    @DisplayName("When actual granularity is constant and drillable to the parameter")
    class ActualGranIsConstantAndDrillable {

        @Test
        @DisplayName("When actual granularity is constant and drillable to the parameter")
        void added1() {
            source = createSource();
            target = createTarget();
            ops = initOperations();
            opTarget = Utils.evaluateAndFire(source, ops);
            Assertions.assertFalse(Utils.evaluate(source, ops).isEmpty());
            Assertions.assertEquals(opTarget, target);
        }

        public Set<Operation> initOperations() {
            Operation op3 = new Operation(DrillDownTo.getInstance(),
                    List.of(AppConstants.DESTINATION_DIM, AppConstants.GEO));
            return Set.of(op3);
        }

        public AnalysisSituation createSource() {
            AnalysisSituation as = new AnalysisSituation(mdGraph);
            as.setGran(AppConstants.DESTINATION_DIM, AppConstants.CONTINENT);
            return as;
        }

        public AnalysisSituation createTarget() {
            AnalysisSituation as = new AnalysisSituation(mdGraph);
            as.setGran(AppConstants.DESTINATION_DIM, AppConstants.GEO);
            return as;
        }
    }

    @Nested
    @DisplayName("When actual granularity is constant not drillable to the parameter")
    class ActualGranIsConstantAndNotDrillable {

        @Test
        @DisplayName("When actual granularity is constant and not drillable to the parameter")
        void added1() {
            source = createSource();
            target = createSource();
            ops = initOperations();
            opTarget = Utils.evaluateAndFire(source, ops);
            Assertions.assertTrue(Utils.evaluate(source, ops).isEmpty());
            Assertions.assertEquals(opTarget, target);
        }

        public Set<Operation> initOperations() {
            Operation op3 = new Operation(DrillDownTo.getInstance(),
                    List.of(AppConstants.DESTINATION_DIM, AppConstants.GEO));
            return Set.of(op3);
        }

        public AnalysisSituation createSource() {
            AnalysisSituation as = new AnalysisSituation(mdGraph);
            as.setGran(AppConstants.DESTINATION_DIM, AppConstants.GEO);
            return as;
        }
    }

    @Nested
    @DisplayName("When actual granularity is bound pair and drillable to the parameter")
    class ActualGranIsBoundParAndNotBottom {

        @Test
        @DisplayName("When actual granularity is bound pair and drillable to the parameter")
        void added1() {
            source = createSource();
            target = createTarget();
            ops = initOperations();
            opTarget = Utils.evaluateAndFire(source, ops);
            Assertions.assertFalse(Utils.evaluate(source, ops).isEmpty());
            Assertions.assertEquals(opTarget, target);
        }

        public Set<Operation> initOperations() {
            Operation op3 = new Operation(DrillDownTo.getInstance(),
                    List.of(AppConstants.DESTINATION_DIM, AppConstants.GEO));
            return Set.of(op3);
        }

        public AnalysisSituation createSource() {
            AnalysisSituation as = new AnalysisSituation(mdGraph);
            as.setGran(AppConstants.DESTINATION_DIM, new Pair(AppConstants.GRAN_PARAM, AppConstants.CONTINENT));
            return as;
        }

        public AnalysisSituation createTarget() {
            AnalysisSituation as = new AnalysisSituation(mdGraph);
            as.setGran(AppConstants.DESTINATION_DIM, new Pair(AppConstants.GRAN_PARAM, AppConstants.GEO));
            return as;
        }
    }

    @Nested
    @DisplayName("When actual granularity is bound pair and drillable")
    class ActualGranIsBoundParAndNotDrillable {

        @Test
        @DisplayName("When actual granularity is bound pair and Drillable")
        void added1() {
            source = createSource();
            target = createTarget();
            ops = initOperations();
            opTarget = Utils.evaluateAndFire(source, ops);
            Assertions.assertFalse(Utils.evaluate(source, ops).isEmpty());
            Assertions.assertEquals(opTarget, target);
        }

        public Set<Operation> initOperations() {
            Operation op3 = new Operation(DrillDownTo.getInstance(),
                    List.of(AppConstants.DESTINATION_DIM, AppConstants.GEO));
            return Set.of(op3);
        }

        public AnalysisSituation createSource() {
            AnalysisSituation as = new AnalysisSituation(mdGraph);
            as.setGran(AppConstants.DESTINATION_DIM, new Pair(AppConstants.GRAN_PARAM, AppConstants.CONTINENT));
            return as;
        }

        public AnalysisSituation createTarget() {
            AnalysisSituation as = new AnalysisSituation(mdGraph);
            as.setGran(AppConstants.DESTINATION_DIM, new Pair(AppConstants.GRAN_PARAM, AppConstants.GEO));
            return as;
        }

    }

    @Nested
    @DisplayName("When actual granularity is unbound pair")
    class ActualGranIsUnBoundParAndNotBottom {

        @Test
        @DisplayName("When actual granularity is unbound pair")
        void added1() {
            source = createSource();
            target = createSource();
            ops = initOperations();
            opTarget = Utils.evaluateAndFire(source, ops);
            Assertions.assertTrue(Utils.evaluate(source, ops).isEmpty());
            Assertions.assertEquals(opTarget, target);
        }

        public Set<Operation> initOperations() {
            Operation op3 = new Operation(DrillDownTo.getInstance(),
                    List.of(AppConstants.DESTINATION_DIM, AppConstants.GEO));
            return Set.of(op3);
        }

        public AnalysisSituation createSource() {
            AnalysisSituation as = new AnalysisSituation(mdGraph);
            as.setGran(AppConstants.DESTINATION_DIM, new Pair(AppConstants.GRAN_PARAM, Level.unknown()));
            return as;
        }
    }
}
