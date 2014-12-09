package gentest.data.statement;

import sav.common.core.utils.Randomness;

public class RArrayConstructor extends Statement {
	// NOTE This must be less than 255
	private static final int GENERATED_ARRAY_MAX_LENGTH = 32;

	private int[] sizes;
	private Class<?> outputType; // Type of the array itself
	private Class<?> contentType; // Type of the array content

	public RArrayConstructor(final int dimension, final Class<?> contentType) {
		super(RStatementKind.ARRAY_CONSTRUCTOR);
		this.outputType = contentType;
		this.contentType = contentType;
		while (this.contentType.isArray()) {
			this.contentType = this.contentType.getComponentType();
		}
		this.sizes = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			this.sizes[i] = Randomness.nextRandomInt(GENERATED_ARRAY_MAX_LENGTH);
		}
	}

	@Override
	public boolean hasOutputVar() {
		return true;
	}

	@Override
	public void accept(StatementVisitor visitor) throws Throwable {
		visitor.visit(this);
	}

	public Class<?> getOutputType() {
		return outputType;
	}

	public Class<?> getContentType() {
		return contentType;
	}

	public int[] getSizes() {
		return sizes;
	}

}
