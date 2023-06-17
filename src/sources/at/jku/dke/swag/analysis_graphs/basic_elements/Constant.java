package at.jku.dke.swag.analysis_graphs.basic_elements;

public class Constant extends ConstantOrUnknown {
    public Constant(String uri) {
        super(uri);
    }

    public static Constant unknown() {
        return new Constant(ConstantOrUnknown.unknown.getUri());
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean isUnknown() {
        return this.getUri().equals(ConstantOrUnknown.unknown.getUri());
    }

}
