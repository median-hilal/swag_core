package at.jku.dke.swag.analysis_graphs.operations.operation_types.dice_ops;

import at.jku.dke.swag.analysis_graphs.AnalysisSituation;
import at.jku.dke.swag.analysis_graphs.asm_elements.Location;
import at.jku.dke.swag.analysis_graphs.asm_elements.LocationValue;
import at.jku.dke.swag.analysis_graphs.asm_elements.Update;
import at.jku.dke.swag.analysis_graphs.basic_elements.ConstantOrUnknown;
import at.jku.dke.swag.analysis_graphs.basic_elements.Pair;
import at.jku.dke.swag.analysis_graphs.operations.OperationTypes;
import at.jku.dke.swag.analysis_graphs.utils.Utils;
import at.jku.dke.swag.md_elements.Dimension;
import at.jku.dke.swag.md_elements.Level;
import at.jku.dke.swag.md_elements.LevelMember;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoveToNextNode extends OperationTypes {

    private static final MoveToNextNode instance = new MoveToNextNode(Collections.emptyList());

    public MoveToNextNode(List<Object> params) {
        super(params);
    }

    public static OperationTypes getInstance() {
        return instance;
    }

    @Override
    public Set<Update> updSet(AnalysisSituation situation, List<Object> params) {

        Set<Update> updates = new HashSet<>();

        Dimension param0 = (Dimension) params.get(0);
        ConstantOrUnknown actualDiceLevel = Utils.actual(situation.getDiceLevels().get(param0));
        ConstantOrUnknown actualDiceNode = Utils.actual(situation.getDiceNodes().get(param0));

        LevelMember nextMember = null;

        if (!actualDiceLevel.isUnknown()) {
            nextMember = mdGraph.nextMember((Level) actualDiceLevel, (LevelMember) actualDiceNode);
        }

        if (nextMember == null) {
            nextMember = new LevelMember("fake");
        }

        if (!actualDiceNode.isUnknown()
                && !actualDiceLevel.isUnknown()
                && !actualDiceNode.equals(situation.getMdGraph().lastMember((Level) actualDiceLevel))
                && situation.getDiceNodes().get(param0).isPair()
                && DiceUtils.isLegalDiceNodePair(situation, param0, nextMember)) {

            Pair newPair = (Pair) situation.getDiceNodes().get(param0).copy();
            newPair.setConstant(nextMember);
            updates.add(new Update(Location.diceNodeOf(param0), newPair));

        } else {
            if (!actualDiceNode.isUnknown()
                    && !actualDiceLevel.isUnknown()
                    && !actualDiceNode.equals(situation.getMdGraph().lastMember((Level) actualDiceLevel))
                    && situation.getDiceNodes().get(param0).isConstant()
                    && !actualDiceNode.equals(nextMember)
                    && DiceUtils.isLegalDiceNode(situation, param0, nextMember)) {

                updates.add(new Update(Location.diceNodeOf(param0), nextMember));
            }
        }

        if (actualDiceNode.isUnknown() || actualDiceLevel.isUnknown()) {
            updates.add(new Update(
                    Location.diceNodeOf(param0),
                    (LocationValue) situation.getDiceNodes().get(param0).copy()));
        }

        return updates;
    }
}
