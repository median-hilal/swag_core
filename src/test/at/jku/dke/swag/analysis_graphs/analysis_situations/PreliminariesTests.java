package at.jku.dke.swag.analysis_graphs.analysis_situations;

import at.jku.dke.swag.AppConstants;
import at.jku.dke.swag.analysis_graphs.basic_elements.*;
import at.jku.dke.swag.analysis_graphs.utils.Utils;
import at.jku.dke.swag.md_elements.MDGraph;
import at.jku.dke.swag.md_elements.init.MDGraphInit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PreliminariesTests {

    MDGraph mdGraph = MDGraphInit.initMDGraph();

    @Nested
    @DisplayName("When auxiliary functions are called")
    class AuxiliaryFunctionsTest {

        BindableSet set = new BindableSet(
                Set.of(new Pair(AppConstants.D_PRED, Constant.unknown())));

        @Test
        @DisplayName("Unbound function works as expected")
        void testUnbound() {
            Assertions.assertEquals(Set.of(AppConstants.D_PRED), Utils.unbound(set));
        }

        @Test
        @DisplayName("Actual function works as expected")
        void testActual() {
            Assertions.assertEquals(Constant.unknown(),
                    Utils.actual(new Pair(AppConstants.D_PRED, Constant.unknown())));
        }
    }

    @Nested
    class BindableSetTest {

        Map<Parameter, Set<Constant>> domain =
                Map.of(
                        AppConstants.D_PRED, Set.of(AppConstants.YEAR_AFTER_2010),
                        AppConstants.D_PRED_2, Set.of(AppConstants.YEAR_AFTER_2015)
                );
        Set<Parameter> P_TEST =
                Set.of(
                        AppConstants.D_PRED,
                        AppConstants.D_PRED_2
                );

        Set<Constant> C_TEST =
                Set.of(
                        AppConstants.YEAR_AFTER_2010,
                        AppConstants.YEAR_AFTER_2015
                );

        BindableSet set1 = new BindableSet(Set.of());
        BindableSet set2 = new BindableSet(Set.of(AppConstants.YEAR_AFTER_2010));
        BindableSet set3 = new BindableSet(Set.of(new Pair(AppConstants.D_PRED, Constant.unknown())));
        BindableSet set4 = new BindableSet(Set.of(AppConstants.YEAR_AFTER_2010, new Pair(AppConstants.D_PRED, Constant.unknown())));
        BindableSet set5 = new BindableSet(Set.of(new Pair(AppConstants.D_PRED, AppConstants.YEAR_AFTER_2010)));
        BindableSet set6 = new BindableSet(Set.of(AppConstants.YEAR_AFTER_2010, new Pair(AppConstants.D_PRED, AppConstants.YEAR_AFTER_2010)));
        Set<BindableSet> S = Set.of(set1, set2, set3, set4, set5, set6);

        @Test
        @DisplayName("Bindable set generation from parameters and constants works as expected")
        void testCorrectBindableSetGeneration() {
            Set<BindableSet> allGenerated = Utils.getAllBindableSetsOver(
                    Set.of(AppConstants.D_PRED),
                    Set.of(AppConstants.YEAR_AFTER_2010));

            Assertions.assertEquals(S, allGenerated);
        }

        @Test
        @DisplayName("Bindable set restriction using a set of constants works as expected")
        void testCorrectBindableSetRestriction1() {
            Set<BindableSet> allGenerated = Utils.restrictBindableSetsTo(
                    Utils.getAllBindableSetsOver(P_TEST, C_TEST),
                    P_TEST,
                    Set.of(AppConstants.YEAR_AFTER_2010),
                    domain);
            Assertions.assertEquals(S, allGenerated);
        }

        @Test
        @DisplayName("Bindable set restriction using a set of constants works as expected")
        void testCorrectParameterOrConstantRestriction1() {
            Set<Constant> constsToRestrictTo = Set.of(
                    AppConstants.AUSTRIA,
                    AppConstants.GERMANY,
                    AppConstants.UK
            );
            Set<Constant> reducedConstants = Set.of(
                    AppConstants.AUSTRIA,
                    AppConstants.GERMANY,
                    AppConstants.UK,
                    AppConstants.EU,
                    AppConstants.GEO
            );
            Set<Parameter> reducedParameters = Set.of(
                    AppConstants.GEO_NODE,
                    AppConstants.DICE_PARAM,
                    AppConstants.CONTINENT_NODE,
                    AppConstants.GRAN_PARAM
            );
            Set<PairOrConstant> allGenerated = Utils.restrictPairsAndConstantsTo(
                    reducedParameters,
                    reducedConstants,
                    constsToRestrictTo,
                    mdGraph.getDOM());

            allGenerated.forEach(x -> {
                Set.of(AppConstants.CONTINENT_NODE,
                        AppConstants.GRAN_PARAM).forEach(p -> {
                    if (x.isPair()) {
                        Assertions.assertNotEquals(((Pair) x).getParameter(), p);
                    }
                });
                Set.of(AppConstants.EU,
                        AppConstants.GEO).forEach(c -> {
                    if (x.isPair()) {
                        Assertions.assertNotEquals(((Pair) x).getConstant(), c);
                    } else {
                        Assertions.assertNotEquals(((Constant) x), c);
                    }
                });
            });
        }

        @Test
        @DisplayName("Bindable set restriction using a set of constants works as expected")
        void testCorrectBindableSetRestriction2() {
            Set<Constant> constsToRestrictTo = Set.of(
                    AppConstants.AUSTRIA,
                    AppConstants.GERMANY,
                    AppConstants.UK,
                    AppConstants.EU,
                    AppConstants.GEO
            );
            Set<Constant> reducedConstants = Set.of(
                    AppConstants.AUSTRIA,
                    AppConstants.GERMANY,
                    AppConstants.UK,
                    AppConstants.EU,
                    AppConstants.GEO
            );
            Set<Parameter> reducedParameters = Set.of(
                    AppConstants.GEO_NODE,
                    AppConstants.DICE_PARAM,
                    AppConstants.CONTINENT_NODE,
                    AppConstants.GRAN_PARAM
            );
            Set<BindableSet> allGenerated = Utils.restrictBindableSetsTo(
                    Utils.getAllBindableSetsOver(reducedParameters, reducedConstants),
                    reducedParameters,
                    constsToRestrictTo,
                    mdGraph.getDOM());

            allGenerated.forEach(x -> {
                Set.of(AppConstants.CONTINENT_NODE,
                        AppConstants.GRAN_PARAM).forEach(p -> {
                    Assertions.assertFalse(x.paras().contains(p));
                });
                Set.of(AppConstants.EU,
                        AppConstants.GEO).forEach(c -> {
                    Assertions.assertFalse(x.consts().contains(c));
                });
            });
        }

        @Test
        @DisplayName("Parameter restriction using a set of constants works as expected")
        void testCorrectParameterRestriction1() {
            Set<Constant> constants = mdGraph.getMembersOf(
                            AppConstants.GEO)
                    .stream()
                    .map(m -> (Constant) m)
                    .collect(Collectors.toSet());
            Set<Parameter> parasRestricted = Utils.restrictParamsTo(mdGraph.getP(), constants, mdGraph.getDOM());
            Set<Parameter> paras = Set.of(
                    AppConstants.GEO_NODE_1,
                    AppConstants.GEO_NODE,
                    AppConstants.DICE_PARAM,
                    AppConstants.DICE_PARAM_1
            );
            Assertions.assertEquals(paras, parasRestricted);
        }

        @Test
        @DisplayName("Parameter restriction using a set of constants works as expected")
        void testCorrectParameterRestriction2() {
            Set<Parameter> parasRestricted = Utils.restrictParamsTo(mdGraph.getP(), mdGraph.getC(), mdGraph.getDOM());
            Assertions.assertEquals(mdGraph.getP(), parasRestricted);
        }
    }
}
