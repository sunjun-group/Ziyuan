package tzuyu.plugin.core.constant;


public enum JElementType {
	JAVA_MODEL,
	JAVA_PROJECT,
	PACKAGE_FRAGMENT_ROOT,
	PACKAGE_FRAGMENT,
	COMPILATION_UNIT,
	CLASS_FILE,
	TYPE,
	FIELD,
	METHOD,
	INITIALIZER,
	PACKAGE_DECLARATION,
	IMPORT_CONTAINER,
	IMPORT_DECLARATION,
	LOCAL_VARIABLE,
	TYPE_PARAMETER,
	ANNOTATION;
	
	public int getIJeType() {
		return this.ordinal() + 1;
	}
}
