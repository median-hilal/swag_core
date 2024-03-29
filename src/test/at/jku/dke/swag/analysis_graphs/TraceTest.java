package at.jku.dke.swag.analysis_graphs;

import at.jku.dke.swag.AppConstants;
import at.jku.dke.swag.analysis_graphs.asm_elements.Location;
import at.jku.dke.swag.analysis_graphs.basic_elements.*;
import at.jku.dke.swag.analysis_graphs.operations.AddParamDimPredicateNonSemanticsPreserving;
import at.jku.dke.swag.analysis_graphs.operations.Operation;
import at.jku.dke.swag.analysis_graphs.operations.operation_types.dice_ops.MoveToLevelAndNode_1;
import at.jku.dke.swag.analysis_graphs.operations.operation_types.gran_ops.DrillDownTo;
import at.jku.dke.swag.analysis_graphs.operations.operation_types.selection_ops.AddParamDimPredicate;
import at.jku.dke.swag.analysis_graphs.operations.operation_types.selection_ops.AddParamResultPredicate;
import at.jku.dke.swag.analysis_graphs.utils.Utils;
import at.jku.dke.swag.md_elements.Dimension;
import at.jku.dke.swag.md_elements.Level;
import at.jku.dke.swag.md_elements.LevelMember;
import at.jku.dke.swag.md_elements.MDGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TraceTest {

    SWAG swag = SwagInit.initSwag();
    MDGraph mdGraph = swag.getMdGraph();
    List<AnalysisSituation> situations = swag.getSituations();
    List<Step> steps = swag.getSteps();

    public static Set<Operation> initOperations2_3Altered() {

        Operation op1 = new Operation(DrillDownTo.getInstance(),
                List.of(new Dimension("timeDim"),
                        new Level("year")));

        Operation op2 = new Operation(MoveToLevelAndNode_1.getInstance(),
                List.of(new Dimension("destinationDim"),
                        new Level("geo"),
                        new Parameter("geoNode"),
                        LevelMember.unknown()));

        Operation op3 = new Operation(AddParamDimPredicateNonSemanticsPreserving.getInstance(),
                List.of(new Dimension("timeDim"),
                        new Parameter("dPred"),
                        ConstantOrUnknown.unknown));

        return Set.of(op1, op2, op3);
    }

    @Test
    @DisplayName("When a trace is executed, the results match the expected ones. Chapter#7 Example#3.")
    void traceExecutionResultsAsExpected() {

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.GERMANY));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        OperationBinding bindings3_4a = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings3_4b = OperationBinding.create().setBindings(Map.of(2, AppConstants.INTENSITY_GT_30K));

        Trace trace = new Trace(initialSituation,
                initialAsBindings,
                List.of(steps.get(1), steps.get(2)),
                List.of(
                        Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                                steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                                steps.get(1).getOperationOfType(AddParamDimPredicate.class), bindings2_3c),
                        Map.of(steps.get(2).getOperationOfType(DrillDownTo.class), bindings3_4a,
                                steps.get(2).getOperationOfType(AddParamResultPredicate.class), bindings3_4b)
                )
        );

        List<AnalysisSituation> res = Utils.executeTrace(trace);

        Assertions.assertEquals(createAs2Prime(), res.get(0));
        Assertions.assertEquals(createAs3Prime(), res.get(1));
        Assertions.assertEquals(createAs4Prime(), res.get(2));
    }

    @Test
    @DisplayName("When a trace is executed, the resulting situations match the expected ones.")
    void semanticsPreservingTraceResultsAsExpected() {

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.GERMANY));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        OperationBinding bindings3_4a = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings3_4b = OperationBinding.create().setBindings(Map.of(2, AppConstants.INTENSITY_GT_30K));

        Trace trace = new Trace(initialSituation,
                initialAsBindings,
                List.of(steps.get(1), steps.get(2)),
                List.of(
                        Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                                steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                                steps.get(1).getOperationOfType(AddParamDimPredicate.class), bindings2_3c),
                        Map.of(steps.get(2).getOperationOfType(DrillDownTo.class), bindings3_4a,
                                steps.get(2).getOperationOfType(AddParamResultPredicate.class), bindings3_4b)
                )
        );

        List<AnalysisSituation> res = Utils.executeTrace(trace);
        Assertions.assertEquals(createAs3Prime(), res.get(1));
        Assertions.assertEquals(createAs4Prime(), res.get(2));
    }

    @Test
    @DisplayName("When a trace that is not semantic-preserving is executed, it fails.")
    void nonSemanticsPreservingTraceFails() {

        Set<Operation> operations2_3 = initOperations2_3Altered();
        Step step2_3 = new Step(situations.get(1), situations.get(2), operations2_3);
        steps.set(1, step2_3);

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.GERMANY));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        OperationBinding bindings3_4a = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings3_4b = OperationBinding.create().setBindings(Map.of(2, AppConstants.INTENSITY_GT_30K));

        Assertions.assertThrows(RuntimeException.class, () -> {
            Trace trace = new Trace(initialSituation,
                    initialAsBindings,
                    List.of(steps.get(1), steps.get(2)),
                    List.of(
                            Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                                    steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                                    steps.get(1).getOperationOfType(AddParamDimPredicateNonSemanticsPreserving.class), bindings2_3c),
                            Map.of(steps.get(2).getOperationOfType(DrillDownTo.class), bindings3_4a,
                                    steps.get(2).getOperationOfType(AddParamResultPredicate.class), bindings3_4b)
                    )
            );
        });
    }

    @Test
    @DisplayName("When a trace is executed, the resulting situations can be alternatively obtained by binding the SWAG situations (the semantics-preservation property)")
    void semanticsPreservingTraceResultsCanBeCreatedByBinding() {

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.GERMANY));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        OperationBinding bindings3_4a = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings3_4b = OperationBinding.create().setBindings(Map.of(2, AppConstants.INTENSITY_GT_30K));

        Trace trace = new Trace(initialSituation,
                initialAsBindings,
                List.of(steps.get(1), steps.get(2)),
                List.of(
                        Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                                steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                                steps.get(1).getOperationOfType(AddParamDimPredicate.class), bindings2_3c),
                        Map.of(steps.get(2).getOperationOfType(DrillDownTo.class), bindings3_4a,
                                steps.get(2).getOperationOfType(AddParamResultPredicate.class), bindings3_4b)
                )
        );

        List<AnalysisSituation> res = Utils.executeTrace(trace);


        Assertions.assertEquals(createAs3PrimeByBinding(), res.get(1));
        Assertions.assertEquals(createAs4PrimeByBinding(), res.get(2));
    }

    @Test
    @DisplayName("When a trace that does not preserve semantics is executed, an exception is thrown.")
    void nonSemanticsPreservingTrace() {

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.EU));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        Assertions.assertThrows(Exception.class, () -> {
            Trace trace = new Trace(initialSituation,
                    initialAsBindings,
                    List.of(steps.get(1), steps.get(2)),
                    List.of(
                            Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                                    steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                                    steps.get(1).getOperationOfType(AddParamDimPredicate.class), bindings2_3c)
                    )
            );
        });
    }

    @Test
    @DisplayName("Test strategy 1 success for semantics preservation during actual analysis")
    void testTraceStrategy1Success() {

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.GERMANY));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        OperationBinding bindings3_4a = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings3_4b = OperationBinding.create().setBindings(Map.of(2, AppConstants.INTENSITY_GT_30K));

        Assertions.assertDoesNotThrow(() -> {
            Trace trace = Trace.initStrategyOneTrace(initialSituation,
                    initialAsBindings,
                    List.of(steps.get(1), steps.get(2))
            );
            trace.addStepBinding(steps.get(1), Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                    steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                    steps.get(1).getOperationOfType(AddParamDimPredicate.class), bindings2_3c)
            );
            trace.addStepBinding(steps.get(2),
                    Map.of(steps.get(2).getOperationOfType(DrillDownTo.class), bindings3_4a,
                            steps.get(2).getOperationOfType(AddParamResultPredicate.class), bindings3_4b)
            );
        });
    }

    @Test
    @DisplayName("Test strategy 1 fail for semantics preservation during actual analysis")
    void testTraceStrategy1Fail() {

        Set<Operation> operations2_3 = initOperations2_3Altered();
        Step step2_3 = new Step(situations.get(1), situations.get(2), operations2_3);
        steps.set(1, step2_3);

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.GERMANY));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        OperationBinding bindings3_4a = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings3_4b = OperationBinding.create().setBindings(Map.of(2, AppConstants.INTENSITY_GT_30K));

        Assertions.assertThrows(RuntimeException.class, () -> {
            Trace trace = Trace.initStrategyOneTrace(initialSituation,
                    initialAsBindings,
                    List.of(steps.get(1), steps.get(2))
            );
            trace.addStepBinding(steps.get(1), Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                    steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                    steps.get(1).getOperationOfType(AddParamDimPredicateNonSemanticsPreserving.class), bindings2_3c)
            );
            trace.addStepBinding(steps.get(2),
                    Map.of(steps.get(2).getOperationOfType(DrillDownTo.class), bindings3_4a,
                            steps.get(2).getOperationOfType(AddParamResultPredicate.class), bindings3_4b)
            );
        });
    }

    @Test
    @DisplayName("Test strategy 2 success for semantics preservation during actual analysis")
    void testTraceStrategy2Success() {

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.GERMANY));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        OperationBinding bindings3_4a = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings3_4b = OperationBinding.create().setBindings(Map.of(2, AppConstants.INTENSITY_GT_30K));

        Assertions.assertDoesNotThrow(() -> {
            Trace trace = Trace.initStrategyTwoTrace(initialSituation,
                    initialAsBindings
            );
            trace.addStepAndBinding(steps.get(1), Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                    steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                    steps.get(1).getOperationOfType(AddParamDimPredicate.class), bindings2_3c)
            );
            trace.addStepAndBinding(steps.get(2),
                    Map.of(steps.get(2).getOperationOfType(DrillDownTo.class), bindings3_4a,
                            steps.get(2).getOperationOfType(AddParamResultPredicate.class), bindings3_4b)
            );
        });
    }

    @Test
    @DisplayName("Test strategy 2 fail for semantics preservation during actual analysis")
    void testTraceStrategy2Fail() {

        Set<Operation> operations2_3 = initOperations2_3Altered();
        Step step2_3 = new Step(situations.get(1), situations.get(2), operations2_3);
        steps.set(1, step2_3);

        AnalysisSituation initialSituation = situations.get(1);
        SituationBinding initialAsBindings = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(Map.of(AppConstants.CONTINENT_NODE, AppConstants.EU));

        OperationBinding bindings2_3a = OperationBinding.create().setBindings(Map.of(4, AppConstants.GERMANY));
        OperationBinding bindings2_3b = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings2_3c = OperationBinding.create().setBindings(Map.of(3, AppConstants.YEAR_AFTER_2010));

        OperationBinding bindings3_4a = OperationBinding.create().setBindings(Map.of());
        OperationBinding bindings3_4b = OperationBinding.create().setBindings(Map.of(2, AppConstants.INTENSITY_GT_30K));

        Assertions.assertThrows(RuntimeException.class, () -> {
            Trace trace = Trace.initStrategyTwoTrace(initialSituation,
                    initialAsBindings
            );
            trace.addStepAndBinding(steps.get(1), Map.of(steps.get(1).getOperationOfType(MoveToLevelAndNode_1.class), bindings2_3a,
                    steps.get(1).getOperationOfType(DrillDownTo.class), bindings2_3b,
                    steps.get(1).getOperationOfType(AddParamDimPredicateNonSemanticsPreserving.class), bindings2_3c)
            );
            trace.addStepAndBinding(steps.get(2),
                    Map.of(steps.get(2).getOperationOfType(DrillDownTo.class), bindings3_4a,
                            steps.get(2).getOperationOfType(AddParamResultPredicate.class), bindings3_4b)
            );
        });
    }

    public AnalysisSituation createAs2Prime() {

        AnalysisSituation as = new AnalysisSituation(mdGraph);

        BindableSet measures = new BindableSet();
        measures.union(mdGraph.getM().stream().findAny().get());
        as.setMeasures(measures);

        as.setGran(AppConstants.DESTINATION_DIM, AppConstants.GEO);

        as.setDiceLevel(AppConstants.DESTINATION_DIM, AppConstants.CONTINENT);
        as.setDiceNode(AppConstants.DESTINATION_DIM, new Pair(AppConstants.CONTINENT_NODE, AppConstants.EU));

        return as;
    }

    public AnalysisSituation createAs3Prime() {

        AnalysisSituation as = new AnalysisSituation(mdGraph);

        BindableSet measures = new BindableSet();
        measures.union(mdGraph.getM().stream().findAny().get());
        as.setMeasures(measures);

        as.setGran(AppConstants.DESTINATION_DIM, AppConstants.GEO);
        as.setGran(AppConstants.TIME_DIM, AppConstants.YEAR);

        as.setDiceLevel(AppConstants.DESTINATION_DIM, AppConstants.GEO);
        as.setDiceNode(AppConstants.DESTINATION_DIM, new Pair(AppConstants.GEO_NODE, AppConstants.GERMANY));

        BindableSet selections = new BindableSet();
        selections.union(new Pair(AppConstants.D_PRED, AppConstants.YEAR_AFTER_2010));
        as.setDimensionSelection(AppConstants.TIME_DIM, selections);

        return as;
    }

    public AnalysisSituation createAs4Prime() {
        AnalysisSituation as = new AnalysisSituation(mdGraph);

        BindableSet measures = new BindableSet();
        measures.union(mdGraph.getM().stream().findAny().get());
        as.setMeasures(measures);

        as.setGran(AppConstants.DESTINATION_DIM, AppConstants.GEO);
        as.setGran(AppConstants.TIME_DIM, AppConstants.REF_PERIOD);

        as.setDiceLevel(AppConstants.DESTINATION_DIM, AppConstants.GEO);
        as.setDiceNode(AppConstants.DESTINATION_DIM, new Pair(AppConstants.GEO_NODE, AppConstants.GERMANY));

        BindableSet selections = new BindableSet();
        selections.union(new Pair(AppConstants.D_PRED, AppConstants.YEAR_AFTER_2010));
        as.setDimensionSelection(AppConstants.TIME_DIM, selections);

        BindableSet filters = new BindableSet();
        filters.union(new Pair(AppConstants.M_PRED, AppConstants.INTENSITY_GT_30K));
        as.setResultFilter(filters);

        return as;
    }


    /**
     * Create AS3' by binding AS3
     *
     * @return AS3'
     */
    public AnalysisSituation createAs3PrimeByBinding() {

        AnalysisSituation as = SwagInit.createAs3();

        SituationBinding binding1 = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(
                        Map.of(AppConstants.GEO_NODE,
                                AppConstants.GERMANY));

        SituationBinding binding2 = SituationBinding
                .create(Location.selectoinOf(AppConstants.TIME_DIM))
                .setBindings(
                        Map.of(AppConstants.D_PRED,
                                AppConstants.YEAR_AFTER_2010));

        Set<SituationBinding> bindings = Set.of(binding1, binding2);
        return Utils.bind(as, bindings);
    }

    /**
     * Create AS4' by binding AS4
     *
     * @return AS4'
     */
    public AnalysisSituation createAs4PrimeByBinding() {

        AnalysisSituation as = SwagInit.createAs4();

        SituationBinding binding1 = SituationBinding
                .create(Location.diceNodeOf(AppConstants.DESTINATION_DIM))
                .setBindings(
                        Map.of(AppConstants.GEO_NODE,
                                AppConstants.GERMANY));

        SituationBinding binding2 = SituationBinding
                .create(Location.selectoinOf(AppConstants.TIME_DIM))
                .setBindings(
                        Map.of(AppConstants.D_PRED,
                                AppConstants.YEAR_AFTER_2010));

        SituationBinding binding3 = SituationBinding
                .create(Location.resultFilter())
                .setBindings(
                        Map.of(AppConstants.M_PRED,
                                AppConstants.INTENSITY_GT_30K));

        Set<SituationBinding> bindings = Set.of(binding1, binding2, binding3);
        return Utils.bind(as, bindings);
    }

}
