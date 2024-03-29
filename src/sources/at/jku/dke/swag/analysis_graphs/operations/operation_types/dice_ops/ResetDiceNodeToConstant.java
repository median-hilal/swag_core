package at.jku.dke.swag.analysis_graphs.operations.operation_types.dice_ops;

import at.jku.dke.swag.analysis_graphs.AnalysisSituation;
import at.jku.dke.swag.analysis_graphs.asm_elements.Location;
import at.jku.dke.swag.analysis_graphs.asm_elements.Update;
import at.jku.dke.swag.analysis_graphs.operations.OperationTypes;
import at.jku.dke.swag.md_elements.Dimension;
import at.jku.dke.swag.md_elements.LevelMember;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResetDiceNodeToConstant extends OperationTypes {

    private static final ResetDiceNodeToConstant instance = new ResetDiceNodeToConstant(Collections.emptyList());

    public ResetDiceNodeToConstant(List<Object> params) {
        super(params);
    }

    public static OperationTypes getInstance() {
        return instance;
    }

    @Override
    public Set<Update> updSet(AnalysisSituation situation, List<Object> params) {
        Set<Update> updates = new HashSet<>();

        Dimension param0 = (Dimension) params.get(0);
        LevelMember member = (LevelMember) params.get(1);

        if (DiceUtils.isLegalDiceNode(situation, param0, member)
                && !member.equals(situation.getDiceNodes().get(param0))) {
            updates.add(new Update(
                    Location.diceNodeOf(param0),
                    member)
            );
        }

        return updates;
    }
}
